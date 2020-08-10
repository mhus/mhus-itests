package de.mhus.lib.itest.cases;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URL;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.github.dockerjava.api.exception.DockerException;

import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MThread;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.tests.TestCase;
import de.mhus.lib.tests.docker.AnsiLogFilter;
import de.mhus.lib.tests.docker.DockerContainer;
import de.mhus.lib.tests.docker.DockerScenario;
import de.mhus.lib.tests.docker.Karaf;
import de.mhus.lib.tests.docker.LogStream;

@TestMethodOrder(OrderAnnotation.class)
public class ReactiveSingleTest extends TestCase {

    private static DockerScenario scenario;
    private static MProperties prop;
    @SuppressWarnings("unused")
    private static WebDriver driver;
    @SuppressWarnings("unused")
    private static final String UI_URL = "http://uiserver:8181/ui";

    @Test
    @Order(1)
    public void testList() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "pls -ta\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1"));
            assertTrue(out.contains("enabled"));
        }
    }
    
    @Test
    @Order(10)
    public void testSecond() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "pstart \"bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool;customId=test;customerId=alf?text1=second\"\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertFalse(out.equals("Exception"));
            assertFalse(out.equals("ERROR"));
        }
        
        boolean done = false;
        for (int i = 0 ; i < 20; i++) {
            MThread.sleep(5000);
            try (LogStream stream = new LogStream(scenario, "karaf")) {
                stream.setCapture(true);
                
                scenario.attach(stream, 
                        "pcase -a -ta list\n" +
                        "a=quiweyBNVNB\n" );
    
                scenario.waitForLogEntry(stream, "quiweyBNVNB");
    
                String out = stream.getCaptured();
                if (out.contains("CLOSED")) {
                    done = true;
                    break;
                }
                assertTrue(out.contains("bpm://de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1/de.mhus.cherry.reactive.examples.simple1.S1Pool"));
                assertFalse(out.equals("Exception"));
                assertFalse(out.equals("ERROR"));
            }
        }
        assertTrue(done,"Process was not processed");
        
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "pengine archive\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("Archive all"));
            assertFalse(out.equals("Exception"));
            assertFalse(out.equals("ERROR"));
        }
    }
    
    
    @BeforeAll
    public static void startDocker() throws NotFoundException, IOException, InterruptedException {
        
        prop = TestUtil.loadProperties();

        scenario = new DockerScenario();
        
        scenario.add("jaeger", "jaegertracing/all-in-one:1.18.1",
                "p:16686+:16686"
                );
                
        scenario.add("jms", "webcenter/activemq:5.14.3", 
          "env:ACTIVEMQ_CONFIG_NAME=amqp-srv1",
          "env:ACTIVEMQ_CONFIG_DEFAULTACCOUNT=false",
          "env:ACTIVEMQ_ADMIN_LOGIN=admin",
          "env:ACTIVEMQ_ADMIN_PASSWORD=nein",
          "env:ACTIVEMQ_CONFIG_MINMEMORY=1024",
          "env:ACTIVEMQ_CONFIG_MAXMEMORY=4096",
          "env:ACTIVEMQ_CONFIG_SCHEDULERENABLED=true"
        );

        scenario.add("db", "mariadb:10.3", 
                "e:MYSQL_ROOT_PASSWORD=nein"
          );

        scenario.add(new Karaf("karaf", prop.getString("docker.mhus-apache-karaf.version"), 
                "debug", 
                "link:jms:jmsserver",
                "link:db:dbserver",
                "link:jaeger:jaeger"
                ));

        scenario.add(new DockerContainer("selenium", "selenium/standalone-chrome-debug:3.141.59-bismuth", 
                "p:25900+:5900",
                "p:24444+:4444",
                "l:karaf:uiserver"
                ));
        
        scenario.destroyPrefix();
        scenario.start();
        
        // DB
        
        scenario.waitForLogEntry("db", "mysqld: ready for connections", 0);
        
        MThread.sleep(10000); // need to wait until background job of mariadb set the admin password
        
        String sql = 
                "CREATE DATABASE db_bpm_stor;\n" + 
                "CREATE DATABASE db_bpm_arch;\n" + 
                "CREATE OR REPLACE USER 'db_bpm_arch'@'%' IDENTIFIED BY 'nein';\n" + 
                "CREATE OR REPLACE USER 'db_bpm_stor'@'%' IDENTIFIED BY 'nein';\n" + 
                "GRANT ALL PRIVILEGES ON db_bpm_arch.* TO 'db_bpm_arch'@'%';\n" + 
                "GRANT ALL PRIVILEGES ON db_bpm_stor.* TO 'db_bpm_stor'@'%';\n" + 
                "quit"; 
        
        try (LogStream stream = scenario.exec("db","mysql -pnein", sql )) {
            stream.setCapture(true);
            scenario.waitForLogEntry(stream, "quit");
            String res = stream.getCaptured();
            assertFalse(res.contains("ERROR"));
        }
        
        // jms
        
        scenario.waitForLogEntry("jms", "activemq entered RUNNING state", 0);
        
        // karaf
        
        scenario.waitForLogEntry("karaf", "@karaf()>", 0);
        
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setFilter(new AnsiLogFilter());
            scenario.attach(stream, 
                "feature:repo-add activemq 5.15.8\n" +
                "feature:repo-add mvn:org.apache.shiro/shiro-features/"+prop.getString("shiro.version")+"/xml/features\n" + 
                "feature:repo-add mvn:de.mhus.osgi/mhus-features/"+prop.getString("mhus-parent.version")+"/xml/features\n" +
                "feature:repo-add mvn:de.mhus.cherry.reactive/reactive-feature/"+prop.getString("cherry-reactive.version")+"/xml/features\n" +
                "feature:install mhu-dev cherry-reactive-all cherry-reactive-dev\n" +
                "a=HGDFhjasdhj\n" );
        
            scenario.waitForLogEntry(stream, "Done.");
        }
        // installing activemq is asynchron, need to wait a while before next step
        MThread.sleep(10000);
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setFilter(new AnsiLogFilter());

            scenario.attach(stream, 
                    "bundle:install -s mvn:de.mhus.lib.itest/examples-reactive/"+prop.getString("project.version")+"\n" +
                    "a=HGDFhjasdhz\n" );
            scenario.waitForLogEntry(stream, "HGDFhjasdhz");
        }
        
        scenario.waitForLogEntry("karaf", "Done.", 1);
        
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setFilter(new AnsiLogFilter());
            stream.setCapture(true);
            scenario.attach(stream, 
                    "list\n" +
                    "a=HGDFhjasdhx\n" );
            scenario.waitForLogEntry(stream, "HGDFhjasdhx");
            
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
            
            assertTrue(out.contains("db-core"));
            assertTrue(out.contains("db-karaf"));
            assertTrue(out.contains("db-osgi-api"));
            assertTrue(out.contains("db-osgi-adb"));

            assertTrue(out.contains("rest-core"));
            assertTrue(out.contains("rest-osgi"));
            assertTrue(out.contains("rest-karaf"));
            
            assertTrue(out.contains("vaadin-core"));
            assertTrue(out.contains("vaadin-osgi-desktop"));
            assertTrue(out.contains("vaadin-osgi-theme"));
            assertTrue(out.contains("vaadin-osgi-bridge"));
            assertTrue(out.contains("vaadin-karaf-bridge"));
            assertTrue(out.contains("vaadin-timerextension"));
            
            assertTrue(out.contains("reactive-osgi"));
            assertTrue(out.contains("reactive-rest"));
            assertTrue(out.contains("reactive-karaf"));
            assertTrue(out.contains("reactive-vaadin-core"));
            assertTrue(out.contains("reactive-vaadin-widgets"));
            
            assertTrue(out.contains("examples-reactive"));
        }
        
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            stream.setFilter(new AnsiLogFilter());
            scenario.attach(stream, 
                    "dev-res -y cp default\n" +
                    "dev-res -y cp examples-reactive-single\n" +
                    "a=kjshkjfhjkIUYJGHJK\n" );

            scenario.waitForLogEntry(stream, "kjshkjfhjkIUYJGHJK");
            MThread.sleep(5000); // a long time - wait for configuration manager

            String out = stream.getCaptured();

