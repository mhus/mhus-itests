package de.mhus.lib.itest.cases;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MThread;
import de.mhus.lib.core.MValidator;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.tests.TestCase;
import de.mhus.lib.tests.docker.AnsiLogFilter;
import de.mhus.lib.tests.docker.DockerScenario;
import de.mhus.lib.tests.docker.LogStream;

@TestMethodOrder(OrderAnnotation.class)
public class OsgiAdbAbstract extends TestCase {

    protected static DockerScenario scenario;
    protected static MProperties prop;

    @Test
    @Order(1)
    public void testDatasource() throws NotFoundException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "datasources\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("jdbc/mysql"));
            assertTrue(out.contains("adb_common"));
        }
    }

    @Test
    @Order(2)
    public void testUse() throws NotFoundException, InterruptedException, IOException {
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "xdb:use\n" +
                    "a=quiweyBNVNB\n" );

            scenario.waitForLogEntry(stream, "quiweyBNVNB");

            String out = stream.getCaptured();
            assertTrue(out.contains("Global : xdb:adb/adb"));
            assertTrue(out.contains("Session: xdb:adb/adb"));
        }
    }

    @Test
    @Order(3)
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
    @Order(4)
    public void testCrud() throws NotFoundException, InterruptedException, IOException {
        
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

    
    public static void prepareKaraf() throws NotFoundException, InterruptedException, IOException {
        
        scenario.waitForLogEntry("karaf", "@karaf()>", 0);
        
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            stream.setFilter(new AnsiLogFilter());
            scenario.attach(stream, 
                "feature:repo-add mvn:org.apache.shiro/shiro-features/"+prop.getString("shiro.version")+"/xml/features\n" + 
                "feature:repo-add mvn:de.mhus.osgi/mhus-features/"+prop.getString("mhus-parent.version")+"/xml/features\n" +
                "feature:install mhu-jdbc mhu-dev\n" +
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
                    "dev-res -y cp examples-adb\n" +
                    "a=kjshkjfhjkIUYJGHJK\n" );

            scenario.waitForLogEntry(stream, "kjshkjfhjkIUYJGHJK");
            MThread.sleep(5000); // a long time - wait for configuration manager

            String out = stream.getCaptured();

            assertTrue(out.contains("[doConfigure]"));
            assertTrue(out.contains("[KarafCfgManager::Register PID][de.mhus.osgi.api.services.PersistentWatch]"));
        }
        
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            scenario.attach(stream, 
                    "access restart\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVU\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVU");
        }
        
    }
}
