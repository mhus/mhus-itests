package de.mhus.lib.itest.cases;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.LinkedList;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MThread;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.tests.TestCase;
import de.mhus.lib.tests.docker.AnsiLogFilter;
import de.mhus.lib.tests.docker.DockerScenario;
import de.mhus.lib.tests.docker.Karaf;
import de.mhus.lib.tests.docker.LogStream;

@TestMethodOrder(OrderAnnotation.class)
public class KarafJmsTest extends TestCase {

    private static DockerScenario scenario;
    private static MProperties prop;

    @Test
    @Order(1)
    public void testJmsConnection() throws NotFoundException, IOException, InterruptedException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "jms:connection-list -ta\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("tcp://jmsserver:61616"));
            // could be closed until first usage - assertTrue(out.split("\n")[2].split("\\|")[4].trim().equals("true"));
        }
    }
    
    @Test
    @Order(2)
    public void testJmsChannel() throws NotFoundException, IOException, InterruptedException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "jms:channel-list -ta\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("TestJmsServer"));
            assertTrue(out.contains("/queue/test"));
        }
    }
    
    @Test
    @Order(3)
    public void testSendDirect() throws NotFoundException, IOException, InterruptedException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "jms:direct-send test test Hello\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "GET ONE WAY: ActiveMQTextMessage");

            String out = stream.getCaptured();
            assertTrue(out.contains("Hello"));
        }
    }

    @Test
    @Order(4)
    public void testSendClient() throws NotFoundException, IOException, InterruptedException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "itest:jms client test test Ribonucleicacid\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "From");

            String out = stream.getCaptured();
            assertTrue(out.contains("text = Ribonucleicacid"));
            assertTrue(out.contains("Text                 Ribonucleicacid"));
        }
    }
    
    @Test
    @Order(5)
    public void testSendJson() throws NotFoundException, IOException, InterruptedException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "itest:jms json test test.json Ribonucleicacid\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "Properties");

            String out = stream.getCaptured();
            assertTrue(out.contains("GET: {\"value\":\"Ribonucleicacid\"}"));
            assertTrue(out.contains("RES: {\"value\":\"Ribonucleicacid\"}"));
        }
    }
    
    @Test
    @Order(6)
    public void testSendJsonObject() throws NotFoundException, IOException, InterruptedException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "itest:jms book test test.obj Ribonucleicacid\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "Properties");

            String out = stream.getCaptured();
            assertTrue(out.contains("GET0: [Book:[Ribonucleicacid]]"));
            assertTrue(out.contains("RES: [Book:[Ribonucleicacid]]"));
        }
    }

    // Service calls are currently not working
