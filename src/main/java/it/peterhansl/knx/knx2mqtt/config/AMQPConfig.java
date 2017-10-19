package it.peterhansl.knx.knx2mqtt.config;

import java.net.InetAddress;

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
	
	private static final String DEFAULT_CLIENT_NAME = "knx2amqp";

	private String clientName;
	
	private InetAddress host;
	
	private int port;
	
	private String protocol;
	
	private String username;
	
	private String password;
	
	@Bean
	@ConditionalOnProperty(prefix = "amqp", value = "host", matchIfMissing = false)
	public BusEventGateway busEventGateway() {
		return new AMQPGateway(this);
	}

	public String getClientName() {
		if (clientName == null) {
			clientName = DEFAULT_CLIENT_NAME;
		}
		return clientName;
	}

	public String getConnectionURLString() {
		StringBuilder connString = new StringBuilder();
		
		connString.append(protocol);
		connString.append("://");
		connString.append(host.getHostAddress());
		connString.append(":");
		connString.append(port);
		
		return connString.toString();
	}

	

}
