package de.mhus.lib.itest.cases;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;

import de.mhus.lib.core.MThread;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.tests.docker.DockerScenario;
import de.mhus.lib.tests.docker.Karaf;
import de.mhus.lib.tests.docker.LogStream;

@TestMethodOrder(OrderAnnotation.class)
public class KarafAdbMariaDbTest extends KarafAdbAbstract {

    @BeforeAll
    public static void startDocker() throws NotFoundException, IOException, InterruptedException {
        
        prop = TestUtil.loadProperties();
        
        scenario = new DockerScenario();
        
        scenario.add("db", "mariadb:10.3", 
              "e:MYSQL_ROOT_PASSWORD=nein"
        );

        scenario.add(new Karaf("karaf", 
                prop.getString("docker.mhus-apache-karaf.version"), 
                "debug", 
                "link:db:dbserver"
                ));
        
        scenario.destroyPrefix();
        scenario.start();
        
        MThread.sleep(1000);

        scenario.waitForLogEntry("db", "mysqld: ready for connections", 0);
        
        MThread.sleep(10000); // need to wait until background job of mariadb set the admin password
        
        String sql = 
                "CREATE DATABASE db_test;\n" + 
                "CREATE USER 'usr_test'@'%' IDENTIFIED BY 'nein';\n" + 
                "GRANT ALL PRIVILEGES ON db_test.* TO 'usr_test'@'%';\n" +
                "quit";
        
        try (LogStream stream = scenario.exec("db","mysql -pnein", sql )) {
            stream.setCapture(true);
            scenario.waitForLogEntry(stream, "quit");
            String res = stream.getCaptured();
            assertFalse(res.contains("ERROR"));
        }
        
        prepareKaraf("maria", "");
        
    }

}