//    @Test
//    @Order(7)
//    public void testSendService() throws NotFoundException, IOException, InterruptedException {
//        try (LogStream stream = new LogStream(scenario, "karaf")) {
//            stream.setCapture(true);
//            
//            scenario.attach(stream, 
//                    "itest:jms service.checkout test test.service 1234 Ribonucleicacid\n" +
//                    "a=quiweyBNVNB\n" );
//
//            scenario.waitForLogEntry(stream, "Properties");
//
//            String out = stream.getCaptured();
//            assertTrue(out.contains("GET0: [Book:[Ribonucleicacid]]"));
//            assertTrue(out.contains("RES: [Book:[Ribonucleicacid]]"));
//        }
//    }
    
    @Test
    @Order(50)
    public void testStress0() throws NotFoundException, IOException, InterruptedException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            String load = "1001";
            String rounds = "1002";
            
            scenario.attach(stream, 
                    "itest:jms stress test test " + load + " " + rounds + " X\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("Load: " + load));
            assertTrue(out.contains("Rounds: " + rounds));
            assertTrue(out.contains("jms="));
            assertFalse(out.contains("Exception"));
        }
    }
    
    @Test
    @Order(51)
    public void testStress1() throws NotFoundException, IOException, InterruptedException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            String load = "1000001";
            String rounds = "1002";
            
            scenario.attach(stream, 
                    "itest:jms stress test test " + load + " " + rounds + " Y\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("Load: " + load));
            assertTrue(out.contains("Rounds: " + rounds));
            assertTrue(out.contains("jms="));
            assertFalse(out.contains("Exception"));
        }
    }

    @Test
    @Order(52)
    public void testStress2() throws NotFoundException, IOException, InterruptedException {
        String load = "10001";
        String rounds = "20001";
        int parallel = 5; // not more parallel ssh connections
        
        LinkedList<Job> jobs = new LinkedList<>();
        
        for (int i = 0; i < parallel; i++) {
            Job job = new Job();
            Thread thread = new Thread(job);
            job.thread = thread;
            job.nr = i;
            job.load = load;
            job.rounds = rounds;
            jobs.add(job);
            thread.start();
        }
        
        // wait
        while (true) {
            boolean alive = false;
            for (Job job : jobs) {
                if (job.thread.isAlive()) {
                    alive = true;
                    System.out.println("=== Running: " + job.nr);
                }
            }
            if (!alive) break;
            MThread.sleep(10000);
        }
            
        boolean error = false;
        for (Job job : jobs) {
            if (job.error != null) {
                System.err.println("Error in " + job.nr);
                error = true;
            }
        }
        assertFalse(error);
    }
    
    private static class Job implements Runnable {

        public String rounds;
        public String load;
        public int nr;
        public Thread thread;
        public Throwable error;

        @Override
        public void run() {
            try {
                MThread.sleep((int)(5000d * Math.random()) );
                System.out.println(">>> Start " + nr);
                try (LogStream stream = new LogStream(scenario, "karaf")) {
                    stream.setCapture(true);
                    
                    scenario.exec(stream, new String[] {"/opt/karaf/bin/client"},null,false,null,null,
                            "\n\n\nitest:jms stress test test " + load + " " + rounds + " " + nr + "\na=JKHIUY\na=${a}675GH\n");
                    scenario.waitForLogEntry(stream, "JKHIUY675GH");
    
                    String out = stream.getCaptured();
                    assertTrue(out.contains("Load: " + load));
                    assertTrue(out.contains("Rounds: " + rounds));
                    assertTrue(out.contains("jms="));
                    assertFalse(out.contains("Exception"));
                }
            } catch (Throwable t) {
                t.printStackTrace();
                error = t;
            }
            System.out.println("<<< Finished " + nr);
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

        scenario.add(new Karaf("karaf", prop.getString("docker.mhus-apache-karaf.version"), 
                "debug", 
                "link:jms:jmsserver"
                ));
        
        scenario.destroyPrefix();
        scenario.start();
        
        MThread.sleep(1000);

        // jms
        scenario.waitForLogEntry("jms", "activemq entered RUNNING state", 0);
        
        // karaf
//        scenario.waitForLogEntry("karaf", "@karaf()>", 0);
        scenario.waitForLogEntry("karaf", "Done.", 0);
        
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setFilter(new AnsiLogFilter());
            scenario.attach(stream, 
                "feature:repo-add activemq 5.15.8\n" +
                "feature:repo-add mvn:org.apache.shiro/shiro-features/"+prop.getString("shiro.version")+"/xml/features\n" + 
                "feature:repo-add mvn:de.mhus.osgi/mhus-features/"+prop.getString("mhus-parent.version")+"/xml/features\n" +
                "feature:install mhu-base mhu-dev mhu-jms\n" +
                "a=HGDFhjasdhj\n" );
        
            scenario.waitForLogEntry(stream, "Done.");
        }
        // installing activemq is asynchron, need to wait a while before next step
        MThread.sleep(10000);
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setFilter(new AnsiLogFilter());

            scenario.attach(stream, 
                    "bundle:install -s mvn:de.mhus.lib.itest/examples-jms/"+prop.getString("project.version")+"\n" +
                    "a=HGDFhjasdhz\n" );
            scenario.waitForLogEntry(stream, "HGDFhjasdhz");
        }
        MThread.sleep(5000);
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
            
            assertTrue(out.contains("jms-core"));
            assertTrue(out.contains("jms-osgi-api"));
            assertTrue(out.contains("examples-jms"));
        }
        
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            stream.setFilter(new AnsiLogFilter());
            scenario.attach(stream, 
                    "dev-res -y cp default\n" +
                    "dev-res -y cp examples-jms\n" +
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
        
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            scenario.attach(stream, 
                    "channel-beat\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVU\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVU");
        }
        MThread.sleep(5000);
        
    }
    
    @AfterAll
    public static void stopDocker() {
        if (prop.getBoolean("docker.destroy.containers", true))
            scenario.destroy();
    }

    
}
