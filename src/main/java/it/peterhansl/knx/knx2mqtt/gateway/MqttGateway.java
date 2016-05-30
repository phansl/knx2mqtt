package it.peterhansl.knx.knx2mqtt.gateway;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.stereotype.Component;

import it.peterhansl.knx.knx2mqtt.config.MQTTConfig;

/**
 * 
 * see http://www.eclipse.org/paho/clients/java/
 * 
 * @author Philip
 *
 */
@Component
public class MqttGateway {
	
	private static final Logger LOG = LoggerFactory.getLogger(MqttGateway.class);
	
	@Inject
	private MQTTConfig mqttConfig;
	
	@Autowired
	private CounterService counterService;
	
	private MqttClient client;
	
	//TODO make persistence configurable (either MemoryPersistence or FilePErsistence)
	private MqttClientPersistence persistence = new MemoryPersistence();
	
	@PostConstruct
	public void initialize() {
		
		try {
			client = new MqttClient(mqttConfig.getConnectionURLString(), mqttConfig.getClientName(), persistence);
		} catch (MqttException e) {
			LOG.error("unable to initialize mqtt client", e);
		}
		
	}
	
	public void publish(final String topic, final MqttMessage message) {
		publish(topic, message, false);
	}
	
	public void publish(final String topic, final MqttMessage message, final boolean async) {
		
		if (!client.isConnected()) {
			connect();
		}
		
		if (async) {
			Runnable asyncPublish = () -> { publish(client, topic, message); };
			new Thread(asyncPublish).start();
		} else {
			publish(client, topic, message);
		}
		
	}
	
	private final void publish(MqttClient client, String topic, MqttMessage message) {
		try {
			client.publish(topic, message);
			// update metrics
			counterService.increment("counter.mqtt.messages.total");
		} catch (MqttException e) {
			LOG.error("failed to publish message to topic {} on server {}", topic, client.getServerURI());
		}
	}
	
	public boolean isInitialized() {
		return client != null;
	}
	
	public boolean isConnected() {
		return (client != null) && client.isConnected();
	}
	
	private void connect() {
		
		if (client == null) {
			LOG.warn("there's no mqtt connection established");
			return;
		}
		
		if (!client.isConnected()) {
			try {
				client.connect();
			} catch (MqttException e) {
				LOG.error("unable to connect to mqtt server {}", mqttConfig.getConnectionURLString());
			}
		}
	}

}
