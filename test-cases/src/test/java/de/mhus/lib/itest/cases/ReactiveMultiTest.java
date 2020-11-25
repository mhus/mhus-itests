package de.mhus.lib.itest.cases;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.EOFException;
import java.io.IOException;
import java.util.LinkedList;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.github.dockerjava.api.exception.DockerException;

import de.mhus.lib.core.M;
import de.mhus.lib.core.MCast;
import de.mhus.lib.core.MPeriod;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MStopWatch;
import de.mhus.lib.core.MThread;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.tests.TestCase;
import de.mhus.lib.tests.Warnings;
import de.mhus.lib.tests.docker.AnsiLogFilter;
import de.mhus.lib.tests.docker.DockerScenario;
import de.mhus.lib.tests.docker.Karaf;
import de.mhus.lib.tests.docker.LogStream;

@TestMethodOrder(OrderAnnotation.class)
public class ReactiveMultiTest extends TestCase {

    private static final int AMOUNT = MCast.toint(System.getenv("REACTIVE_MULTI_AMOUNT"), 2); // number of parallel instances
    private static final int STRESS_ROUNDS = MCast.toint(System.getenv("REACTIVE_MULTI_ROUNDS"), 10); // every round ca. 1 minute
    private static DockerScenario scenario;
    private static MProperties prop;

    @Test
    @Order(1)
    public void testList() throws NotFoundException, DockerException, InterruptedException, IOException {
        for (int a = 0; a < AMOUNT; a++) {
            try (LogStream stream = new LogStream(scenario, "karaf"+a)) {
                stream.setCapture(true);
                
                scenario.attach(stream, 
                        "pls -ta\n" +
                        "a=quiweyBNVNB\n" );
    
                scenario.waitForLogEntry(stream, "quiweyBNVNB");
    
                String out = stream.getCaptured();
                assertTrue(out.contains("de.mhus.app.reactive.examples.simple1.S1Process:0.0.1"));
                assertTrue(out.contains("enabled"));
            }
        }
    }
    
