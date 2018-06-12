package it.peterhansl.knx.knx2mqtt.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import it.peterhansl.knx.knx2mqtt.gateway.AMQPGateway;
import it.peterhansl.knx.knx2mqtt.gateway.BusEventGateway;
import lombok.Data;

@Component
@ConfigurationProperties(prefix = "amqp")
@Data
public class AMQPConfig {
	
	private String queue;
	
	@Bean
	@ConditionalOnProperty(prefix = "amqp", value = "queue", matchIfMissing = false)
	public BusEventGateway busEventGateway() {
		return new AMQPGateway();
	}
	
	@Bean
	@ConditionalOnProperty(prefix = "amqp", value = "queue", matchIfMissing = false)
    public MessageConverter rabbitJsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }
	
	@Bean
	@ConditionalOnProperty(prefix = "amqp", value = "queue", matchIfMissing = false)
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,MessageConverter rabbitJsonMessageConverter) {
	    RabbitTemplate template = new RabbitTemplate(connectionFactory);
	    template.setMessageConverter(rabbitJsonMessageConverter);
	    return template;
	}
	
}
