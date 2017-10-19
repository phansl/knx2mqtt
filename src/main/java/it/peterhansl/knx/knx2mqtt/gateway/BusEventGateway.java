package it.peterhansl.knx.knx2mqtt.gateway;

import it.peterhansl.iot.dahome.knx.model.BusEvent;

public interface BusEventGateway {

	void publishAsync(String topic, BusEvent message);

	void publishSync(String topic, BusEvent message);

	boolean isInitialized();

	boolean isConnected();

}