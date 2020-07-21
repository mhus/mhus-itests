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
public class SimpleTest extends TestCase {

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
    public void testInstallFeature() throws NotFoundException, DockerException, InterruptedException, IOException {
        System.out.println("Install features");
        try (LogStream stream = scenario.attach("karaf", 
                "feature:repo-add mvn:org.apache.shiro/shiro-features/1.5.1/xml/features\n" + 
                "feature:repo-add mvn:de.mhus.osgi/mhus-features/7.1.0-SNAPSHOT/xml/features\n" +
                "feature:install mhu-base\n" )) {
            scenario.waitForLogEntry(stream, "Hello World");
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
        scenario.add(new Karaf("karaf", null, "debug"));
        
        scenario.destroyPrefix();
        scenario.start();
        
        MThread.sleep(1000);
        
    }
    
    @AfterAll
    public static void stopDocker() {
        // scenario.destroy();
    }
    
    
}
