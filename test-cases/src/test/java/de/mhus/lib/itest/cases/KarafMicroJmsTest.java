package de.mhus.lib.itest.cases;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.logging.Level;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MThread;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.tests.TestCase;
import de.mhus.lib.tests.docker.AnsiLogFilter;
import de.mhus.lib.tests.docker.DockerScenario;
import de.mhus.lib.tests.docker.Karaf;
import de.mhus.lib.tests.docker.LogStream;

//@Disabled
@TestMethodOrder(OrderAnnotation.class)
public class KarafMicroJmsTest extends TestCase {

    static String activemqVersion = "5.16.0";
    
    private static DockerScenario scenario;
    private static MProperties prop;

    @Test
    @Order(1)
    public void test1MoList() throws NotFoundException, IOException, InterruptedException {
    }

    @BeforeAll
    public static void startDocker() throws NotFoundException, IOException, InterruptedException {
        
        de.mhus.lib.tests.TestUtil.configureApacheCommonLogging("org.apache.http.impl.conn.PoolingHttpClientConnectionManager", Level.INFO);
        {
            String ln = "org.apache.http.impl.conn.PoolingHttpClientConnectionManager";
            final Logger logger = LoggerFactory.getLogger(ln);
            final ch.qos.logback.classic.Logger logger2 = (ch.qos.logback.classic.Logger) logger;
            logger2.setLevel(ch.qos.logback.classic.Level.WARN);
        }
        {
            String ln = "org.apache.http.headers";
            final Logger logger = LoggerFactory.getLogger(ln);
            final ch.qos.logback.classic.Logger logger2 = (ch.qos.logback.classic.Logger) logger;
            logger2.setLevel(ch.qos.logback.classic.Level.WARN);
        }
        {
            String ln = "org.apache.http.wire";
            final Logger logger = LoggerFactory.getLogger(ln);
            final ch.qos.logback.classic.Logger logger2 = (ch.qos.logback.classic.Logger) logger;
            logger2.setLevel(ch.qos.logback.classic.Level.WARN);
        }
        {
            String ln = "org.apache.http.client.protocol.RequestAddCookies";
            final Logger logger = LoggerFactory.getLogger(ln);
            final ch.qos.logback.classic.Logger logger2 = (ch.qos.logback.classic.Logger) logger;
            logger2.setLevel(ch.qos.logback.classic.Level.WARN);
        }
        {
            String ln = "org.apache.http.client.protocol.RequestAuthCache";
            final Logger logger = LoggerFactory.getLogger(ln);
            final ch.qos.logback.classic.Logger logger2 = (ch.qos.logback.classic.Logger) logger;
            logger2.setLevel(ch.qos.logback.classic.Level.WARN);
        }
        {
            String ln = "org.apache.hc.client5.http.wire";
            final Logger logger = LoggerFactory.getLogger(ln);
            final ch.qos.logback.classic.Logger logger2 = (ch.qos.logback.classic.Logger) logger;
            logger2.setLevel(ch.qos.logback.classic.Level.WARN);
        }
        {
            String ln = "org.apache.hc.client5.http.headers";
            final Logger logger = LoggerFactory.getLogger(ln);
            final ch.qos.logback.classic.Logger logger2 = (ch.qos.logback.classic.Logger) logger;
            logger2.setLevel(ch.qos.logback.classic.Level.WARN);
        }
        {
            String ln = "org.apache.hc.client5.http.impl.classic.MainClientExec";
            final Logger logger = LoggerFactory.getLogger(ln);
            final ch.qos.logback.classic.Logger logger2 = (ch.qos.logback.classic.Logger) logger;
            logger2.setLevel(ch.qos.logback.classic.Level.WARN);
        }
        {
            String ln = "org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager";
            final Logger logger = LoggerFactory.getLogger(ln);
            final ch.qos.logback.classic.Logger logger2 = (ch.qos.logback.classic.Logger) logger;
            logger2.setLevel(ch.qos.logback.classic.Level.WARN);
        }
        {
            String ln = "org.apache.hc.client5.http.impl.classic.InternalHttpClient";
            final Logger logger = LoggerFactory.getLogger(ln);
            final ch.qos.logback.classic.Logger logger2 = (ch.qos.logback.classic.Logger) logger;
            logger2.setLevel(ch.qos.logback.classic.Level.WARN);
        }
        {
            String ln = "org.apache.http.headers";
            final Logger logger = LoggerFactory.getLogger(ln);
            final ch.qos.logback.classic.Logger logger2 = (ch.qos.logback.classic.Logger) logger;
            logger2.setLevel(ch.qos.logback.classic.Level.WARN);
        }
        
        
        
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

        scenario.add(new Karaf("karaf1", prop.getString("docker.mhus-apache-karaf.version"), 
                "debug", 
                "link:jms:jmsserver",
                "env:de_mhus_lib_jms_MJms_defaultConnection=test"
                ));

//        scenario.add(new Karaf("karaf2", prop.getString("docker.mhus-apache-karaf.version"), 
//                "debug", 
//                "link:jms:jmsserver",
//                "env:de_mhus_lib_jms_MJms_defaultConnection=test"
//                ));

        scenario.destroyPrefix();
        scenario.start();
        
        MThread.sleep(1000);

        // jms
        scenario.waitForLogEntry("jms", "activemq entered RUNNING state", 0);

        //-----------------------------------------------
        // karaf1
        scenario.waitForLogEntry("karaf1", "Done.", 0);
        
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            stream.setFilter(new AnsiLogFilter());
            scenario.attach(stream, 
                "feature:repo-add mvn:de.mhus.lib.itest/examples-features/"+prop.getString("project.version")+"/xml/features\n" +
                "feature:install example-micro-jms\n" +
                "a=HGDFhjasdhj\n" );
        
            scenario.waitForLogEntry(stream, "Done.");
        }
/*        
        // installing activemq is asynchron, need to wait a while before next step
        MThread.sleep(10000);
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            stream.setFilter(new AnsiLogFilter());

            scenario.attach(stream, 
                    "bundle:install -s mvn:de.mhus.osgi/dev-micro/"+prop.getString("mhus-dev.version","7.2.0-SNAPSHOT")+"\n" + 
                    "bundle:install -s mvn:de.mhus.micro/micro-oper-rest/"+prop.getString("mhus-micro.version","7.0.0-SNAPSHOT")+"\n" + 
                    "bundle:install -s mvn:de.mhus.micro/micro-execute-rest/"+prop.getString("mhus-micro.version","7.0.0-SNAPSHOT")+"\n" + 
                    "bundle:install -s mvn:de.mhus.lib.itest/examples-jms/"+prop.getString("project.version")+"\n" + 
                    "blue-create de.mhus.rest.osgi.RestServlet\n" +
                    "blue-create de.mhus.rest.osgi.nodes.PublicRestNode\n" +
                    "a=HGDFhjasdhz\n" );
            scenario.waitForLogEntry(stream, "HGDFhjasdhz");
        }
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            stream.setFilter(new AnsiLogFilter());

            scenario.attach(stream, 
                    "dev-res -y cp examples-jms/examples-jms\n" +
                    "install -s mvn:de.mhus.micro/micro-oper-jms/7.0.0-SNAPSHOT\n" +
                    "blue-create de.mhus.micro.oper.jms.DefaultOperationsChannel\n" + 
                    "a=HGDFhjasdhz\n" );
            scenario.waitForLogEntry(stream, "HGDFhjasdhz");
        }
        MThread.sleep(5000);
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            stream.setFilter(new AnsiLogFilter());
            stream.setCapture(true);
            scenario.attach(stream, 
                    "list\n" +
                    "a=HGDFhjasdhx\n" );
            scenario.waitForLogEntry(stream, "HGDFhjasdhx");
            
            String out = stream.getCaptured();
            
            int pos1 = out.indexOf("List Threshold");
            assertTrue(pos1 > 0);
            int pos2 = out.indexOf("@karaf1()", pos1);
            assertTrue(pos2 > 0);
            out = out.substring(pos1, pos2);
            
            assertTrue(out.contains("lib-annotations"));
            assertTrue(out.contains("lib-core"));
            assertTrue(out.contains("lib-j2ee"));
            assertTrue(out.contains("karaf-commands"));
            assertTrue(out.contains("osgi-api"));
            assertTrue(out.contains("osgi-services"));
            
            assertTrue(out.contains("dev-micro"));
            assertTrue(out.contains("micro-api"));
            assertTrue(out.contains("micro-impl"));
            assertTrue(out.contains("micro-karaf"));
            assertTrue(out.contains("micro-oper-rest"));
            assertTrue(out.contains("micro-execute"));
            assertTrue(out.contains("service-de.mhus.rest.osgi.restservlet.xml"));

        }
        
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            stream.setCapture(true);
            stream.setFilter(new AnsiLogFilter());
            scenario.attach(stream, 
                    "dev-res -y cp karaf-dev/default\n" +
                    "a=kjshkjfhjkIUYJGHJK\n" );

            scenario.waitForLogEntry(stream, "kjshkjfhjkIUYJGHJK");
            MThread.sleep(5000); // a long time - wait for configuration manager

            @SuppressWarnings("unused")
            String out = stream.getCaptured();

//            assertTrue(out.contains("[doConfigure]"));
//            assertTrue(out.contains("[KarafCfgManager::Register PID][de.mhus.osgi.api.services.PersistentWatch]"));
        }
        
        try (LogStream stream = new LogStream(scenario, "karaf1")) {
            scenario.attach(stream, 
                    "access-restart\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVU\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVU");
        }

        
        //-----------------------------------------------
        // karaf2
        scenario.waitForLogEntry("karaf2", "Done.", 0);
        
        try (LogStream stream = new LogStream(scenario, "karaf2")) {
            stream.setFilter(new AnsiLogFilter());
            scenario.attach(stream, 
                "feature:repo-add activemq "+activemqVersion+"\n" +
                "feature:repo-add mvn:org.apache.shiro/shiro-features/"+prop.getString("shiro.version")+"/xml/features\n" + 
                "feature:repo-add mvn:de.mhus.osgi/mhus-features/"+prop.getString("mhus-parent.version")+"/xml/features\n" +
                "feature:install eventadmin mhu-dev mhu-micro mhu-jms\n" +
                "a=HGDFhjasdhj\n" );
        
            scenario.waitForLogEntry(stream, "Done.");
        }
        // installing activemq is asynchron, need to wait a while before next step
        MThread.sleep(10000);
        try (LogStream stream = new LogStream(scenario, "karaf2")) {
            stream.setFilter(new AnsiLogFilter());

            scenario.attach(stream, 
                    "bundle:install -s mvn:de.mhus.micro/micro-execute-rest/"+prop.getString("mhus-micro.version","7.0.0-SNAPSHOT")+"\n" + 
                    "bundle:install -s mvn:de.mhus.lib.itest/examples-jms/"+prop.getString("project.version")+"\n" + 
                    "a=HGDFhjasdhz\n" );
            scenario.waitForLogEntry(stream, "HGDFhjasdhz");
        }
        try (LogStream stream = new LogStream(scenario, "karaf2")) {
            stream.setFilter(new AnsiLogFilter());

            scenario.attach(stream, 
                    "dev-res -y cp examples-jms/examples-jms\n" +
                    "install -s mvn:de.mhus.micro/micro-oper-jms/7.0.0-SNAPSHOT\n" +
                    "a=HGDFhjasdhz\n" );
            scenario.waitForLogEntry(stream, "HGDFhjasdhz");
        }
        MThread.sleep(5000);
        try (LogStream stream = new LogStream(scenario, "karaf2")) {
            stream.setFilter(new AnsiLogFilter());
            stream.setCapture(true);
            scenario.attach(stream, 
                    "list\n" +
                    "a=HGDFhjasdhx\n" );
            scenario.waitForLogEntry(stream, "HGDFhjasdhx");
            
            String out = stream.getCaptured();
            
            int pos1 = out.indexOf("List Threshold");
            assertTrue(pos1 > 0);
            int pos2 = out.indexOf("@karaf2()", pos1);
            assertTrue(pos2 > 0);
            out = out.substring(pos1, pos2);
            
            assertTrue(out.contains("lib-annotations"));
            assertTrue(out.contains("lib-core"));
            assertTrue(out.contains("lib-j2ee"));
            assertTrue(out.contains("karaf-commands"));
            assertTrue(out.contains("osgi-api"));
            assertTrue(out.contains("osgi-services"));
            
            assertTrue(out.contains("micro-api"));
            assertTrue(out.contains("micro-impl"));
            assertTrue(out.contains("micro-karaf"));
            assertTrue(out.contains("micro-execute"));

        }
        
        try (LogStream stream = new LogStream(scenario, "karaf2")) {
            stream.setCapture(true);
            stream.setFilter(new AnsiLogFilter());
            scenario.attach(stream, 
                    "dev-res -y cp karaf-dev/default\n" +
                    "dev-res -y cp karaf-dev/disable-debug-log\n" +
                    "a=kjshkjfhjkIUYJGHJK\n" );

            scenario.waitForLogEntry(stream, "kjshkjfhjkIUYJGHJK");
            MThread.sleep(5000); // a long time - wait for configuration manager

            String out = stream.getCaptured();

//            assertTrue(out.contains("[doConfigure]"));
            assertTrue(out.contains("[KarafCfgManager::Register PID][de.mhus.osgi.api.services.PersistentWatch]"));
        }
        
        try (LogStream stream = new LogStream(scenario, "karaf2")) {
            scenario.attach(stream, 
                    "access-restart\n" +
                    "a=HJGPODGHHKJNBHJGJHHJVU\n" );

            scenario.waitForLogEntry(stream, "HJGPODGHHKJNBHJGJHHJVU");
        }
*/
    }
    
    @AfterAll
    public static void stopDocker() {
        if (prop.getBoolean("docker.destroy.containers", true))
            scenario.destroy();
    }

    
}
