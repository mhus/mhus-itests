package de.mhus.lib.itest.cases;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
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

@Disabled
@TestMethodOrder(OrderAnnotation.class)
public class KarafMicroTest extends TestCase {

    private static DockerScenario scenario;
    private static MProperties prop;

    @Test
    @Order(1)
    public void test1MoList() throws NotFoundException, IOException, InterruptedException {
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "micro:mo-list -ta\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("de.mhus.osgi.dev.micro.AuthenticationOnlyOperation"));
            assertTrue(out.contains("de.mhus.osgi.dev.critical.micro.Hello"));
            assertTrue(out.contains("0.0.0"));
            assertTrue(out.contains("1.1.0"));
            assertTrue(out.contains("2.0.0"));
        }
    }

    @Test
    @Order(2)
    public void test1PublisherList() throws NotFoundException, IOException, InterruptedException {
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "micro:pusher-list -ta\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("de.mhus.micro.client.redis.RedisPusher"));
            assertTrue(out.contains("de.mhus.micro.impl.LocalOperationsLoopback"));
        }
    }
    
    @Test
    @Order(3)
    public void test1OperationList() throws NotFoundException, IOException, InterruptedException {
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "micro:operation-list -l -ta\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("rest.url=http://karaf1:8181/rest/public/operation/de.mhus.osgi.dev.critical.micro.Hello/0.0.0"));
            assertTrue(out.contains("rest.url=http://karaf1:8181/rest/public/operation/de.mhus.osgi.dev.micro.AuthenticationOnlyOperation/1.0.0"));
            assertTrue(out.contains("rest.url=http://karaf1:8181/rest/public/operation/de.mhus.osgi.dev.critical.micro.Hello/1.1.0"));
            assertTrue(out.contains("rest.url=http://karaf1:8181/rest/public/operation/de.mhus.osgi.dev.critical.micro.Hello/2.0.0"));

        }
    }

    @Test
    @Order(4)
    public void test1ProviderList() throws NotFoundException, IOException, InterruptedException {
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "micro:provider-list -ta\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("de.mhus.micro.oper.rest.OperationsNode"));
            assertTrue(out.contains("rest.url=http://karaf1:8181/rest/public/operation/de.mhus.osgi.dev.critical.micro.Hello/0.0.0"));
            assertTrue(out.contains("rest.url=http://karaf1:8181/rest/public/operation/de.mhus.osgi.dev.micro.AuthenticationOnlyOperation/1.0.0"));
            assertTrue(out.contains("rest.url=http://karaf1:8181/rest/public/operation/de.mhus.osgi.dev.critical.micro.Hello/1.1.0"));
            assertTrue(out.contains("rest.url=http://karaf1:8181/rest/public/operation/de.mhus.osgi.dev.critical.micro.Hello/2.0.0"));

        }
    }

    @Test
    @Order(11)
    public void test2MoList() throws NotFoundException, IOException, InterruptedException {
        try (LogStream stream = new LogStream(scenario, "karaf2")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "micro:mo-list -ta\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertFalse(out.contains("de.mhus.osgi.dev.micro.AuthenticationOnlyOperation"));
            assertFalse(out.contains("de.mhus.osgi.dev.critical.micro.Hello"));
            assertFalse(out.contains("0.0.0"));
            assertFalse(out.contains("1.1.0"));
            assertFalse(out.contains("2.0.0"));
        }
    }

    @Test
    @Order(12)
    public void test2PublisherList() throws NotFoundException, IOException, InterruptedException {
        try (LogStream stream = new LogStream(scenario, "karaf2")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "micro:pusher-list -ta\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("de.mhus.micro.client.redis.RedisPusher"));
            assertTrue(out.contains("de.mhus.micro.impl.LocalOperationsLoopback"));
        }
    }
    
    @Test
    @Order(13)
    public void test2OperationList() throws NotFoundException, IOException, InterruptedException {
        try (LogStream stream = new LogStream(scenario, "karaf2")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "micro:operation-list -l -ta\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("rest.url=http://karaf1:8181/rest/public/operation/de.mhus.osgi.dev.critical.micro.Hello/0.0.0"));
            assertTrue(out.contains("rest.url=http://karaf1:8181/rest/public/operation/de.mhus.osgi.dev.micro.AuthenticationOnlyOperation/1.0.0"));
            assertTrue(out.contains("rest.url=http://karaf1:8181/rest/public/operation/de.mhus.osgi.dev.critical.micro.Hello/1.1.0"));
            assertTrue(out.contains("rest.url=http://karaf1:8181/rest/public/operation/de.mhus.osgi.dev.critical.micro.Hello/2.0.0"));

        }
    }

    @Test
    @Order(14)
    public void test2ProviderList() throws NotFoundException, IOException, InterruptedException {
        try (LogStream stream = new LogStream(scenario, "karaf2")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "micro:provider-list -ta\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertFalse(out.contains("de.mhus.micro.oper.rest.OperationsNode"));
            assertFalse(out.contains("rest.url=http://karaf1:8181/rest/public/operation/de.mhus.osgi.dev.critical.micro.Hello/0.0.0"));
            assertFalse(out.contains("rest.url=http://karaf1:8181/rest/public/operation/de.mhus.osgi.dev.micro.AuthenticationOnlyOperation/1.0.0"));
            assertFalse(out.contains("rest.url=http://karaf1:8181/rest/public/operation/de.mhus.osgi.dev.critical.micro.Hello/1.1.0"));
            assertFalse(out.contains("rest.url=http://karaf1:8181/rest/public/operation/de.mhus.osgi.dev.critical.micro.Hello/2.0.0"));

        }
    }

    
    @Test
    @Order(20)
    public void test1ExecuteLocal() throws NotFoundException, IOException, InterruptedException {
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            stream.setCapture(true);

            scenario.attach(stream, 
                    "operation-execute -l trans=local de.mhus.osgi.dev.critical.micro.Hello 1.0.0+\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("[OperationDescription:[de.mhus.osgi.dev.critical.micro.Hello],[1.1.0],[{trans=local}]]"));
            assertTrue(out.contains("msg=Moin"));
            assertTrue(out.contains("version=1"));
        }
    }
    
    @Test
    @Order(21)
    public void test1ExecuteRest() throws NotFoundException, IOException, InterruptedException {
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "operation-execute -l trans=rest de.mhus.osgi.dev.critical.micro.Hello 1.0.0+\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("[OperationDescription:[de.mhus.osgi.dev.critical.micro.Hello],"));
            assertTrue(out.contains("trans=rest"));
            assertTrue(out.contains("msg=Moin"));
            assertTrue(out.contains("version=1"));
            assertTrue(out.contains("ident=karaf1-"));
            assertTrue(out.contains("1.1.0"));
        }
    }
    
    @Test
    @Order(22)
    public void test1ExecuteMandatory() throws NotFoundException, IOException, InterruptedException {
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "operation-execute -l trans=local de.mhus.osgi.dev.critical.micro.Hello 0.0.0\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("[not found (3)][Mandatory: next]"));
        }
    }
    
    @Test
    @Order(30)
    public void test2ExecuteLocal() throws NotFoundException, IOException, InterruptedException {
        try (LogStream stream = new LogStream(scenario, "karaf2")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "operation-execute -l trans=local de.mhus.osgi.dev.critical.micro.Hello 1.0.0+\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("Operation not found"));
        }
    }
    
    @Test
    @Order(31)
    public void test2ExecuteRest() throws NotFoundException, IOException, InterruptedException {
        try (LogStream stream = new LogStream(scenario, "karaf2")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "operation-execute -l trans=rest de.mhus.osgi.dev.critical.micro.Hello 1.0.0+\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("[OperationDescription:[de.mhus.osgi.dev.critical.micro.Hello],[1.1.0],"));
            assertTrue(out.contains("trans=rest"));
            assertTrue(out.contains("msg=Moin"));
            assertTrue(out.contains("version=1"));
            assertTrue(out.contains("ident=karaf1-"));
            assertTrue(out.contains("1.1.0"));
        }
    }
    
    @Test
    @Order(32)
    public void test2ExecuteMandatory() throws NotFoundException, IOException, InterruptedException {
        try (LogStream stream = new LogStream(scenario, "karaf2")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "operation-execute -l trans=rest de.mhus.osgi.dev.critical.micro.Hello 0.0.0\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("[not found (3)]"));
            assertTrue(out.contains("_error=404"));
        }
    }

    @Test
    @Order(33)
    public void test2ExecuteViaJms() throws NotFoundException, IOException, InterruptedException {
        try (LogStream stream = new LogStream(scenario, "karaf2")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "operation-execute -l trans=jms de.mhus.osgi.dev.critical.micro.Hello 2.0.0\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("[OperationDescription:[de.mhus.osgi.dev.critical.micro.Hello],[2.0.0]"));
            assertTrue(out.contains("queue=karaf1"));
            assertTrue(out.contains("msg=Moin"));
        }
    }
    
    @Test
    @Order(40)
    public void testCreateDelete() throws NotFoundException, IOException, InterruptedException {
        
        // create 
        
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "blue-create de.mhus.osgi.dev.micro.AnotherOperation\n");

            scenario.waitForLogEntry(stream, "Started bundle: blueprint:file:/opt/karaf/deploy/service-de.mhus.osgi.dev.micro.anotheroperation.xml");

            String out = stream.getCaptured();
            assertTrue(out.contains("Created"));
        }
        MThread.sleep(1000); // max time for events to notify all components

        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            stream.setCapture(true);

            scenario.attach(stream, 
                    "micro:operation-list -l -ta\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("rest.url=http://karaf1:8181/rest/public/operation/de.mhus.osgi.dev.micro.AnotherOperation/0.0.0"));

        }
        try (LogStream stream = new LogStream(scenario, "karaf2")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "micro:operation-list -l -ta\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("rest.url=http://karaf1:8181/rest/public/operation/de.mhus.osgi.dev.micro.AnotherOperation/0.0.0"));

        }
        
        // delete
        
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "blue-delete de.mhus.osgi.dev.micro.AnotherOperation\n");

            scenario.waitForLogEntry(stream, "Destroying container for blueprint bundle service-de.mhus.osgi.dev.micro.anotheroperation.xml");

            String out = stream.getCaptured();
            assertTrue(out.contains("Deleted"));
        }
        MThread.sleep(1000); // max time for events to notify all components
        
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            stream.setCapture(true);

            scenario.attach(stream, 
                    "micro:operation-list -l -ta\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertFalse(out.contains("rest.url=http://karaf1:8181/rest/public/operation/de.mhus.osgi.dev.micro.AnotherOperation/0.0.0"));

        }
        try (LogStream stream = new LogStream(scenario, "karaf2")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "micro:operation-list -l -ta\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertFalse(out.contains("rest.url=http://karaf1:8181/rest/public/operation/de.mhus.osgi.dev.micro.AnotherOperation/0.0.0"));

        }
        
        
    }

    @Test
    @Order(50)
    public void test1Bearer() throws NotFoundException, IOException, InterruptedException {
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            
            // login as joe
            stream.setCapture(true);
            scenario.attach(stream, 
                    "access login joe nein\n" +
                    "a=quiweyBNVNX\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNX");
            
            String out = stream.getCaptured();
            assertTrue(out.contains("OK"));
        }
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            // local
            stream.setCapture(true);
            scenario.attach(stream, 
                    "operation-execute -l trans=local de.mhus.osgi.dev.critical.micro.Hello 2.0.0+\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("[OperationDescription:[de.mhus.osgi.dev.critical.micro.Hello],[2.0.0],"));
            assertTrue(out.contains("trans=local"));
            assertTrue(out.contains("msg=Moin"));
            assertTrue(out.contains("version=2"));
            assertTrue(out.contains("principal=joe"));
        }
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            // rest
            stream.setCapture(true);
            scenario.attach(stream, 
                    "operation-execute -l trans=rest de.mhus.osgi.dev.critical.micro.Hello 2.0.0+\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("[OperationDescription:[de.mhus.osgi.dev.critical.micro.Hello],[2.0.0],"));
            assertTrue(out.contains("trans=rest"));
            assertTrue(out.contains("msg=Moin"));
            assertTrue(out.contains("version=2"));
            assertTrue(out.contains("principal=joe"));
        }
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            // jms
            stream.setCapture(true);
            scenario.attach(stream, 
                    "operation-execute -l trans=jms de.mhus.osgi.dev.critical.micro.Hello 2.0.0+\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("[OperationDescription:[de.mhus.osgi.dev.critical.micro.Hello],[2.0.0],"));
            assertTrue(out.contains("trans=jms"));
            assertTrue(out.contains("msg=Moin"));
            assertTrue(out.contains("version=2"));
            assertTrue(out.contains("principal=joe"));
        }
        try (LogStream stream = new LogStream(scenario, "karaf1")) {

            // logout
            stream.setCapture(true);
            scenario.attach(stream, 
                    "access logout\n" +
                    "a=quiweyBNVNX\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNX");
            
            String out = stream.getCaptured();
            assertTrue(out.contains("OK"));
            
        }
    }
    
    @BeforeAll
    public static void startDocker() throws NotFoundException, IOException, InterruptedException {
        
        prop = TestUtil.loadProperties();

        scenario = new DockerScenario();
        
        scenario.add("jms", "webcenter/activemq:5.14.3", 
                "env:ACTIVEMQ_CONFIG_NAME=amqp-srv1",
                "env:ACTIVEMQ_CONFIG_DEFAULTACCOUNT=false",
                "env:ACTIVEMQ_ADMIN_LOGIN=admin",
                "env:ACTIVEMQ_ADMIN_PASSWORD=nein",
                "env:ACTIVEMQ_CONFIG_MINMEMORY=1024",
                "env:ACTIVEMQ_CONFIG_MAXMEMORY=4096",
                "env:ACTIVEMQ_CONFIG_SCHEDULERENABLED=true",
                "p:28161+:8161"
            );
        
        scenario.add("redis", "redis:6.0.8-alpine");

        scenario.add(new Karaf("karaf1", prop.getString("docker.mhus-apache-karaf.version"), 
                "debug", 
                "link:redis:redis",
                "link:jms:jmsserver",
                "env:de_mhus_lib_jms_MJms_defaultConnection=test"
                ));
        
        scenario.add(new Karaf("karaf2", prop.getString("docker.mhus-apache-karaf.version"), 
                "debug", 
                "link:redis:redis",
                "link:karaf1:karaf1",
                "link:jms:jmsserver",
                "env:de_mhus_lib_jms_MJms_defaultConnection=test"
                ));

        scenario.destroyPrefix();
        scenario.start();
        
        MThread.sleep(1000);

        // jms
        scenario.waitForLogEntry("jms", "activemq entered RUNNING state", 0);

        // redis
        scenario.waitForLogEntry("redis", "Ready to accept connections", 0);
        
        //-----------------------------------------------
        // karaf1
        scenario.waitForLogEntry("karaf1", "Done.", 0);
        
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            stream.setFilter(new AnsiLogFilter());
            scenario.attach(stream, 
                "feature:repo-add activemq 5.15.8\n" +
                "feature:repo-add mvn:org.apache.shiro/shiro-features/"+prop.getString("shiro.version")+"/xml/features\n" + 
                "feature:repo-add mvn:de.mhus.osgi/mhus-features/"+prop.getString("mhus-parent.version")+"/xml/features\n" +
                "feature:install eventadmin mhu-dev mhu-micro mhu-rest mhu-jms\n" +
                "a=HGDFhjasdhj\n" );
        
            scenario.waitForLogEntry(stream, "Done.");
        }
        // installing activemq is asynchron, need to wait a while before next step
        MThread.sleep(10000);
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            stream.setFilter(new AnsiLogFilter());

            scenario.attach(stream, 
                    "bundle:install -s mvn:de.mhus.osgi/dev-micro/"+prop.getString("mhus-dev.version","7.2.0-SNAPSHOT")+"\n" + 
                    "bundle:install -s mvn:de.mhus.micro/micro-oper-rest/"+prop.getString("mhus-micro.version","7.0.0-SNAPSHOT")+"\n" + 
                    "bundle:install -s mvn:de.mhus.micro/micro-execute-rest/"+prop.getString("mhus-micro.version","7.0.0-SNAPSHOT")+"\n" + 
                    "bundle:install -s mvn:de.mhus.micro/micro-discover-redis/"+prop.getString("mhus-micro.version","7.0.0-SNAPSHOT")+"\n" + 
                    "bundle:install -s mvn:de.mhus.lib.itest/examples-jms/"+prop.getString("project.version")+"\n" + 
                    "blue-create de.mhus.rest.osgi.RestServlet\n" +
                    "blue-create de.mhus.rest.osgi.nodes.PublicRestNode\n" +
                    "a=HGDFhjasdhz\n" );
            scenario.waitForLogEntry(stream, "HGDFhjasdhz");
        }
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            stream.setFilter(new AnsiLogFilter());

            scenario.attach(stream, 
                    "dev-res -y cp examples-jms\n" +
                    "install -s mvn:de.mhus.micro/micro-oper-jms/7.0.0-SNAPSHOT\n" +
                    "blue-create de.mhus.micro.oper.jms.DefaultOperationsChannel\n" + 
                    "a=HGDFhjasdhz\n" );
            scenario.waitForLogEntry(stream, "HGDFhjasdhz");
        }
        MThread.sleep(5000);
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            stream.setFilter(new AnsiLogFilter());
            stream.setCapture(true);
            scenario.attach(stream, 
                    "list\n" +
                    "a=HGDFhjasdhx\n" );
            scenario.waitForLogEntry(stream, "HGDFhjasdhx");
            
            String out = stream.getCaptured();
            
            int pos1 = out.indexOf("List Threshold");
            assertTrue(pos1 > 0);
            int pos2 = out.indexOf("@karaf1()", pos1);
            assertTrue(pos2 > 0);
            out = out.substring(pos1, pos2);
            
            assertTrue(out.contains("lib-annotations"));
            assertTrue(out.contains("lib-core"));
            assertTrue(out.contains("lib-j2ee"));
            assertTrue(out.contains("karaf-commands"));
            assertTrue(out.contains("osgi-api"));
            assertTrue(out.contains("osgi-services"));
            
            assertTrue(out.contains("dev-micro"));
            assertTrue(out.contains("micro-api"));
            assertTrue(out.contains("micro-impl"));
            assertTrue(out.contains("micro-karaf"));
            assertTrue(out.contains("micro-oper-rest"));
            assertTrue(out.contains("micro-execute"));
            assertTrue(out.contains("micro-discover-redis"));
            assertTrue(out.contains("service-de.mhus.rest.osgi.restservlet.xml"));

        }
        
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            stream.setCapture(true);
            stream.setFilter(new AnsiLogFilter());
            scenario.attach(stream, 
                    "dev-res -y cp default\n" +
                    "a=kjshkjfhjkIUYJGHJK\n" );

            scenario.waitForLogEntry(stream, "kjshkjfhjkIUYJGHJK");
            MThread.sleep(5000); // a long time - wait for configuration manager

            @SuppressWarnings("unused")
            String out = stream.getCaptured();

