package de.mhus.lib.itest.cases;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;

import com.github.dockerjava.api.exception.DockerException;

import de.mhus.lib.core.MProperties;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.tests.TestCase;
import de.mhus.lib.tests.docker.DockerScenario;
import de.mhus.lib.tests.docker.Karaf;

@TestMethodOrder(OrderAnnotation.class)
public class KarafHelloTest extends TestCase {

    private static DockerScenario scenario;
    private static MProperties prop;

//    @Test
    public void testList() throws NotFoundException, DockerException, InterruptedException, IOException {
    }

    @BeforeAll
    public static void startDocker() throws NotFoundException, IOException, InterruptedException {
        
        prop = TestUtil.loadProperties();

        scenario = new DockerScenario();
        

        scenario.add(new Karaf("karaf", prop.getString("docker.mhus-apache-karaf.version"), 
                "debug"
                ));
        
        scenario.destroyPrefix();
        scenario.start();

    }
    
    @AfterAll
    public static void stopDocker() {
//        if (prop.getBoolean("docker.destroy.containers", true))
//            scenario.destroy();
    }
    
}
