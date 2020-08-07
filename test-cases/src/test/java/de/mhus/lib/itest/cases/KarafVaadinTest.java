package de.mhus.lib.itest.cases;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.function.BooleanSupplier;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.github.dockerjava.api.exception.DockerException;

import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MThread;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.tests.TestCase;
import de.mhus.lib.tests.docker.DockerContainer;
import de.mhus.lib.tests.docker.DockerScenario;
import de.mhus.lib.tests.docker.Karaf;
import de.mhus.lib.tests.docker.LogStream;

@TestMethodOrder(OrderAnnotation.class)
public class KarafVaadinTest extends TestCase {

    private static DockerScenario scenario;
    private static MProperties prop;
    private static WebDriver driver;
    private static final String UI_URL = "http://uiserver:8181/ui";

    @Test
    @Order(10)
    public void testStart() throws NotFoundException, DockerException, InterruptedException, IOException {
        driver.navigate().to(UI_URL);
        {
            WebElement ele = driver.findElement(By.id("gwt-uid-3"));
            assertNotNull(ele);
            ele.sendKeys("admin");
        }
        {
            WebElement ele = driver.findElement(By.id("gwt-uid-5"));
            assertNotNull(ele);
            ele.sendKeys("secret");
        }
        {
            WebElement ele = findVaadinButton("Sign In");
            assertNotNull(ele);
            ele.click();
        }
        {
            assertTrue(waitForText("Spaces", 1000, 10));
            assertTrue(driver.getPageSource().contains("Test Space"));
        }
    }

    boolean waitForText(String text, int sleep, int loops ) {
        for (int i = 0; i < loops; i++) {
            MThread.sleep(sleep);
            if (driver.getPageSource().contains(text)) return true;
        }
        return false;
    }

    WebElement findVaadinButton(String text) {
        for (WebElement e : driver.findElements(By.className("v-button-caption"))) {
            System.out.println(e.getText());
            if (e.getText().equals(text))
                return e.findElement(By.xpath("./../.."));
        }
        return null;
    }

    @BeforeAll
    public static void startDocker() throws NotFoundException, IOException, InterruptedException {
        
        prop = TestUtil.loadProperties();
        
        scenario = new DockerScenario();
        scenario.add(new Karaf("karaf", prop.getString("docker.mhus-apache-karaf.version"), 
                "debug",
                "p:28181+:8181"
                ));
        
        scenario.add(new DockerContainer("selenium", "selenium/standalone-chrome-debug:3.141.59-bismuth", 
                "p:25900+:5900",
                "p:24444+:4444",
                "l:karaf:uiserver"
                ));
        
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
            stream.setCapture(true);
            scenario.attach(stream, 
                "feature:repo-add mvn:org.apache.shiro/shiro-features/"+prop.getString("shiro.version")+"/xml/features\n" + 
                "feature:repo-add mvn:de.mhus.osgi/mhus-features/"+prop.getString("mhus-parent.version")+"/xml/features\n" +
                "feature:install mhu-vaadin-ui mhu-dev\n" +
                "bundle:install -s mvn:de.mhus.lib.itest/examples-vaadin/"+prop.getString("project.version")+"\n" +
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
            
        }
        
        try (LogStream stream = new LogStream(scenario, "karaf")) {
            stream.setCapture(true);
            
            scenario.attach(stream, 
                    "dev-res -y cp default\n" +
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

        int port = scenario.get("selenium").getPortBinding(4444);
        driver = new RemoteWebDriver(new URL("http://localhost:" + port + "/wd/hub"), DesiredCapabilities.chrome());
        
    }
    
    @AfterAll
    public static void stopDocker() {
        // scenario.destroy();
        driver.close();
    }
    
    
}
