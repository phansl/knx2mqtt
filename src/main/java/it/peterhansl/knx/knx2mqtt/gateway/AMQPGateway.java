package it.peterhansl.knx.knx2mqtt.gateway;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import it.peterhansl.iot.dahome.knx.model.BusEvent;
import it.peterhansl.knx.knx2mqtt.config.AMQPConfig;

public class AMQPGateway implements BusEventGateway {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AMQPGateway.class); 
	
	@Autowired
	private AMQPConfig config;
	
	@Autowired
	private AmqpAdmin admin;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	private boolean active = false;
	
	@PostConstruct
	public void postConstruct() {
		try {
			Exchange exchange = ExchangeBuilder.topicExchange("knx.messages").durable(true).build();
			Queue queue = new Queue(config.getQueue(), true);
			admin.declareQueue(queue);
			admin.declareExchange(exchange);
			admin.declareBinding(BindingBuilder.bind(queue).to(exchange).with("#").noargs());
			active = true;
		} catch (Exception e) {
			LOGGER.warn("Could not connect to RabbitMQ, no KNX telegrams will be published.");
			active = false;
		}
	}
	
	@Override
	public void publishAsync(String routingKey, BusEvent message) {
		if (active) {
			LOGGER.debug("publishAsync (routingKey={}; message={})", routingKey, message.toString());
			rabbitTemplate.convertAndSend("knx.messages", routingKey, message);
		}
	}

	@Override
	public void publishSync(String destination, BusEvent message) {
		if (active) {
			LOGGER.debug("publishSync (destination={}; message={})", destination, message.toString());
		}
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