    @Test
    @Order(10)
    public void testStress() throws NotFoundException, DockerException, InterruptedException, IOException {
        // start stress
        
        String stress = "pstress -i 0 -m " + (AMOUNT * 100) + " \\\n" + 
                "'bpm://de.mhus.app.reactive.examples.simple1.S1Process:0.0.1/de.mhus.app.reactive.examples.simple1.S1Pool;customId=$cnt$;customerId=alf?text1=second' \\\n" + 
                "'bpm://de.mhus.app.reactive.examples.simple1.S1Process:0.0.1/de.mhus.app.reactive.examples.simple1.S1Pool;customId=$cnt$;customerId=alf?text1=third' \\\n" + 
                "'bpm://de.mhus.app.reactive.examples.simple1.S1Process:0.0.1/de.mhus.app.reactive.examples.simple1.S1Pool;customId=$cnt$;customerId=alf?text1=error1' \\\n" + 
                "'bpm://de.mhus.app.reactive.examples.simple1.S1Process:0.0.1/de.mhus.app.reactive.examples.simple1.S1Pool;customId=$cnt$;customerId=alf?text1=parallel1' \\\n" + 
                "'bpm://de.mhus.app.reactive.examples.simple1.S1Process:0.0.1/de.mhus.app.reactive.examples.simple1.S1Pool;customId=$cnt$;customerId=alf?text1=parallel2'\n" + 
                "";
        try (LogStream stream = scenario.exec("karaf0", new String[] {"/opt/karaf/bin/client"},null,false,null,null,stress)) {
            scenario.waitForLogEntry(stream, ">>> 10001:");
        }

        MStopWatch timerAll = new MStopWatch("all").start();
        int maxId = 9999;
        for (int i = 0; i < STRESS_ROUNDS; i++) {
            MThread.sleep(MPeriod.MINUTE_IN_MILLISECOUNDS);
            int lastMax = maxId;
            MStopWatch timerRound = new MStopWatch("round").start();
            try (LogStream stream = new LogStream(scenario, "karaf0")) {
                stream.setCapture(true);
                scenario.exec(stream, new String[] {"/opt/karaf/bin/client"},null,false,null,null,"pcase -ta list\na=JKHIUY\na=${a}675GH\n");
                scenario.waitForLogEntry(stream, "JKHIUY675GH");
                
                String out = stream.getCaptured();
                String[] lines = out.split("\n");
                for (int l = 0; l < lines.length; l++) {
                    String line = lines[l];
                    String[] cols = line.split("\\|");
                    if (cols.length > 2) {
                        int lastId = M.to(cols[1].trim(), 0);
                        maxId = Math.max(maxId, lastId);
                    }
                }
            }
            
            System.out.println();
            System.out.println("--------------------------");
            System.out.println(" Round   : " + i);
            System.out.println(" MaxId   : " + maxId);
            System.out.println(" Created : " + (maxId-lastMax) );
            System.out.println(" Duration: " + timerRound);
            System.out.println(" Duration: " + timerAll);
            System.out.println("--------------------------");
        }
        
        // stop stress
        try (LogStream stream = scenario.exec("karaf0", new String[] {"/opt/karaf/bin/client"},null,false,null,null,"\npstress stop\n")) {
                scenario.waitForLogEntry(stream, ">>> Stopping ...");
        } catch (EOFException e) {}
        
        assertTrue(maxId > 10060); // minimum 60 created processed - if not something went wrong
        
        boolean done = false;
        for (int i = 0; i < 10; i++) { // wait maximal 10 minutes for all processes to stop
            MThread.sleep(MPeriod.MINUTE_IN_MILLISECOUNDS);
            try (LogStream stream = new LogStream(scenario, "karaf0")) {
                stream.setCapture(true);
                scenario.exec(stream, new String[] {"/opt/karaf/bin/client"},null,false,null,null,"pcase -ta list\na=JKHIUY\na=${a}675GH\n");
                scenario.waitForLogEntry(stream, "JKHIUY675GH");
                
                String out = stream.getCaptured();
                String[] lines = out.split("\n");
                boolean started = false;
                int caseAmount = 0;
                for (String line : lines) {
                    if (started) {
                        String[] cols = line.split("\\|");
                        if (cols.length > 2)
                            caseAmount++;
                    } else
                    if (line.contains("----------------"))
                        started = true;
                }
                if (caseAmount == 0) {
                    done = true;
                    break;
                }
            }
        }
        assertTrue(done);
        
        System.out.println();
        System.out.println("--------------------------");
        System.out.println(" Created: " + (maxId - 9999));
        System.out.println("--------------------------");
        
    }
    
    
    @BeforeAll
    public static void startDocker() throws NotFoundException, IOException, InterruptedException {
        
        prop = TestUtil.loadProperties();

        scenario = new DockerScenario();
        
        scenario.add("db", "mariadb:10.3", 
                "e:MYSQL_ROOT_PASSWORD=nein"
          );

        for (int a = 0; a < AMOUNT; a++) {
            scenario.add(new Karaf("karaf" + a, prop.getString("docker.mhus-apache-karaf.version"), 
                    "debug", 
                    "link:db:dbserver"
                    ));
        }
        
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
        
        LinkedList<PrepareJob> jobs = new LinkedList<>();
        // karaf
        
        for (int a = 0; a < AMOUNT; a++) {
            PrepareJob job = new PrepareJob(a);
            Thread t = new Thread(job);
            job.thread = t;
            jobs.add(job);
            t.start();
        }
        
        int cnt = 0;
        while (true) {
            MThread.sleep(10000);
            boolean alive = false;
            for (PrepareJob job : jobs) {
                if (job.thread.isAlive()) {
                    alive = true;
                    System.out.println("====== Running " + job.nr);
                    if (job.error != null) {
                        job.error.printStackTrace();
                        fail("Job failed " + job.nr);
                    }
                }
            }
            if (!alive)
                break;
            cnt++;
            if (cnt > 30) {
                fail("Timeout preparing karaf");
            }
        }
        
        boolean failed = false;
        for (PrepareJob job : jobs) {
            if (job.error != null) {
                System.out.println("Error in job " + job.nr);
                job.error.printStackTrace();
                failed = true;
            }
        }
        assertFalse(failed);
        
    }
    
    private static class PrepareJob implements Runnable {

        public Thread thread;
        int nr;
        volatile Throwable error;

        public PrepareJob(int nr) {
            this.nr = nr;
        }

        @Override
        public void run() {
            System.out.println(">>>> START " + nr);
            try {
                prepreKaraf(nr);
            } catch (Throwable t) {
                error = t;
            }
            System.out.println("<<<< FINISHED " + nr);
        }
        
    }
    
