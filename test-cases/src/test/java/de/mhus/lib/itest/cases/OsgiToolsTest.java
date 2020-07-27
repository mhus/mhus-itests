package de.mhus.lib.itest.cases;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
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
public class OsgiToolsTest extends TestCase {

    private static DockerScenario scenario;
    private static MProperties prop;

    @Test
    @Order(10)
    public void testAccessStart() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            scenario.attach(stream, 
                    "access restart\n" +
                    "access id\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("null"));
        }
    }

    @Test
    @Order(11)
    public void testAccessAdmin() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            
            scenario.attach(stream, 
                    "access admin\n" +
                    "access id\n" +
                    "a=JKHHJKkjhkjhHJKHJ\n" );

            scenario.waitForLogEntry(stream, "JKHHJKkjhkjhHJKHJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("[?1000l[?2004ladmin"));
        }
    }
    
    @Test
    @Order(20)
    public void testTimerList() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            
            scenario.attach(stream, 
                    "timer list\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("Scheduled/Thread"));
            assertTrue(out.contains("housekeeper:de.mhus.lib.core.jmx.MRemoteManager.Housekeeper"));
        }
    }
    
    @Test
    @Order(30)
    public void testCacheList() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            
            scenario.attach(stream, 
                    "cache list\n" +
                    "a=poiVCXkljERT\n" );

            scenario.waitForLogEntry(stream, "poiVCXkljERT");

            String out = stream.getCaptured();
            assertTrue(out.contains("Bytes"));
            assertTrue(out.contains("osgi-services:"));
            assertTrue(out.contains("/baseApi"));
        }
    }
    
    @Test
    @Order(30)
    public void testSimpleServices() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            
            scenario.attach(stream, 
                    "simpleservice list\n" +
                    "a=ERERRYhjhgfj\n" );

            scenario.waitForLogEntry(stream, "ERERRYhjhgfj");

            String out = stream.getCaptured();
            assertTrue(out.contains("Info"));
            assertTrue(out.contains("de.mhus.osgi.services.VaultManagerImpl"));
            assertTrue(out.contains("de.mhus.osgi.services.deploy.DeployServiceManager"));
        }
    }
    
    @Test
    @Order(40)
    public void testBundleList() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            
            scenario.attach(stream, 
                    "ll -l -m\n" +
                    "a=quiPOIUgvbBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiPOIUgvbBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("Modified"));
            assertTrue(out.contains("org.apache.felix.framework"));
            String[] lines = out.split("\n");
            assertTrue(lines.length > 5);
            assertTrue(lines[lines.length-1].contains("karaf-dev")); // last installed bundle
        }
    }
    
    @Test
    @Order(41)
    public void testBundleWatch() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            
            scenario.attach(stream, 
                    "bundle:watch\n" +
                    "a=mbnBNVyiuGHF\n" );

            scenario.waitForLogEntry(stream, "mbnBNVyiuGHF");

            String out = stream.getCaptured();
            assertTrue(out.contains("Watched URLs/IDs"));
            assertTrue(out.contains(".*"));
        }
    }
    
    @Test
    @Order(42)
    public void testHealtCheck() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            
            scenario.attach(stream, 
                    "healthcheck\n" +
                    "a=TYURTYTYTGHBNHJV\n" );

            scenario.waitForLogEntry(stream, "TYURTYTYTGHBNHJV");

            String out = stream.getCaptured();
            assertTrue(out.contains("OSGi Framework Ready Check"));
            assertTrue(out.contains("INFO"));
            assertTrue(out.contains("OK"));
        }
    }
    
    @Test
    @Order(43)
    public void testJcaInfo() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            
            scenario.attach(stream, 
                    "jcainfo\n" +
                    "a=iouuiouohjghjDFSDF\n" );

            scenario.waitForLogEntry(stream, "iouuiouohjghjDFSDF");

            String out = stream.getCaptured();
            assertTrue(out.contains("SunPKCS11"));
            assertTrue(out.contains("BouncyCastle Security Provider"));
        }
    }

    @Test
    @Order(44)
    public void testMem() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            
            scenario.attach(stream, 
                    "mem\n" +
                    "a=mbnB12NVyiuGHF\n" );

            scenario.waitForLogEntry(stream, "mbnB12NVyiuGHF");

            String out = stream.getCaptured();
            assertTrue(out.contains("Free:"));
            assertTrue(out.contains("Total:"));
            assertTrue(out.contains("Max:"));
        }
    }
    
    @Test
    @Order(45)
    public void testUptime() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            
            scenario.attach(stream, 
                    "uptime\n" +
                    "a=mbnB13NVyiuGHF\n" );

            scenario.waitForLogEntry(stream, "mbnB13NVyiuGHF");

            String out = stream.getCaptured();
            assertTrue(out.contains("Runtime"));
            assertTrue(out.contains("CURRENT"));
        }
    }
    
    @Test
    @Order(60)
    public void testMhusConfigDump() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            
            scenario.attach(stream, 
                    "mhus:config dump\n" +
                    "a=mbnB100NVyiuGHF\n" );

            scenario.waitForLogEntry(stream, "mbnB100NVyiuGHF");

            String out = stream.getCaptured();
            assertTrue(out.contains("de.mhus.lib.core.mapi.MCfgManager.CentralMhusCfgProvider system"));
            assertTrue(out.contains("de.mhus.lib.mutable.KarfConfigProvider org.ops4j.pax.logging"));
        }
    }

    @Test
    @Order(61)
    public void testConsoleInfo() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            
            scenario.attach(stream, 
                    "console info  \n" +
                    "a=mbnB1001NVyiuGHF\n" );

            scenario.waitForLogEntry(stream, "mbnB1001NVyiuGHF");

            String out = stream.getCaptured();
            assertTrue(out.contains("Type      : de.mhus.osgi.api.karaf.KarafConsole"));
            assertTrue(out.contains("Ansi : true"));
        }
    }
    
    @Test
    @Order(62)
    public void testIdent() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            
            scenario.attach(stream, 
                    "ident\n" +
                    "a=mbnB102NVyiuGHF\n" );

            scenario.waitForLogEntry(stream, "mbnB102NVyiuGHF");

            String out = stream.getCaptured();
            assertTrue(out.contains("karaf-"));
            assertTrue(out.contains("@karaf"));
        }
    }

    @Test
    @Order(63)
    public void testKeychain() throws NotFoundException, DockerException, InterruptedException, IOException {
        String id = null;
        String secret = "Secret" + Math.random();
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            
            scenario.attach(stream, 
                    "keychain list\n" +
                    "a=mbnB163NVyiuGHF\n" );

            scenario.waitForLogEntry(stream, "mbnB163NVyiuGHF");

            String out = stream.getCaptured();
            assertTrue(out.contains("Source"));
            assertFalse(out.contains("Test"));
        }
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            
            scenario.attach(stream, 
                    "keychain add test Test TDesc "+secret+"\n" +
                    "a=mbnB163ANVyiuGHF\n" );

            scenario.waitForLogEntry(stream, "mbnB163ANVyiuGHF");

            String out = stream.getCaptured();
            assertTrue(out.contains("Created"));
            String[] lines = out.split("\n");
            id = lines[2].trim();
            System.out.println(">>> ID: " + id);
        }
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            
            scenario.attach(stream, 
                    "keychain list\n" +
                    "a=mbnB163BNVyiuGHF\n" );

            scenario.waitForLogEntry(stream, "mbnB163BNVyiuGHF");

            String out = stream.getCaptured();
            assertTrue(out.contains("Source"));
            assertTrue(out.contains("Test"));
        }
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            
            scenario.attach(stream, 
                    "keychain get "+id+"\n" +
                    "a=mbnB163CNVyiuGHF\n" );

            scenario.waitForLogEntry(stream, "mbnB163CNVyiuGHF");

            String out = stream.getCaptured();
            assertTrue(out.contains("test"));
            assertTrue(out.contains("Test"));
            assertTrue(out.contains("TDesc"));
            assertTrue(out.contains(secret));
        }
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            
            scenario.attach(stream, 
                    "keychain save\n" +
                    "a=mbnB163ENVyiuGHF\n" );
            
            scenario.waitForLogEntry(stream, "mbnB163ENVyiuGHF");
            
            String out = stream.getCaptured();
            assertTrue(out.contains("OK"));
        }
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            
            scenario.attach(stream, 
                    "keychain remove "+id+"\n" +
                    "a=mbnB163DNVyiuGHF\n" );

            scenario.waitForLogEntry(stream, "mbnB163DNVyiuGHF");

            String out = stream.getCaptured();
            assertTrue(out.contains("OK"));
        }
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            
            scenario.attach(stream, 
                    "keychain list\n" +
                    "a=mbnB163FNVyiuGHF\n" );

            scenario.waitForLogEntry(stream, "mbnB163FNVyiuGHF");

            String out = stream.getCaptured();
            assertTrue(out.contains("Source"));
            assertFalse(out.contains("Test"));
        }
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            
            scenario.attach(stream, 
                    "keychain load\n" +
                    "a=mbnB163GNVyiuGHF\n" );
            
            scenario.waitForLogEntry(stream, "mbnB163GNVyiuGHF");
            
            String out = stream.getCaptured();
            assertTrue(out.contains("OK"));
        }
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            
            scenario.attach(stream, 
                    "keychain list\n" +
                    "a=mbnB163HNVyiuGHF\n" );

            scenario.waitForLogEntry(stream, "mbnB163HNVyiuGHF");

            String out = stream.getCaptured();
            assertTrue(out.contains("Source"));
            assertTrue(out.contains("Test"));
        }
    }

    @Test
    @Order(64)
    public void testLockList() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            
            scenario.attach(stream, 
                    "bundle:watch\n" +
                    "a=mbnB64NVyiuGHF\n" );

            scenario.waitForLogEntry(stream, "mbnB64NVyiuGHF");

            String out = stream.getCaptured();
            assertTrue(out.contains("Locked"));
        }
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
        scenario.add(new Karaf("karaf", prop.getString("docker.mhus-apache-karaf.version"), "debug"));
        
        scenario.destroyPrefix();
        scenario.start();
        
        MThread.sleep(1000);

        scenario.waitForLogEntry("karaf", "@karaf()>", 0);
        
        try (LogStream stream = scenario.exec("karaf", "ls /home/user/.m2" )) {
            // scenario.waitForLogEntry(stream, "repository");
            String res = stream.readAll();
            assertTrue(res.contains("repository"));
            assertTrue(res.contains("settings.xml")); // must be there since karaf is in debug and mounted local .m2 directory
        }
        
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCaputre(true);
            scenario.attach(stream, 
                "feature:repo-add mvn:org.apache.shiro/shiro-features/"+prop.getString("shiro.version")+"/xml/features\n" + 
                "feature:repo-add mvn:de.mhus.osgi/mhus-features/"+prop.getString("mhus-parent.version")+"/xml/features\n" +
                "feature:install mhu-base mhu-dev\n" +
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
            
        }
        
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
    
    @AfterAll
    public static void stopDocker() {
        // scenario.destroy();
    }
    
    
}
