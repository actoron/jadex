package deco4mas.mechanism.graph;

import haw.mmlab.production_line.configuration.ProductionLineConfiguration;
import haw.mmlab.production_line.configuration.Robot;
import haw.mmlab.production_line.configuration.Transport;
import jadex.bridge.IComponentIdentifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import deco4mas.coordinate.environment.CoordinationSpace;
import deco4mas.helper.Constants;
import deco4mas.mechanism.CoordinationInfo;
import deco4mas.mechanism.ICoordinationMechanism;
import deco4mas.util.file.FileFinder;
import deco4mas.util.xml.XmlUtil;

public class RingGraphMechanism extends ICoordinationMechanism {

	private ProductionLineConfiguration plc = null;

	private Integer eventNumber = null;

	public RingGraphMechanism(CoordinationSpace space) {
		super(space);
		eventNumber = 0;
	}

	@Override
	public void start() {
		// read in the IOGraph from the specified XML File
		String graphFile = mechanismConfiguration.getProperty("graph_file");
		File file = FileFinder.findFiles(new File("."), graphFile).get(0);
		try {
			plc = (ProductionLineConfiguration) XmlUtil.retrieveFromXML(ProductionLineConfiguration.class, file.getPath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
	}

	@Override
	public void restart() {
	}

	@Override
	public void suspend() {
	}

	@Override
	public void perceiveCoordinationEvent(Object obj) {
		CoordinationInfo ci = (CoordinationInfo) obj;
		IComponentIdentifier senderAgent = (IComponentIdentifier) ci.getValueByName(Constants.SENDER_AGENT);
		List<String> receiver = lookupReceiver(senderAgent);
		if (receiver != null && !receiver.isEmpty()) {
			space.publishCoordinationEvent(obj, receiver, getRealisationName(), ++eventNumber);
		}
	}

	/**
	 * Looks up in the graph which agents should receive coordination information from the given sender agent.
	 * 
	 * @param senderAgent
	 *            the given sender agent
	 * @return a {@link List} of receiving agents (their local names)
	 */
	private List<String> lookupReceiver(IComponentIdentifier senderAgent) {
		if (plc != null) {
			List<String> receiver = new ArrayList<String>();

			for (Robot robot : plc.getRobots()) {
				if (robot.getAgentId().equals(senderAgent.getLocalName())) {
					receiver.add(robot.getOutput());
				}
			}

			if (receiver.isEmpty()) {
				for (Transport transport : plc.getTransports()) {
					if (transport.getAgentId().equals(senderAgent.getLocalName())) {
						receiver.add(transport.getOutput());
					}
				}
			}

			return receiver;
		}
		return null;
	}
}