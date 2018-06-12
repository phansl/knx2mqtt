package it.peterhansl.knx.knx2mqtt.config;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="knx")
public class BusMonitorConfiguration {
	
	/**
	 * local IP in same network segment as router
	 */
	private InetAddress local;
	
	/**
	 * IP or hostname of the knx router
	 */
	private InetAddress router;
	
	private boolean nat = false;
	
	private List<GroupAddressConfig> groupAddresses;
	
	private Map<Integer, GroupAddressConfig> groupAddressesMap;

	@PostConstruct
	public void postConstruct() {
		groupAddressesMap = new HashMap<>();
		if (groupAddresses != null) {
			for (GroupAddressConfig cfg : groupAddresses) {
				groupAddressesMap.put(cfg.getAddress(), cfg);
			}
		}
	}
	
	public Map<Integer, GroupAddressConfig> getGroupAddressesMap() {
		return groupAddressesMap;
	}
	
	public InetAddress getLocal() {
		return local;
	}

	public void setLocal(InetAddress local) {
		this.local = local;
	}

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
