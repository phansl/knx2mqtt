package it.peterhansl.knx.knx2mqtt.config;

import java.net.InetAddress;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mqtt")
public class MQTTConfig {
	
	private static final String DEFAULT_CLIENT_NAME = "knx2mqtt";

	private String clientName;
	
	private InetAddress host;
	
	private int port;
	
	private String protocol;
	
	private String username;
	
	private String password;

	public String getClientName() {
		if (clientName == null) {
			clientName = DEFAULT_CLIENT_NAME;
		}
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public InetAddress getHost() {
		return host;
	}

	public void setHost(InetAddress host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
