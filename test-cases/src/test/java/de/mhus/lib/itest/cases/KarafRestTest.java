package de.mhus.lib.itest.cases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MThread;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.tests.TestCase;
import de.mhus.lib.tests.docker.AnsiLogFilter;
import de.mhus.lib.tests.docker.DockerScenario;
import de.mhus.lib.tests.docker.Karaf;
import de.mhus.lib.tests.docker.LogStream;
import kong.unirest.Config;
import kong.unirest.HttpRequest;
import kong.unirest.HttpRequestSummary;
import kong.unirest.HttpResponse;
import kong.unirest.Interceptor;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

@TestMethodOrder(OrderAnnotation.class)
public class KarafRestTest extends TestCase {

    private static DockerScenario scenario;
    private static MProperties prop;


    @Test
    @Order(1)
    public void testGetPublicAsNone() throws NotFoundException, InterruptedException, IOException, UnirestException {
        int port = scenario.get("karaf").getPortBinding(8181);
        String host = scenario.get("karaf").getExternalHost();
        String uri = "http://" + host + ":" + port + "/rest/public";
        System.out.println("Port: " + port);
        
        HttpResponse<JsonNode> res = Unirest.get(uri)
                .asJson();
        assertEquals(200, res.getStatus());
        JsonNode body = res.getBody();
        
        JSONObject obj = body.getObject();
        System.out.println(obj);
        assertFalse(obj.has("_user"));
    }

    @Test
    @Order(2)
    public void testGetPublicAsAdmin() throws NotFoundException, InterruptedException, IOException, UnirestException {
        int port = scenario.get("karaf").getPortBinding(8181);
        String host = scenario.get("karaf").getExternalHost();
        String uri = "http://" + host + ":" + port + "/rest/public";
        System.out.println("Port: " + port);
        
        try (LogStream stream = scenario.attach("karaf", null)) {
            HttpResponse<JsonNode> res = Unirest.get(uri)
                    .basicAuth("admin", "secret")
                    .asJson();
            assertEquals(200, res.getStatus());
            JsonNode body = res.getBody();
            
            JSONObject obj = body.getObject();
            System.out.println(obj);
            assertEquals("admin", obj.getString("_user"));
        }
    }

    @Test
    @Order(3)
    public void testGetPrivateAsNone() throws NotFoundException, InterruptedException, IOException, UnirestException {
        int port = scenario.get("karaf").getPortBinding(8181);
        String host = scenario.get("karaf").getExternalHost();
        String uri = "http://" + host + ":" + port + "/rest/library";
        System.out.println("Port: " + port);
        
        try (LogStream stream = scenario.attach("karaf", null)) {
            HttpResponse<JsonNode> res = Unirest.get(uri)
                    .asJson();
            assertEquals(401, res.getStatus());
        }
    }

    @Test
    @Order(4)
    public void testGetPublicDeepNode() throws NotFoundException, InterruptedException, IOException, UnirestException {
        int port = scenario.get("karaf").getPortBinding(8181);
        String host = scenario.get("karaf").getExternalHost();
        String uri = "http://" + host + ":" + port + "/rest/public/info";
        System.out.println("Port: " + port);
        
        try (LogStream stream = scenario.attach("karaf", null)) {
            HttpResponse<JsonNode> res = Unirest.get(uri)
                    .asJson();
            assertEquals(200, res.getStatus());
            JsonNode body = res.getBody();
            
            JSONObject obj = body.getObject();
            System.out.println(obj);
            assertFalse(obj.has("_user"));
            assertEquals("pong", obj.getString("ping"));
        }
    }
    
    @Test
    @Order(10)
    public void testGetBooks() throws NotFoundException, InterruptedException, IOException, UnirestException {
        int port = scenario.get("karaf").getPortBinding(8181);
        String host = scenario.get("karaf").getExternalHost();
        String uri = "http://" + host + ":" + port + "/rest/library/book";
        System.out.println("Port: " + port);
        
        try (LogStream stream = scenario.attach("karaf", null)) {
            HttpResponse<JsonNode> res = Unirest.get(uri)
                    .basicAuth("admin", "secret")
                    .asJson();
            assertEquals(200, res.getStatus());
            JsonNode body = res.getBody();
            
            assertTrue(body.isArray());
            JSONArray books = body.getArray();
            
            assertEquals(1, books.length());
            JSONObject book = books.getJSONObject(0);
            System.out.println(book);
            
            assertEquals(2005, book.getInt("createdyear"));
            assertEquals("Douglas Adams", book.getString("author"));
            assertEquals("978-0345391803", book.getString("isbn"));
        }
    }

