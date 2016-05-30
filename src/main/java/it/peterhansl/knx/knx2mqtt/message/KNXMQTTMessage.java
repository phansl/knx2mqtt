package it.peterhansl.knx.knx2mqtt.message;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KNXMQTTMessage {
	
	private static final Logger LOG = LoggerFactory.getLogger(KNXMQTTMessage.class);
	
	private String name;
	
	private int address;
	
	private String dpt;
	
	private byte[] value;
	
	public MqttMessage toMqtt() {
		ObjectMapper mapper = new ObjectMapper();
		String json = "";
		try {
			json = mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			LOG.error("cannot map message to json");
		}
		
		MqttMessage msg = new MqttMessage(json.getBytes());
		msg.setQos(0);
		
		return msg;
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}

	public String getDpt() {
		return dpt;
	}

	public void setDpt(String dpt) {
		this.dpt = dpt;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	
}
