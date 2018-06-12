package it.peterhansl.knx.knx2mqtt.monitor;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Metrics;
import it.peterhansl.iot.dahome.knx.model.BusEvent;
import it.peterhansl.knx.knx2mqtt.config.BusMonitorConfiguration;
import it.peterhansl.knx.knx2mqtt.config.GroupAddressConfig;
import it.peterhansl.knx.knx2mqtt.gateway.BusEventGateway;
import tuwien.auto.calimero.DetachEvent;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.datapoint.CommandDP;
import tuwien.auto.calimero.datapoint.Datapoint;
import tuwien.auto.calimero.datapoint.DatapointMap;
import tuwien.auto.calimero.dptxlator.DPTXlator;
import tuwien.auto.calimero.dptxlator.TranslatorTypes;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.process.ProcessEvent;
import tuwien.auto.calimero.process.ProcessListener;
import tuwien.auto.calimero.process.ProcessListenerEx;

@Component
public class BusProcessListener extends ProcessListenerEx implements ProcessListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(BusProcessListener.class);
	
	@Autowired
	private BusMonitorConfiguration busMonitorConfiguration;
	
	@Autowired
	private BusEventGateway gateway;
	
	private DatapointMap datapoints;
	
	private long lastMessageReceived = System.currentTimeMillis();
	
	@PostConstruct
	public void initConfigMap() {
		int configCount = busMonitorConfiguration.getGroupAddresses().size();
		
		LOG.debug("initializing group address config map: {} configurations", configCount);
		datapoints = new DatapointMap();
		
		for (GroupAddressConfig config: busMonitorConfiguration.getGroupAddresses()) {
			GroupAddress ga = new GroupAddress(config.getAddress());
			
			Datapoint datapoint = new CommandDP(ga, config.getText(), 0, config.getDpt());
			datapoints.add(datapoint);
			
			LOG.debug("Added config for {} [{}]", ga.toString(), ga.getRawAddress());
		}
	}
	
	@Override
	public void groupWrite(ProcessEvent groupWriteEvent) {
		
		lastMessageReceived = System.currentTimeMillis();
		
		Datapoint dp = datapoints.get(groupWriteEvent.getDestination());
		
		String unknown = "";
		
		if (dp != null) {
			
			String value = null;
			try {
				DPTXlator xlator = TranslatorTypes.createTranslator(dp.getMainNumber(), dp.getDPT());
				xlator.setData(groupWriteEvent.getASDU());
				value = xlator.getValue();
			} catch (KNXException e) {
				LOG.error("no xlator found for address {} [{}] with type {}", 
						groupWriteEvent.getDestination(), 
						groupWriteEvent.getDestination().getRawAddress(),
						dp.getDPT());
			} finally {
				if (null == value) {
					value = dp.getDPT();
				}
			}
			
			BusEvent msg = new BusEvent(
					dp.getName(),
					groupWriteEvent.getDestination().getRawAddress(),
					dp.getMainNumber(),
					dp.getDPT(),
					groupWriteEvent.getASDU());
			
			GroupAddressConfig cfg = busMonitorConfiguration.getGroupAddressesMap().get(msg.getAddress());
			if ((null != cfg) && (null != cfg.getTopics())) {
				for (String topic : cfg.getTopics()) {
					gateway.publishAsync(topic, msg);
				}
			} else {
				gateway.publishAsync("home.knx", msg);
			}
			
			LOG.debug("groupWrite to {} [{}] - {}: {}", 
					groupWriteEvent.getDestination(), 
					groupWriteEvent.getDestination().getRawAddress(), 
					dp.getName(),
					value);
		} else {
			unknown = "unknown.";
			LOG.debug("groupWrite to {} [{}] - {}", 
					groupWriteEvent.getDestination(), 
					groupWriteEvent.getDestination().getRawAddress(), 
					unknown);
			
		}
		
		
		// update metrics
		Metrics.counter("counter.knx.groupWrite.total").increment();
		Metrics.counter("counter.knx.groupWrite." + unknown + groupWriteEvent.getDestination().getRawAddress()).increment();
	}

	@Override
	public void detached(DetachEvent detachedEvent) {
		LOG.debug("DETACHED");
	}
	
	@Override
	public void groupReadRequest(ProcessEvent groupReadRequestEvent) {
		LOG.info("groupReadRequest");
		
	}

	@Override
	public void groupReadResponse(ProcessEvent groupReadResponseEvent) {
		LOG.info("groupReadResponse");
		
	}

	public long getLastMessageReceived() {
		return lastMessageReceived;
	}

}
