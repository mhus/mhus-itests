package de.mhus.lib.itest.cases;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import com.github.dockerjava.api.exception.DockerException;

import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MThread;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.tests.TestCase;
import de.mhus.lib.tests.docker.DockerScenario;
import de.mhus.lib.tests.docker.Karaf;
import de.mhus.lib.tests.docker.LogStream;

@TestMethodOrder(OrderAnnotation.class)
public class KarafCryptTest extends TestCase {

    private static DockerScenario scenario;
    private static MProperties prop;

    @Test
    @Order(10)
    public void testCipherList() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                    "crypt:cipher x list\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("AESWITHRSA-JCE-01"));
            assertTrue(out.contains("RSA-JCE-01"));
            assertTrue(out.contains("AESWITHRSA-BC-01"));
            assertTrue(out.contains("AES-JCE-01"));
            assertTrue(out.contains("RSA-BC-01"));
        }
    }
    
    @Test
    @Order(11)
    public void testSignerList() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                    "crypt:signer x list\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("DSA-BC-01"));
            assertTrue(out.contains("ECC-BC-01"));
            assertTrue(out.contains("DSA-JCE-01"));
        }
    }
    
    @Test
    @Order(20)
    public void testCipherRsaBc512() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                    "crypt:cipher -p secret RSA-BC-01 test length=512\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("Valide: true"));
        }
    }
    
    @Test
    @Order(21)
    public void testCipherRsaBc1024() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                    "crypt:cipher -p secret RSA-BC-01 test length=1024\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("Valide: true"));
        }
    }
    
    @Test
    @Order(22)
    public void testCipherRsaBc2048() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                    "crypt:cipher -p secret RSA-BC-01 test length=2048\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("Valide: true"));
        }
    }

    @Test
    @Order(23)
    public void testCipherRsaBc4096() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                    "crypt:cipher -p secret RSA-BC-01 test length=4096\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("Valide: true"));
        }
    }
    
    @Test
    @Order(24)
    public void testCipherRsaBc8192() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                    "crypt:cipher -p secret RSA-BC-01 test length=8192\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("Valide: true"));
        }
    }
    
    
    @Test
    @Order(30)
    public void testCipherRsaJce512() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                    "crypt:cipher -p secret RSA-JCE-01 test length=512\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("Valide: true"));
        }
    }
    
    @Test
    @Order(31)
    public void testCipherRsaJce1024() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                    "crypt:cipher -p secret RSA-JCE-01 test length=1024\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("Valide: true"));
        }
    }
    
    @Test
    @Order(32)
    public void testCipherRsaJce2048() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                    "crypt:cipher -p secret RSA-JCE-01 test length=2048\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("Valide: true"));
        }
    }

    @Test
    @Order(33)
    public void testCipherRsaJce4096() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                    "crypt:cipher -p secret RSA-JCE-01 test length=4096\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("Valide: true"));
        }
    }
    
    @Test
    @Order(34)
    public void testCipherRsaJce8192() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                    "crypt:cipher -p secret RSA-JCE-01 test length=8192\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("Valide: true"));
        }
    }
    
    @Test
    @Order(40)
    public void testCipherAesJce256() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                    "crypt:cipher AES-JCE-01 test length=256\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("Valide: true"));
        }
    }
    
    @Test
    @Order(41)
    public void testCipherAesJce128() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                    "crypt:cipher AES-JCE-01 test length=128\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("Valide: true"));
        }
    }
    
    @Test
    @Order(42)
    public void testCipherAesJce192() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                    "crypt:cipher AES-JCE-01 test length=192\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("Valide: true"));
        }
    }
    
    @Test
    @Order(50)
    public void testCipherAesWithRsaJce() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                    "crypt:cipher -p secret AESWITHRSA-JCE-01 test \n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("Valide: true"));
        }
    }    
    
    @Test
    @Order(60)
    public void testCipherAesWithRsaBc() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                    "crypt:cipher -p secret AESWITHRSA-BC-01 test\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("Valide: true"));
        }
    }    
    
    
    @Test
    @Order(100)
    public void testSignerDsaBc1024() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                    "crypt:signer -p secret DSA-BC-01 test length=1024\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("Valide: true"));
        }
    }    

    @Test
    @Order(101)
    public void testSignerDsaBc512() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                    "crypt:signer -p secret DSA-BC-01 test length=512\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("Valide: true"));
        }
    }    
    
    @Test
    @Order(110)
    public void testSignerDsaJce1024() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                    "crypt:signer -p secret DSA-JCE-01 test length=1024\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("Valide: true"));
        }
    }    

    @Test
    @Order(111)
    public void testSignerDsaJce512() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                    "crypt:signer -p secret DSA-JCE-01 test length=512\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("Valide: true"));
        }
    }    
    
    @Test
    @Order(120)
    public void testSignerEccBc_prime192v1() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                    "crypt:signer -p secret ECC-BC-01 test stdName=prime192v1\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("Valide: true"));
        }
    }    
    
    @Test
    @Order(121)
    public void testSignerEccBc_secp521r1() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                    "crypt:signer -p secret ECC-BC-01 test stdName=secp521r1\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("Valide: true"));
        }
    }    
    
    @Test
    @Order(122)
    public void testSignerEccBc_c2tnb431r1() throws NotFoundException, DockerException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                    "crypt:signer -p secret ECC-BC-01 test stdName=c2tnb431r1\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVJ\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVJ");

            String out = stream.getCaptured();

            assertTrue(out.contains("Valide: true"));
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

//        scenario.waitForLogEntry("karaf", "@karaf()>", 0);
        scenario.waitForLogEntry("karaf", "Done.", 0);
        
        try (LogStream stream = scenario.exec("karaf", "ls /home/user/.m2" )) {
            // scenario.waitForLogEntry(stream, "repository");
            String res = stream.readAll();
            assertTrue(res.contains("repository"));
            assertTrue(res.contains("settings.xml")); // must be there since karaf is in debug and mounted local .m2 directory
        }
        
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                "feature:repo-add mvn:org.apache.shiro/shiro-features/"+prop.getString("shiro.version")+"/xml/features\n" + 
                "feature:repo-add mvn:de.mhus.osgi/mhus-features/"+prop.getString("mhus-parent.version")+"/xml/features\n" +
                "feature:install mhu-crypt mhu-dev\n" +
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
            
            assertTrue(out.contains("osgi-crypt-api"));
            assertTrue(out.contains("osgi-crypt-bc"));
            assertTrue(out.contains("osgi-services"));
        }
        
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "dev-res -y cp default\n" +
                    "a=kjshkjfhjkIUYJGHJK\n" );

            scenario.waitForLogEntry(stream, "kjshkjfhjkIUYJGHJK");
            MThread.sleep(10000); // a long time - wait for configuration manager

            String out = stream.getCaptured();

//            assertTrue(out.contains("[doConfigure]"));
            assertTrue(out.contains("[KarafCfgManager::Register PID]"));
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
        if (prop.getBoolean("docker.destroy.containers", true))
            scenario.destroy();
    }

}
