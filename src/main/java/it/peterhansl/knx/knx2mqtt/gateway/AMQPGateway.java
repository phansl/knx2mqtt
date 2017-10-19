package it.peterhansl.knx.knx2mqtt.gateway;

import org.springframework.beans.factory.annotation.Autowired;

import it.peterhansl.iot.dahome.knx.model.BusEvent;
import it.peterhansl.knx.knx2mqtt.config.AMQPConfig;

public class AMQPGateway implements BusEventGateway {
	
	private final AMQPConfig config;
	
	@Autowired
	public AMQPGateway(AMQPConfig config) {
		this.config = config;
	}

	@Override
	public void publishAsync(String topic, BusEvent message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void publishSync(String topic, BusEvent message) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isInitialized() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

}