    @Test
    @Order(11)
    public void testBooksCrud() throws NotFoundException, InterruptedException, IOException, UnirestException {
        int port = scenario.get("karaf").getPortBinding(8181);
        String host = scenario.get("karaf").getExternalHost();
        String uri = "http://" + host + ":" + port + "/rest/library/book";
        System.out.println("URI: " + uri);
        try (LogStream stream = scenario.attach("karaf", null)) {
    
            {
                HttpResponse<JsonNode> res = Unirest.post(uri)
                        .basicAuth("admin", "secret")
                        .field("isbn", "978-0156031615")
                        .field("author", "Scarlett Thomas")
                        .field("title", "The End of Mr. Y Paperback")
                        .field("description", "A cursed book. A missing professor. Some nefarious men in gray suits. And a dreamworld called the Troposphere?")
                        .field("createdyear", "2006")
                        .asJson();
                assertEquals(200, res.getStatus());
                JsonNode body = res.getBody();
        
                JSONObject book = body.getObject();
                System.out.println(book);
        
                assertEquals(2006, book.getInt("createdyear"));
                assertEquals("Scarlett Thomas", book.getString("author"));
                assertEquals("978-0156031615", book.getString("isbn"));
                assertEquals("The End of Mr. Y Paperback", book.getString("title"));
            }
            {
                HttpResponse<JsonNode> res = Unirest.get(uri)
                        .basicAuth("admin", "secret")
                        .asJson();
                assertEquals(200, res.getStatus());
                JsonNode body = res.getBody();
                
                assertTrue(body.isArray());
                JSONArray books = body.getArray();
                System.out.println(books);
                
                assertEquals(2, books.length());
            }
            {
                HttpResponse<JsonNode> res = Unirest.get(uri + "/978-0156031615")
                        .basicAuth("admin", "secret")
                        .asJson();
                assertEquals(200, res.getStatus());
                JsonNode body = res.getBody();
                JSONObject book = body.getObject();
                System.out.println(book);
                
                assertEquals(2006, book.getInt("createdyear"));
                assertEquals("Scarlett Thomas", book.getString("author"));
                assertEquals("978-0156031615", book.getString("isbn"));
                assertEquals("The End of Mr. Y Paperback", book.getString("title"));
            }
            // update
            {
                HttpResponse<JsonNode> res = Unirest.put(uri + "/978-0156031615")
                        .basicAuth("admin", "secret")
                        .field("title", "The End of Mr. Y")
                        .asJson();
                assertEquals(200, res.getStatus());
                JsonNode body = res.getBody();
                JSONObject book = body.getObject();
                System.out.println(book);
                
                assertEquals(2006, book.getInt("createdyear"));
                assertEquals("Scarlett Thomas", book.getString("author"));
                assertEquals("978-0156031615", book.getString("isbn"));
                assertEquals("The End of Mr. Y", book.getString("title"));
            }
            {
                HttpResponse<JsonNode> res = Unirest.get(uri + "/978-0156031615")
                        .basicAuth("admin", "secret")
                        .asJson();
                assertEquals(200, res.getStatus());
                JsonNode body = res.getBody();
                JSONObject book = body.getObject();
                System.out.println(book);
                
                assertEquals(2006, book.getInt("createdyear"));
                assertEquals("Scarlett Thomas", book.getString("author"));
                assertEquals("978-0156031615", book.getString("isbn"));
                assertEquals("The End of Mr. Y", book.getString("title"));
            }
            // delete
            {
                HttpResponse<JsonNode> res = Unirest.delete(uri + "/978-0156031615")
                        .basicAuth("admin", "secret")
                        .asJson();
                assertEquals(200, res.getStatus());
                JsonNode body = res.getBody();
                JSONObject book = body.getObject();
                System.out.println(book);
                
                assertEquals(2006, book.getInt("createdyear"));
                assertEquals("Scarlett Thomas", book.getString("author"));
                assertEquals("978-0156031615", book.getString("isbn"));
                assertEquals("The End of Mr. Y", book.getString("title"));
            }
            {
                HttpResponse<JsonNode> res = Unirest.get(uri + "/978-0156031615")
                        .basicAuth("admin", "secret")
                        .asJson();
                assertEquals(200, res.getStatus());
                assertEquals("404", res.getBody().getObject().getString("_error"));
            }
        }
    }
    