    private static void prepreKaraf(final int a) throws Exception {
        scenario.waitForLogEntry("karaf"+a, "@karaf"+a+"()>", 0);
        
        try (LogStream stream = new LogStream(scenario, "karaf"+a)) {
            stream.setFilter(new AnsiLogFilter());
            scenario.attach(stream, 
                "feature:repo-add activemq 5.15.8\n" +
                "feature:repo-add mvn:org.apache.shiro/shiro-features/"+prop.getString("shiro.version")+"/xml/features\n" + 
                "feature:repo-add mvn:de.mhus.osgi/mhus-features/"+prop.getString("mhus-parent.version")+"/xml/features\n" +
                "feature:repo-add mvn:de.mhus.app.reactive/reactive-feature/"+prop.getString("mhus-reactive.version")+"/xml/features\n" +
                "feature:install mhu-dev mhus-reactive-all mhus-reactive-dev\n" +
                "a=HGDFhjasdhj\n" );
        
            scenario.waitForLogEntry(stream, "Done.");
        }
        // installing activemq is asynchron, need to wait a while before next step
        MThread.sleep(10000);
        try (LogStream stream = new LogStream(scenario, "karaf"+a)) {
            stream.setFilter(new AnsiLogFilter());

            scenario.attach(stream, 
                    "bundle:install -s mvn:de.mhus.lib.itest/examples-reactive/"+prop.getString("project.version")+"\n" +
                    "a=JKHIUY\na=${a}675GH\n" );
            scenario.waitForLogEntry(stream, "JKHIUY675GH");
        }
        
        MThread.sleep(10000);
//        scenario.waitForLogEntry("karaf"+a, "Done.", 1);
        
        int tryCnt = 0;
        while (true) {
            try (LogStream stream = new LogStream(scenario, "karaf"+a)) {
                stream.setFilter(new AnsiLogFilter());
                stream.setCapture(true);
                scenario.attach(stream, 
                        "list\n" +
                        "a=JKHIUY\na=${a}675GH\n" );
                scenario.waitForLogEntry(stream, "JKHIUY675GH");
                
                String out = stream.getCaptured();
                
                int pos1 = out.indexOf("List Threshold");
                assertTrue(pos1 > 0);
                int pos2 = out.indexOf("@karaf"+a+"()", pos1);
                assertTrue(pos2 > 0);
                out = out.substring(pos1, pos2);
                
                assertFalse(out.contains("Resolved"));
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
            }  catch (Throwable t) {
                if (tryCnt > 12)
                    throw t;
                MThread.sleep(10000);
                tryCnt++;
                System.out.println();
                System.out.println("=== "+a+" Wait for deployment " + tryCnt);
                continue;
            }
            break;
        }
        MThread.sleep(10000);
        
        try (LogStream stream = new LogStream(scenario, "karaf"+a)) {
            stream.setCapture(true);
            stream.setFilter(new AnsiLogFilter());
            scenario.attach(stream, 
                    "dev-res -y cp default\n" +
                    "dev-res -y cp examples-reactive-multi\n" +
                    "dev-res -y cp disable-debug-log\n" +
                    "a=kjshkjfhjkIUYJGHJK\n" );

            scenario.waitForLogEntry(stream, "kjshkjfhjkIUYJGHJK");
            MThread.sleep(5000); // a long time - wait for configuration manager

            String out = stream.getCaptured();

//            assertTrue(out.contains("[doConfigure]"));
            Warnings.warnTrue(out.contains("[KarafCfgManager::Register PID][de.mhus.osgi.api.services.PersistentWatch]"));
        }
        
        try (LogStream stream = new LogStream(scenario, "karaf"+a)) {
            scenario.attach(stream, 
                    "shutdown -f -r\n" );

            scenario.waitForLogEntry(stream, "@karaf"+a+"()");
            MThread.sleep(15000);
            scenario.waitForLogEntry(stream, "@karaf"+a+"()");
        }

        // wait for engine to start
        
        boolean started = false;
        for (int i = 0 ; i < 20; i++) {
            MThread.sleep(2000);
            try (LogStream stream = new LogStream(scenario, "karaf"+a)) {
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
        
        
        try (LogStream stream = new LogStream(scenario, "karaf"+a)) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "pdeploy de.mhus.app.reactive.examples.simple1.S1Process:0.0.1\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("[de.mhus.app.reactive.examples.simple1.S1Pool]"));
            assertTrue(out.contains("[de.mhus.app.reactive.examples.simple1.S1Pool2]"));
        }        
    }
    
    @AfterAll
    public static void stopDocker() {
        if (prop.getBoolean("docker.destroy.containers", true))
            scenario.destroy();
    }
    
}
