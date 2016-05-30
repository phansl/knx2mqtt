package it.peterhansl.knx.knx2mqtt.config;

import java.util.List;

import org.springframework.stereotype.Component;

/**
 *
 * List of group addresses, see http://stackoverflow.com/questions/32593014/mapping-list-in-yaml-to-list-of-objects-in-spring-boot 
 * 
 * @author Philip
 *
 */
@Component
public class GroupAddressConfig {

	/**
	 * Groupaddress short (not in form x/y/z) 
	 */
	private int address;
	
	/**
	 * DPT for this address, see https://de.wikipedia.org/wiki/KNX-Standard
	 */
	private String dpt;
	
	/**
	 * Description for this group address
	 */
	private String text;
	
	/**
	 * Topics for publishing the knx events 
	 */
	private List<String> topics;

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
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public List<String> getTopics() {
		return topics;
	}

	public void setTopics(List<String> topics) {
		this.topics = topics;
	}

}
