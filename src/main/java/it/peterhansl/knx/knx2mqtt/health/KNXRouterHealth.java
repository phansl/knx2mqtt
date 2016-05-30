package it.peterhansl.knx.knx2mqtt.health;

import javax.inject.Inject;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import it.peterhansl.knx.knx2mqtt.config.BusMonitorConfiguration;
import it.peterhansl.lnx.knx2mqtt.util.NetworkUtil;

/**
 * 
 * see http://kielczewski.eu/2015/01/application-metrics-with-spring-boot-actuator/
 * 
 * @author Philip
 *
 */

@Component
public class KNXRouterHealth implements HealthIndicator {

	@Inject
	BusMonitorConfiguration busMonitorConfiguration;
	
	@Override
	public Health health() {
		
		boolean isReachable = NetworkUtil.ping(busMonitorConfiguration.getRouter());
		
		if (isReachable) {
			return Health.up().build();
		} else {
			return Health.down().build();
		}
		
	}

}
