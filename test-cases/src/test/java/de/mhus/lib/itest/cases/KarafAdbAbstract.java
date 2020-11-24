package de.mhus.lib.itest.cases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import de.mhus.lib.core.MCast;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MThread;
import de.mhus.lib.core.MValidator;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.tests.TestCase;
import static de.mhus.lib.tests.Warnings.warnTrue;
import de.mhus.lib.tests.docker.AnsiLogFilter;
import de.mhus.lib.tests.docker.DockerScenario;
import de.mhus.lib.tests.docker.LogStream;

@Disabled
public abstract class KarafAdbAbstract extends TestCase {

    protected static DockerScenario scenario;
    protected static MProperties prop;
    @SuppressWarnings("unused")
    private static String kind;

    @Test
    @Order(1)
    //@Timeout(value=1,unit=TimeUnit.MINUTES)
    public void testDatasource() throws NotFoundException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "datasources\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("jdbc/"));
            assertTrue(out.contains("adb_common"));
        }
    }

    @Test
    @Order(2)
    //@Timeout(value=1,unit=TimeUnit.MINUTES)
    public void testUse() throws NotFoundException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "xdb:use\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("Global : xdb:adb/common_adb/adb_common"));
            assertTrue(out.contains("Session: xdb:adb/common_adb/adb_common"));
        }
    }

    @Test
    @Order(3)
    //@Timeout(value=1,unit=TimeUnit.MINUTES)
    public void testList() throws NotFoundException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "xdb:list\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("adb"));
            assertTrue(out.contains("DbLockObject"));
            assertTrue(out.contains("Book"));
            assertTrue(out.contains("Author"));
            assertTrue(out.contains("Member"));
        }
    }

    @Test
    @Order(10)
    //@Timeout(value=3,unit=TimeUnit.MINUTES)
    public void testDelete() throws NotFoundException, InterruptedException, IOException {
        // create
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            scenario.attach(stream, 
                            "xdb:create Member 'name=Shmi Skywalker-Lars'\n" +
                            "xdb:create Member 'name=Anakin Skywalker'\n" +
                            "xdb:create Member 'name=Padmé Amidala'\n" +
                            "xdb:create Member 'name=Luke Skywalker'\n" +
                            "xdb:create Member 'name=Leia Organa'\n" +
                            "xdb:create Member 'name=Ben Solo'\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("--- SET name"));
            assertTrue(out.contains("*** CREATE"));
        }
        // count
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            stream.setFilter(new AnsiLogFilter());
            scenario.attach(stream, 
                    "xdb:count Member\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            System.out.println();
            System.out.println("------");
            System.out.println(out);
            System.out.println("------");
            
            String count = out.split("\n")[1].trim();
            System.out.println(count);
            assertEquals("6", count);
        }
        // delete
        cleanupEntities();
        // count
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            stream.setFilter(new AnsiLogFilter());
            scenario.attach(stream, 
                    "xdb:count Member\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            System.out.println();
            System.out.println("------");
            System.out.println(out);
            System.out.println("------");
            
            String count = out.split("\n")[1].trim();
            System.out.println(count);
            assertEquals("0", count);
        }
    }
    
    protected void cleanupEntities() throws NotFoundException, IOException, InterruptedException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            stream.setFilter(new AnsiLogFilter());
            scenario.attach(stream, 
                            "xdb:delete -y Member '()'\n" +
                            "xdb:delete -y Author '()'\n" +
                            "xdb:delete -y Book '()'\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");
        }
    }

    @Test
    @Order(11)
    //@Timeout(value=5,unit=TimeUnit.MINUTES)
    public void testCrud() throws NotFoundException, InterruptedException, IOException {
        
        cleanupEntities();
        
        String id = null;
        
        // create
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "xdb:create Member name=Luke\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("--- SET name"));
            assertTrue(out.contains("*** CREATE"));
        
            id = out.split("CREATE")[1].trim().split("\n")[0].trim();
            System.out.println(">>> ID: " + id);
            assertTrue(MValidator.isUUID(id));
        }
        // get
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "xdb:view Member "+id+"\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("name"));
            assertTrue(out.contains("Luke"));
        }
        // update
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "xdb:update -y Member "+id+" name=Anakin\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("--- SET name  = Anakin"));
            assertTrue(out.contains("*** SAVE"));
        }
        // get
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "xdb:view Member "+id+"\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("name"));
            assertTrue(out.contains("Anakin"));
            assertFalse(out.contains("Luke"));
        }
        // delete
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "xdb:delete -y Member "+id+"\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("*** DELETE [Member:]"));
        }
        // get
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "xdb:view Member "+id+"\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("*** Object not found"));
            assertFalse(out.contains("Anakin"));
            assertFalse(out.contains("Luke"));
        }
    }

    @Test
    @Order(20)
    public void testBenchmark() throws NotFoundException, IOException, InterruptedException {
        int amount = MCast.toint(System.getenv("ADB_BENCHMARK_AMOUNT"), 1000);
        int loops = MCast.toint(System.getenv("ADB_BENCHMARK_LOOPS"), 100);
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setFilter(new AnsiLogFilter());
            scenario.attach(stream, 
                            "itest:adbbenchmark "+amount+" "+loops+"\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");
        }
    }
    
    
    public static void prepareKaraf(String kind, String additionallInstall) throws NotFoundException, InterruptedException, IOException {
        KarafAdbAbstract.kind = kind;
        
        scenario.waitForLogEntry("karaf", "@karaf()>", 0);
        
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            stream.setFilter(new AnsiLogFilter());
            scenario.attach(stream, 
                "feature:repo-add mvn:org.apache.shiro/shiro-features/"+prop.getString("shiro.version")+"/xml/features\n" + 
                "feature:repo-add mvn:de.mhus.osgi/mhus-features/"+prop.getString("mhus-parent.version")+"/xml/features\n" +
                "feature:install mhu-jdbc mhu-dev\n" +
                additionallInstall +
                "bundle:install -s mvn:de.mhus.lib.itest/examples-adb/"+prop.getString("project.version")+"\n" +
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
            
            assertTrue(out.contains("db-core"));
            assertTrue(out.contains("db-karaf"));
            assertTrue(out.contains("db-osgi-api"));
            assertTrue(out.contains("db-osgi-adb"));
            
            assertTrue(out.contains("examples-adb"));
        }
        
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            stream.setFilter(new AnsiLogFilter());
            scenario.attach(stream, 
                    "dev-res -y cp default\n" +
                    "dev-res -y cp examples-adb-"+kind+"\n" +
                    "a=kjshkjfhjkIUYJGHJK\n" );

            scenario.waitForLogEntry(stream, "kjshkjfhjkIUYJGHJK");
            MThread.sleep(5000); // a long time - wait for configuration manager

            String out = stream.getCaptured();

            warnTrue(out.contains("[doConfigure]"));
            warnTrue(out.contains("[KarafCfgManager::Register PID][de.mhus.osgi.api.services.PersistentWatch]"));
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