    @Test
    @Order(12)
    public void testWebSocket() throws NotFoundException, InterruptedException, IOException, UnirestException {
        int port = scenario.get("karaf").getPortBinding(8181);
        String host = scenario.get("karaf").getExternalHost();
        String uri = "sw://" + host + ":" + port + "/restsocket/library?_ticket=admin:secret";
        try (LogStream stream = scenario.attach("karaf", null)) {
            stream.setCapture(true);
            MThread.sleep(3000);
            
            MyWebSocketClient client = new MyWebSocketClient(URI.create(uri));
            client.connect();
            MThread.sleep(3000);
            assertTrue(client.isOpen());
            client.send("true\n");
            MThread.sleep(3000);
            assertTrue(client.text.toString().contains("Hello "));
            client.send("Hello World\n");
            client.close();
            MThread.sleep(3000);
            
            @SuppressWarnings("unused")
            String out = stream.getCaptured();
            
//XXX is working but output is not working --- why...?
//XXX since log level=DEBUG           assertTrue(out.contains("Streaming.Text:true"));
//XXX since log level=DEBUG           assertTrue(out.contains("Streaming.Text:Hello World"));
            
        } finally {
        }
    }
    
    @Test
    @Order(13)
    public void testBearer() throws NotFoundException, InterruptedException, IOException, UnirestException {
        int port = scenario.get("karaf").getPortBinding(8181);
        String host = scenario.get("karaf").getExternalHost();
        
        // create token
        
        String uri = "http://" + host + ":" + port + "/rest/jwt_token";
        
        try (LogStream stream = scenario.attach("karaf", null)) {
            HttpResponse<JsonNode> res = Unirest.post(uri)
                    .basicAuth("admin", "secret")
                    .asJson();
            assertEquals(200, res.getStatus());
            JsonNode body = res.getBody();
            
            JSONObject obj = body.getObject();
            System.out.println(obj);
            assertEquals("admin", obj.getString("_user"));
            assertTrue(obj.has("token"));
    
            String token = obj.getString("token");
            
            // test access
            
            uri = "http://" + host + ":" + port + "/rest/public/uid?jwt_token=" + token;
            res = Unirest.get(uri)
//                    .header("Authorization", "Bearer " + token)
                    .asJson();
            assertEquals(200, res.getStatus());
            body = res.getBody();
            
            obj = body.getObject();
            System.out.println(obj);
            
            assertEquals("admin", obj.getString("_user"));
            
            uri = "http://" + host + ":" + port + "/rest/public/uid";
            res = Unirest.get(uri)
                    .header("Authorization", "Bearer " + token)
                    .asJson();
            assertEquals(200, res.getStatus());
            body = res.getBody();
            
            obj = body.getObject();
            System.out.println(obj);
            
            assertEquals("admin", obj.getString("_user"));
            
        }
        
    }

    private class MyWebSocketClient extends WebSocketClient {

        public MyWebSocketClient(URI serverUri) {
            super(serverUri);
        }

        StringBuffer text = new StringBuffer();
        
        @Override
        public void onOpen(ServerHandshake handshakedata) {
            System.out.println("MyWebSocketClient:Open");
        }

        @Override
        public void onMessage(String message) {
            System.out.println("MyWebSocketClient:Message " + message);
            text.append(message);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            System.out.println("MyWebSocketClient:Closed");
        }

        @Override
        public void onError(Exception ex) {
            ex.printStackTrace();
        }
        
    }
    
