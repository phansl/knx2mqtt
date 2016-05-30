package it.peterhansl.knx.knx2mqtt.config;

import java.net.InetAddress;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="knx")
public class BusMonitorConfiguration {
	
	/**
	 * IP or hostname of the knx router
	 */
	private InetAddress router;
	
	private boolean nat = false;
	
	private List<GroupAddressConfig> groupAddresses;

	public InetAddress getRouter() {
		return router;
	}

	public void setRouter(InetAddress routerIP) {
		this.router = routerIP;
	}

	public boolean isNat() {
		return nat;
	}

	public void setNat(boolean nat) {
		this.nat = nat;
	}

	public List<GroupAddressConfig> getGroupAddresses() {
		return groupAddresses;
	}

	public void setGroupAddresses(List<GroupAddressConfig> groupAddressConfig) {
		this.groupAddresses = groupAddressConfig;
	}

}
