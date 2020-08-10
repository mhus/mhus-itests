package de.mhus.lib.itest.cases;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.github.dockerjava.api.exception.DockerException;

import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MThread;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.tests.TestCase;
import de.mhus.lib.tests.docker.DockerScenario;
import de.mhus.lib.tests.docker.Karaf;
import de.mhus.lib.tests.docker.LogStream;

@TestMethodOrder(OrderAnnotation.class)
public class KarafTransformTest extends TestCase {

    private static DockerScenario scenario;
    private static MProperties prop;

    @Test
    @Order(10)
    public void testTransformTest() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                    "transform:test\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();
            
            String[] parts = out.split("======================");
            assertTrue(parts.length > 1);
            
            for (int i = 1; i < parts.length; i = i + 2) {
                String name = parts[i].trim();
                System.out.println(">>> " + name);
                assertTrue(parts[i+1].contains(">>> Transform successful"));
            }

//            assertTrue(out.contains("null"));
        }

    }

    @BeforeAll
    public static void startDocker() throws NotFoundException, IOException, InterruptedException {
        
        prop = TestUtil.loadProperties();
        
        scenario = new DockerScenario();
        scenario.add(new Karaf("karaf", prop.getString("docker.mhus-apache-karaf.version"), "debug"));
        
        scenario.destroyPrefix();
        scenario.start();
        
        MThread.sleep(1000);

        try (LogStream stream = scenario.exec("karaf", 
                new String[] {"bash","-c","apt-get update && apt-get install -y xpdf && apt-get install -y libreoffice"}, 
                null, false, "root", null, null
                )) {
            @SuppressWarnings("unused")
            String res = stream.readAll();
        }
        
        scenario.waitForLogEntry("karaf", "@karaf()>", 0);
        
        try (LogStream stream = scenario.exec("karaf", "ls /home/user/.m2" )) {
            // scenario.waitForLogEntry(stream, "repository");
            String res = stream.readAll();
            System.out.println();
            System.out.println("----");
            System.out.println(res);
            System.out.println();
            System.out.println("----");
            
            assertTrue(res.contains("repository"));
            assertTrue(res.contains("settings.xml")); // must be there since karaf is in debug and mounted local .m2 directory
        }
        
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                "feature:repo-add mvn:org.apache.shiro/shiro-features/"+prop.getString("shiro.version")+"/xml/features\n" + 
                "feature:repo-add mvn:de.mhus.osgi/mhus-features/"+prop.getString("mhus-parent.version")+"/xml/features\n" +
                "feature:install mhu-transform mhu-dev\n" +
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
            
            assertTrue(out.contains("transform-api"));
            assertTrue(out.contains("transform-birt"));
            assertTrue(out.contains("transform-core"));
            assertTrue(out.contains("transform-freemarker"));
            assertTrue(out.contains("transform-jtwig"));
            assertTrue(out.contains("transform-pdf"));
            assertTrue(out.contains("transform-soffice"));
            assertTrue(out.contains("transform-velocity"));
        }
        
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "dev-res -y cp default\n" +
                    "a=kjshkjfhjkIUYJGHJK\n" );

            scenario.waitForLogEntry(stream, "kjshkjfhjkIUYJGHJK");
            MThread.sleep(5000); // a long time - wait for configuration manager

//            String out = stream.getCaptured();

//            assertTrue(out.contains("[doConfigure]"));
//            assertTrue(out.contains("[KarafCfgManager::Register PID][de.mhus.osgi.api.services.PersistentWatch]"));
        }

//        try (LogStream stream = new LogStream(scenario, "karaf")) {
//            scenario.attach(stream, 
//                    "access restart\n" +
//                    "a=HJGPODGHHKJNBHJGJHHJVU\n" );
//
//            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVU");
//
//        }

    }
    
    @AfterAll
    public static void stopDocker() {
        if (prop.getBoolean("docker.destroy.containers", true))
            scenario.destroy();
    }
    
    
}
