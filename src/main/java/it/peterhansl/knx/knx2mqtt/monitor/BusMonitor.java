package it.peterhansl.knx.knx2mqtt.monitor;

import java.net.InetSocketAddress;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.xml.ws.ServiceMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.peterhansl.knx.knx2mqtt.config.BusMonitorConfiguration;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.knxnetip.KNXnetIPConnection;
import tuwien.auto.calimero.link.KNXLinkClosedException;
import tuwien.auto.calimero.link.KNXNetworkLink;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;

@Component
public class BusMonitor {
	
	private static final Logger LOG = LoggerFactory.getLogger(BusMonitor.class);
	
	/**
	 * External configuration for the BusMonitor.
	 */
	private BusMonitorConfiguration busMonitorConfiguration;
	
	/**
	 * Persistent network link to the KNX router
	 */
	private KNXNetworkLink knxLink = null;
	
	/**
	 * Handles the knx bus events
	 */
	private ProcessCommunicator processCommunicator;
	
	@Inject
	private BusProcessListener busProcessListener;
	
	@Inject
	public BusMonitor(BusMonitorConfiguration busMonitorConfiguration) {
		this.busMonitorConfiguration = busMonitorConfiguration;
		
		LOG.debug("construct BusMonitor");
		LOG.debug("BusMonitor listening to router at {}", busMonitorConfiguration.getRouter().getHostAddress());
	}
	
	@PostConstruct
	public void initialize() {
		if (knxLink != null) {
			return;
		}
		
		// Initialize link to router
		open();
		
		// initialize ProcessCommunicator on established router link
		attach();
		
	}
	
	private void detach() {
		processCommunicator.removeProcessListener(busProcessListener);
		processCommunicator.detach();
	}
	
	private void attach() {
		if ((knxLink != null) && (busProcessListener != null)) {
			try {
				processCommunicator = new ProcessCommunicatorImpl(knxLink);
				processCommunicator.addProcessListener(busProcessListener);
				LOG.debug("ProcessListener attached to router {}", busMonitorConfiguration.getRouter().getHostAddress());
			} catch (KNXLinkClosedException e) {
				LOG.error("Cannot attach process communicator to knxLink for router {}", busMonitorConfiguration.getRouter().getHostAddress(), e);
			}
		}
	}
	
	private void open() {
		try {
			if (busMonitorConfiguration.isNat()) {
				knxLink = new KNXNetworkLinkIP(KNXNetworkLinkIP.TUNNELING, null, new InetSocketAddress(busMonitorConfiguration.getRouter().getHostAddress(), KNXnetIPConnection.DEFAULT_PORT), true, TPSettings.TP1);
			} else {
				knxLink = new KNXNetworkLinkIP(busMonitorConfiguration.getRouter().getHostAddress(), TPSettings.TP1);
			}
			LOG.debug("knx link established via router {}, NAT={}", busMonitorConfiguration.getRouter().getHostAddress(), busMonitorConfiguration.isNat());
		} catch (KNXException | InterruptedException e) {
			LOG.error("Cannot establish route to knx via router {}, NAT={}", busMonitorConfiguration.getRouter().getHostAddress(), busMonitorConfiguration.isNat());
			System.err.println(e);
		}
	}
	
	private void reattach() {
		LOG.info("reattaching link");
		detach();
		knxLink.close();
		open();
		attach();
	}
	
	@Scheduled(cron = "0/10 * * * * *")
	public void healthCheck() {
		if (knxLink == null) {
			return;
		}
		
		long lmr = busProcessListener.getLastMessageReceived();
		long diff = System.currentTimeMillis() - lmr;
		if (diff > 1000 * 30) {
			reattach();
		}
		
		LOG.debug("BusMonitor health: knxLink {} {}s without message", toUpOrDown(knxLink.isOpen()), (diff / 1000));
	}
	
	private String toUpOrDown(boolean b) {
		if (b) {
			return "UP";
		} else {
			return "DOWN";
		}
	}

}
