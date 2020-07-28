package de.mhus.lib.itest.cases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MThread;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.tests.TestCase;
import de.mhus.lib.tests.docker.AnsiLogFilter;
import de.mhus.lib.tests.docker.DockerScenario;
import de.mhus.lib.tests.docker.Karaf;
import de.mhus.lib.tests.docker.LogStream;

@TestMethodOrder(OrderAnnotation.class)
public class OsgiRestTest extends TestCase {

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
        
        HttpResponse<JsonNode> res = Unirest.get(uri)
                .basicAuth("admin", "secret")
                .asJson();
        assertEquals(200, res.getStatus());
        JsonNode body = res.getBody();
        
        JSONObject obj = body.getObject();
        System.out.println(obj);
        assertEquals("admin", obj.getString("_user"));
    }

    @Test
    @Order(3)
    public void testGetPrivateAsNone() throws NotFoundException, InterruptedException, IOException, UnirestException {
        int port = scenario.get("karaf").getPortBinding(8181);
        String host = scenario.get("karaf").getExternalHost();
        String uri = "http://" + host + ":" + port + "/rest/library";
        System.out.println("Port: " + port);
        
        HttpResponse<JsonNode> res = Unirest.get(uri)
                .asJson();
        assertEquals(401, res.getStatus());
    }
    
    @Test
    @Order(10)
    public void testGetBooks() throws NotFoundException, InterruptedException, IOException, UnirestException {
        int port = scenario.get("karaf").getPortBinding(8181);
        String host = scenario.get("karaf").getExternalHost();
        String uri = "http://" + host + ":" + port + "/rest/library/book";
        System.out.println("Port: " + port);
        
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

    @BeforeAll
    public static void startDocker() throws NotFoundException, IOException, InterruptedException {
        
        prop = new MProperties(System.getenv());
        
        if (!prop.containsKey("project.version")) {
            System.out.println("Load env from file");
            File f = new File("../target/classes/app.properties");
            if (!f.exists())
                throw new NotFoundException("app.properties not found: " + f);
            prop.putAll(MProperties.load(f));
        }
        System.out.println(prop);
        
        scenario = new DockerScenario();
        scenario.add(new Karaf("karaf", prop.getString("docker.mhus-apache-karaf.version"), "debug", "p:32796+:8181"));
        
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
                "feature:install mhu-base mhu-dev mhu-rest-servlet\n" +
                "bundle:install -s mvn:de.mhus.lib.itest/examples-rest/7.0.0-SNAPSHOT\n" + // +prop.getString("project.version")+
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
            assertTrue(out.contains("db-core"));
            assertTrue(out.contains("db-karaf"));
            assertTrue(out.contains("db-osgi-api"));
            assertTrue(out.contains("db-osgi-adb"));
            assertTrue(out.contains("karaf-commands"));
            assertTrue(out.contains("osgi-api"));
            assertTrue(out.contains("osgi-services"));
            assertTrue(out.contains("karaf-dev"));
            
            assertTrue(out.contains("rest-core"));
            assertTrue(out.contains("rest-osgi"));
            assertTrue(out.contains("rest-karaf"));
            assertTrue(out.contains("rest-osgi-servlet"));
            assertTrue(out.contains("examples-rest"));
        }
        
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            stream.setFilter(new AnsiLogFilter());
            scenario.attach(stream, 
                    "dev-res -y cp default\n" +
                    "a=kjshkjfhjkIUYJGHJK\n" );

            scenario.waitForLogEntry(stream, "kjshkjfhjkIUYJGHJK");
            MThread.sleep(5000); // a long time - wait for configuration manager

            String out = stream.getCaptured();

            assertTrue(out.contains("[doConfigure]"));
            assertTrue(out.contains("[KarafCfgManager::Register PID][de.mhus.osgi.api.services.PersistentWatch]"));
        }
        
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            scenario.attach(stream, 
                    "access restart\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVU\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVU");
        }
        
    }
    
    @AfterAll
    public static void stopDocker() {
        // scenario.destroy();
    }
    
    
}
