package it.peterhansl.knx.knx2mqtt.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import it.peterhansl.knx.knx2mqtt.gateway.BusEventGateway;

@Component
public class MqttHealth implements HealthIndicator {
	
	@Autowired
	private BusEventGateway gateway;

	@Override
	public Health health() {
		
		Health.Builder hb = null;
		
		if ((gateway != null) && (gateway.isConnected())) {
			hb = Health.up();
		} else {
			hb = Health.down();
		}
		
		if (gateway != null) {
			hb.withDetail("initialized", gateway.isInitialized());
			hb.withDetail("connected", gateway.isConnected());
		} else {
			hb.withDetail("initialized", false);
		}
		
		return hb.build();
	}
	

}