//            assertTrue(out.contains("[doConfigure]"));
//            assertTrue(out.contains("[KarafCfgManager::Register PID][de.mhus.osgi.api.services.PersistentWatch]"));
        }
        
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            scenario.attach(stream, 
                    "access restart\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVU\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVU");
        }

        
        //-----------------------------------------------
        // karaf2
        scenario.waitForLogEntry("karaf2", "Done.", 0);
        
        try (LogStream stream = new LogStream(scenario, "karaf2")) {
            stream.setFilter(new AnsiLogFilter());
            scenario.attach(stream, 
                "feature:repo-add activemq 5.15.8\n" +
                "feature:repo-add mvn:org.apache.shiro/shiro-features/"+prop.getString("shiro.version")+"/xml/features\n" + 
                "feature:repo-add mvn:de.mhus.osgi/mhus-features/"+prop.getString("mhus-parent.version")+"/xml/features\n" +
                "feature:install eventadmin mhu-dev mhu-micro mhu-jms\n" +
                "a=HGDFhjasdhj\n" );
        
            scenario.waitForLogEntry(stream, "Done.");
        }
        // installing activemq is asynchron, need to wait a while before next step
        MThread.sleep(10000);
        try (LogStream stream = new LogStream(scenario, "karaf2")) {
            stream.setFilter(new AnsiLogFilter());

            scenario.attach(stream, 
                    "bundle:install -s mvn:de.mhus.micro/micro-execute-rest/"+prop.getString("mhus-micro.version","7.0.0-SNAPSHOT")+"\n" + 
                    "bundle:install -s mvn:de.mhus.micro/micro-discover-redis/"+prop.getString("mhus-micro.version","7.0.0-SNAPSHOT")+"\n" + 
                    "bundle:install -s mvn:de.mhus.lib.itest/examples-jms/"+prop.getString("project.version")+"\n" + 
                    "a=HGDFhjasdhz\n" );
            scenario.waitForLogEntry(stream, "HGDFhjasdhz");
        }
        try (LogStream stream = new LogStream(scenario, "karaf2")) {
            stream.setFilter(new AnsiLogFilter());

            scenario.attach(stream, 
                    "dev-res -y cp examples-jms\n" +
                    "install -s mvn:de.mhus.micro/micro-oper-jms/7.0.0-SNAPSHOT\n" +
                    "a=HGDFhjasdhz\n" );
            scenario.waitForLogEntry(stream, "HGDFhjasdhz");
        }
        MThread.sleep(5000);
        try (LogStream stream = new LogStream(scenario, "karaf2")) {
            stream.setFilter(new AnsiLogFilter());
            stream.setCapture(true);
            scenario.attach(stream, 
                    "list\n" +
                    "a=HGDFhjasdhx\n" );
            scenario.waitForLogEntry(stream, "HGDFhjasdhx");
            
            String out = stream.getCaptured();
            
            int pos1 = out.indexOf("List Threshold");
            assertTrue(pos1 > 0);
            int pos2 = out.indexOf("@karaf2()", pos1);
            assertTrue(pos2 > 0);
            out = out.substring(pos1, pos2);
            
            assertTrue(out.contains("lib-annotations"));
            assertTrue(out.contains("lib-core"));
            assertTrue(out.contains("lib-j2ee"));
            assertTrue(out.contains("karaf-commands"));
            assertTrue(out.contains("osgi-api"));
            assertTrue(out.contains("osgi-services"));
            
            assertTrue(out.contains("micro-api"));
            assertTrue(out.contains("micro-impl"));
            assertTrue(out.contains("micro-karaf"));
            assertTrue(out.contains("micro-execute"));
            assertTrue(out.contains("micro-discover-redis"));

        }
        
        try (LogStream stream = new LogStream(scenario, "karaf2")) {
            stream.setCapture(true);
            stream.setFilter(new AnsiLogFilter());
            scenario.attach(stream, 
                    "dev-res -y cp default\n" +
                    "dev-res -y cp disable-debug-log\n" +
                    "a=kjshkjfhjkIUYJGHJK\n" );

            scenario.waitForLogEntry(stream, "kjshkjfhjkIUYJGHJK");
            MThread.sleep(5000); // a long time - wait for configuration manager

            String out = stream.getCaptured();

//            assertTrue(out.contains("[doConfigure]"));
            assertTrue(out.contains("[KarafCfgManager::Register PID][de.mhus.osgi.api.services.PersistentWatch]"));
        }
        
        try (LogStream stream = new LogStream(scenario, "karaf2")) {
            scenario.attach(stream, 
                    "access restart\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVU\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVU");
        }

    }
    
    @AfterAll
    public static void stopDocker() {
        if (prop.getBoolean("docker.destroy.containers", true))
            scenario.destroy();
    }

    
}
