package de.mhus.lib.itest.simple;

import java.io.EOFException;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.github.dockerjava.api.exception.DockerException;

import de.mhus.lib.core.MThread;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.tests.TestCase;
import de.mhus.lib.tests.docker.DockerScenario;
import de.mhus.lib.tests.docker.Karaf;
import de.mhus.lib.tests.docker.LogStream;

public class SimpleTest extends TestCase {

	private static DockerScenario scenario;

	
	@Test
	public void testSample() throws NotFoundException, DockerException, InterruptedException, IOException {
	    
        System.out.println("STEP 1 ===========================");
	    try {
	        scenario.waitForLogEntry("karaf", "@karaf()>", 0);
	    } catch (EOFException e) {
	        e.printStackTrace();
	    }

	    System.out.println("STEP 2 ===========================");
	    try (LogStream stream = scenario.attach("karaf", "echo \"Hello World\"\n" )) {
	        scenario.waitForLogEntry(stream, "Hello World");
	    }
	}
	
	@BeforeAll
	public static void startDocker() {
	    
//	    System.out.println("ENV:");
//        System.out.println(System.getenv());
//        System.out.println("PROPS:");
//        System.out.println(System.getProperties());
        
		scenario = new DockerScenario();
//		scenario.add("jms", "webcenter/activemq:5.14.3", 
//				"env:ACTIVEMQ_CONFIG_NAME=amqp-srv1",
//				"env:ACTIVEMQ_CONFIG_DEFAULTACCOUNT=false",
//				"env:ACTIVEMQ_ADMIN_LOGIN=admin",
//				"env:ACTIVEMQ_ADMIN_PASSWORD=nein",
//				"env:ACTIVEMQ_CONFIG_MINMEMORY=1024",
//				"env:ACTIVEMQ_CONFIG_MAXMEMORY=4096",
//				"env:ACTIVEMQ_CONFIG_SCHEDULERENABLED=true"
//				);
//		
//		scenario.add("db", "mariadb:10.3", 
//		        "e:MYSQL_ROOT_PASSWORD=nein"
//		        );
		
		scenario.add(new Karaf("karaf", null, "debug"));
		
		scenario.destroyPrefix();
		scenario.start();
		
		MThread.sleep(1000);
		
	}
	
	@AfterAll
	public static void stopDocker() {
		scenario.destroy();
	}
	
	
}