//            assertTrue(out.contains("[doConfigure]"));
            assertTrue(out.contains("[KarafCfgManager::Register PID][de.mhus.osgi.api.services.PersistentWatch]"));
        }
        
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            scenario.attach(stream, 
                    "access restart\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVU\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVU");
        }

        int port = scenario.get("selenium").getPortBinding(4444);
        driver = new RemoteWebDriver(new URL("http://localhost:" + port + "/wd/hub"), DesiredCapabilities.chrome());
        
        
        // wait for engine to start
        
        boolean started = false;
        for (int i = 0 ; i < 20; i++) {
            MThread.sleep(2000);
            try (LogStream stream = new LogStream(scenario, "karaf")) {
                stream.setCapture(true);
                
                scenario.attach(stream, 
                        "pengine status\n" +
                        "a=quiweyBNVNB\n" );
    
                scenario.waitForLogEntry(stream, "quiweyBNVNB");
    
                String out = stream.getCaptured();
                if (out.contains("RUNNING")) {
                    started = true;
                    break;
                }
                assertTrue(out.contains("STOPPED"));
            }
        }
        assertTrue(started,"Engine not starting in time");
        
        
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "pdeploy de.mhus.cherry.reactive.examples.simple1.S1Process:0.0.1\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("[de.mhus.cherry.reactive.examples.simple1.S1Pool]"));
            assertTrue(out.contains("[de.mhus.cherry.reactive.examples.simple1.S1Pool2]"));
        }
        
    }
    
    @AfterAll
    public static void stopDocker() {
        if (prop.getBoolean("docker.destroy.containers", true))
            scenario.destroy();
    }
    
}
