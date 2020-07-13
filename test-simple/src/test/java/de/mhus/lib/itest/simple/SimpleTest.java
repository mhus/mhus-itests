package de.mhus.lib.itest.simple;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;

import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.tests.TestCase;
import de.mhus.lib.tests.docker.DockerScenario;

public class SimpleTest extends TestCase {

	private static DockerScenario scenario;

	
	@Test
	public void testSample() throws NotFoundException, DockerException, InterruptedException {
		try (LogStream logs = scenario.logs("jms")) {
			String l = logs.readFully();
			System.out.println(l);
		}
	}
	
	@BeforeAll
	public static void startDocker() throws DockerCertificateException, DockerException, InterruptedException {
		scenario = new DockerScenario();
		scenario.add("jms", "webcenter/activemq:5.14.3", 
				"env:ACTIVEMQ_CONFIG_NAME=amqp-srv1",
				"env:ACTIVEMQ_CONFIG_DEFAULTACCOUNT=false",
				"env:ACTIVEMQ_ADMIN_LOGIN=admin",
				"env:ACTIVEMQ_ADMIN_PASSWORD=nein",
				"env:ACTIVEMQ_CONFIG_MINMEMORY=1024",
				"env:ACTIVEMQ_CONFIG_MAXMEMORY=4096",
				"env:ACTIVEMQ_CONFIG_SCHEDULERENABLED=true"
				);
		scenario.start();
	}
	
	@AfterAll
	public static void stopDocker() throws DockerCertificateException, DockerException, InterruptedException {
		scenario.destroy();
	}
	
	
}
