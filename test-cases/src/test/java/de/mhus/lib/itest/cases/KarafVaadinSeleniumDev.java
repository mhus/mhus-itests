package de.mhus.lib.itest.cases;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.Disabled;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

// http://localhost:24444/wd/hub/static/resource/hub.html
@Disabled
public class KarafVaadinSeleniumDev {

    private static final String UI_URL = "http://uiserver:8181/ui";

    public static void main(String[] args) throws MalformedURLException {
        WebDriver driver = new RemoteWebDriver(new URL("http://localhost:24444/wd/hub"), DesiredCapabilities.chrome());

        driver.navigate().to(UI_URL);
        
        
        
    }

}
