package it.peterhansl.knx.knx2mqtt;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import it.peterhansl.knx.knx2mqtt.monitor.BusMonitor;

@SpringBootApplication
@IntegrationComponentScan
@EnableScheduling
public class KNX2MQTT {

	private static final Logger LOG = LoggerFactory.getLogger(KNX2MQTT.class);
	
	@Inject
	private BusMonitor busMonitor;
	
	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(KNX2MQTT.class, args);
		
	}

}