    @BeforeAll
    public static void startDocker() throws NotFoundException, IOException, InterruptedException {
        
        prop = TestUtil.loadProperties();

        // http://kong.github.io/unirest-java/
        Unirest.config().interceptor(new Interceptor() {
            @Override
            public void onRequest(HttpRequest<?> request, Config config) {
                System.out.println("REST >>> " + request.getHttpMethod() + " " + request.getUrl());
            }
            @Override
            public void onResponse(HttpResponse<?> response, HttpRequestSummary request, Config config) {
                System.out.println("REST <<< " + response.getBody());
            }

        });
        
        scenario = new DockerScenario();
        scenario.add(new Karaf("karaf", prop.getString("docker.mhus-apache-karaf.version"), "debug", 
                "p:32796+:8181",
                "env:TERM=xterm"
                ));
        
        scenario.destroyPrefix();
        scenario.start();
        
        MThread.sleep(1000);

        scenario.waitForLogEntry("karaf", "@karaf()>", 0);
        
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            stream.setFilter(new AnsiLogFilter());
            scenario.attach(stream, 
                "feature:repo-add mvn:org.apache.shiro/shiro-features/"+prop.getString("shiro.version")+"/xml/features\n" + 
                "feature:repo-add mvn:de.mhus.osgi/mhus-features/"+prop.getString("mhus-parent.version")+"/xml/features\n" +
                "feature:install mhu-base mhu-dev mhu-rest\n" +
                "bundle:install -s mvn:de.mhus.lib.itest/examples-rest/"+prop.getString("project.version")+"\n" +
                "list\n" +
                "a=HGDFhjasdhj\n" );
        
            scenario.waitForLogEntry(stream, "HGDFhjasdhj");
            
            String out = stream.getCaptured();
            
            int pos1 = out.indexOf("List Threshold");
            assertTrue(pos1 > 0);
            int pos2 = out.indexOf("@karaf()", pos1);
            assertTrue(pos2 > 0);
            
            out = out.substring(pos1, pos2);
            assertTrue(out.contains("lib-annotations"));
            assertTrue(out.contains("lib-core"));
            assertTrue(out.contains("lib-j2ee"));
            assertTrue(out.contains("karaf-commands"));
            assertTrue(out.contains("osgi-api"));
            assertTrue(out.contains("osgi-services"));
            assertTrue(out.contains("karaf-dev"));
            
            assertTrue(out.contains("rest-core"));
            assertTrue(out.contains("rest-osgi"));
            assertTrue(out.contains("rest-karaf"));
            assertTrue(out.contains("examples-rest"));
        }
        
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            stream.setFilter(new AnsiLogFilter());
            scenario.attach(stream, 
                    "dev-res -y cp karaf-dev/default\n" +
                    "blue-create de.mhus.rest.core.impl.RestServlet\n" +
                    "blue-create de.mhus.rest.core.impl.RestWebSocketServlet\n" +
                    "blue-create de.mhus.rest.core.nodes.PublicRestNode\n" +
                    "blue-create de.mhus.rest.core.nodes.UserInformationRestNode\n" +
                    "blue-create de.mhus.rest.core.nodes.JwtTokenNode\n" +
                    "a=kjshkjfhjkIUYJGHJK\n" );

            scenario.waitForLogEntry(stream, "kjshkjfhjkIUYJGHJK");
            MThread.sleep(5000); // a long time - wait for configuration manager

            @SuppressWarnings("unused")
            String out = stream.getCaptured();

      //      assertTrue(out.contains("[doConfigure]"));
      //      assertTrue(out.contains("[KarafCfgManager::Register PID][de.mhus.osgi.api.services.PersistentWatch]"));
        }
        
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            scenario.attach(stream, 
                    "mhus:log-set reloadconfig\n" +
                    "mhus:access-restart\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVU\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVU");
        }
        
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            stream.setFilter(new AnsiLogFilter());
            scenario.attach(stream, 
                "list\n" +
                "a=HGDFhjasdhj\n" );
        
            scenario.waitForLogEntry(stream, "HGDFhjasdhj");
            
            String out = stream.getCaptured();
            
            int pos1 = out.indexOf("List Threshold");
            assertTrue(pos1 > 0);
            int pos2 = out.indexOf("@karaf()", pos1);
            assertTrue(pos2 > 0);
            
            out = out.substring(pos1, pos2);
            assertTrue(out.contains("service-de.mhus.rest.core.impl.restservlet.xml"));
//            assertTrue(out.contains("service-de.mhus.rest.osgi.restwebsocketservlet.xml"));
            assertTrue(out.contains("service-de.mhus.rest.core.impl.restwebsockets"));
        }
    }
    
    @AfterAll
    public static void stopDocker() {
        if (prop.getBoolean("docker.destroy.containers", true))
            scenario.destroy();
    }
    
    
}
