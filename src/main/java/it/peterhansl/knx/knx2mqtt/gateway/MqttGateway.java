package it.peterhansl.knx.knx2mqtt.gateway;

import javax.annotation.PostConstruct;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.instrument.Metrics;
import it.peterhansl.iot.dahome.knx.model.BusEvent;
import it.peterhansl.knx.knx2mqtt.config.MQTTConfig;
import it.peterhansl.lnx.knx2mqtt.util.BusEventMapper;

/**
 * 
 * see http://www.eclipse.org/paho/clients/java/
 * 
 * @author Philip
 *
 */
public class MqttGateway implements BusEventGateway {
	
	private static final Logger LOG = LoggerFactory.getLogger(MqttGateway.class);
	
	private final MQTTConfig mqttConfig;
	
	private MqttClient client;
	
	//TODO make persistence configurable (either MemoryPersistence or FilePErsistence)
	private MqttClientPersistence persistence = new MemoryPersistence();
	
	public MqttGateway(MQTTConfig config) {
		this.mqttConfig = config;
	}
	
	@PostConstruct
	public void initialize() {
		
		try {
			client = new MqttClient(mqttConfig.getConnectionURLString(), mqttConfig.getClientName(), persistence);
			connect();
		} catch (MqttException e) {
			LOG.error("unable to initialize mqtt client", e);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see it.peterhansl.knx.knx2mqtt.gateway.BusEventGateway#publishAsync(java.lang.String, it.peterhansl.iot.dahome.knx.model.BusEvent)
	 */
	@Override
	public void publishAsync(final String topic, final BusEvent message) {
		
		if (!client.isConnected()) {
			return;
		}
		
		Runnable asyncPublish = () -> { publishSync(topic, message); };
		new Thread(asyncPublish).start();
		
	}
	
	/* (non-Javadoc)
	 * @see it.peterhansl.knx.knx2mqtt.gateway.BusEventGateway#publishSync(java.lang.String, it.peterhansl.iot.dahome.knx.model.BusEvent)
	 */
	@Override
	public void publishSync(String topic, BusEvent message) {
		try {
			client.publish(topic, BusEventMapper.toMqtt(message));
			// update metrics
			Metrics.counter("counter.mqtt.messages.total").increment();
		} catch (MqttException e) {
			LOG.error("failed to publish mqtt message to topic {} on server {}", topic, client.getServerURI());
		}
	}
	
	/* (non-Javadoc)
	 * @see it.peterhansl.knx.knx2mqtt.gateway.BusEventGateway#isInitialized()
	 */
	@Override
	public boolean isInitialized() {
		return client != null;
	}
	
	/* (non-Javadoc)
	 * @see it.peterhansl.knx.knx2mqtt.gateway.BusEventGateway#isConnected()
	 */
	@Override
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
