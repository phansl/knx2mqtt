package it.peterhansl.lnx.knx2mqtt.util;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.peterhansl.iot.dahome.knx.model.BusEvent;

public class BusEventMapper {
	
	private static final Logger LOG = LoggerFactory.getLogger(BusEventMapper.class);
	
	public static MqttMessage toMqtt(BusEvent evt) {
		ObjectMapper mapper = new ObjectMapper();
		String json = "";
		try {
			json = mapper.writeValueAsString(evt);
		} catch (JsonProcessingException e) {
			LOG.error("cannot map message to json");
		}
		
		MqttMessage msg = new MqttMessage(json.getBytes());
		msg.setQos(0);
		
		return msg;
	}

}
