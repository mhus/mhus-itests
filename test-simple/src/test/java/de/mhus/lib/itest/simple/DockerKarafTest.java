package de.mhus.lib.itest.simple;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.github.dockerjava.api.exception.DockerException;

import de.mhus.lib.core.MThread;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.tests.TestCase;
import de.mhus.lib.tests.docker.DockerScenario;
import de.mhus.lib.tests.docker.Karaf;
import de.mhus.lib.tests.docker.LogStream;

@TestMethodOrder(OrderAnnotation.class)
public class DockerKarafTest extends TestCase {

    private static DockerScenario scenario;

    @Test
    @Order(1)
    public void testLog() throws NotFoundException, DockerException, InterruptedException, IOException {
        scenario.waitForLogEntry("karaf", "@karaf()>", 0);
    }
    
    @Test
    @Order(2)
    public void testAttach() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = scenario.attach("karaf", "echo \"Hello World\"\n" )) {
            scenario.waitForLogEntry(stream, "Hello World");
        }
    }
    
    @Test
    @Order(3)
    public void testExec() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = scenario.exec("karaf", "ls /home/user/.m2" )) {
            // scenario.waitForLogEntry(stream, "repository");
            String res = stream.readAll();
            assertTrue(res.contains("repository"));
            assertTrue(res.contains("settings.xml")); // must be there since karaf is in debug and mounted local .m2 directory
        }
    }
    
    @Test
    @Order(10)
    public void testInstallMhusFeature() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            scenario.attach(stream, 
                "feature:repo-add mvn:org.apache.shiro/shiro-features/"+System.getenv("shiro.version")+"/xml/features\n" + 
                "feature:repo-add mvn:de.mhus.osgi/mhus-features/"+System.getenv("mhus-parent.version")+"/xml/features\n" +
                "feature:install mhu-base\n" +
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
            
        }
    }
    
    @Test
    @Order(11)
    public void testInstallDevFeature() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            scenario.attach(stream, 
                "feature:install mhu-dev\n" +
                "list\n" +
                "a=kjhsHGJ\n" );
        
            scenario.waitForLogEntry(stream, "kjhsHGJ");
            
            String out = stream.getCaptured();
            
            int pos1 = out.indexOf("List Threshold");
            assertTrue(pos1 > 0);
            int pos2 = out.indexOf("@karaf()", pos1);
            assertTrue(pos2 > 0);
            
            out = out.substring(pos1, pos2);
            assertTrue(out.contains("karaf-dev"));
        }
    }
    
    @Test
    @Order(12)
    public void testInstallDevFiles() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            
            scenario.attach(stream, 
                    "dev-res -y cp default\n" +
                    "a=kjshkjfhjkIUYJGHJK\n" );

            scenario.waitForLogEntry(stream, "kjshkjfhjkIUYJGHJK");
            MThread.sleep(5000); // a long time - wait for configuration manager

            String out = stream.getCaptured();

            assertTrue(out.contains("[doConfigure]"));
            assertTrue(out.contains("[KarafCfgManager::Register PID][de.mhus.osgi.api.services.PersistentWatch]"));
        }
    }

    @BeforeAll
    public static void startDocker() {
        
//      System.out.println("ENV:");
//        System.out.println(System.getenv());
//        System.out.println("PROPS:");
//        System.out.println(System.getProperties());
        
        scenario = new DockerScenario();
//      scenario.add("jms", "webcenter/activemq:5.14.3", 
//              "env:ACTIVEMQ_CONFIG_NAME=amqp-srv1",
//              "env:ACTIVEMQ_CONFIG_DEFAULTACCOUNT=false",
//              "env:ACTIVEMQ_ADMIN_LOGIN=admin",
//              "env:ACTIVEMQ_ADMIN_PASSWORD=nein",
//              "env:ACTIVEMQ_CONFIG_MINMEMORY=1024",
//              "env:ACTIVEMQ_CONFIG_MAXMEMORY=4096",
//              "env:ACTIVEMQ_CONFIG_SCHEDULERENABLED=true"
//              );
//      
//      scenario.add("db", "mariadb:10.3", 
//              "e:MYSQL_ROOT_PASSWORD=nein"
//              );
        // "link:db:dbserver"
        scenario.add(new Karaf("karaf", System.getenv("docker.mhus-apache-karaf.version"), "debug"));
        
        scenario.destroyPrefix();
        scenario.start();
        
        MThread.sleep(1000);
        
    }
    
    @AfterAll
    public static void stopDocker() {
        // scenario.destroy();
    }
    
    
}
