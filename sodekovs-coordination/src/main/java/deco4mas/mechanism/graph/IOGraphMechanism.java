package deco4mas.mechanism.graph;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import deco4mas.coordinate.environment.CoordinationSpace;
import deco4mas.helper.Constants;
import deco4mas.mechanism.CoordinationInfo;
import deco4mas.mechanism.ICoordinationMechanism;
import deco4mas.util.xml.XmlUtil;

/**
 * Implements the coordination mechanism based on a given {@link IOGraph}.
 * 
 * @author Thomas Preisler
 */
public class IOGraphMechanism extends ICoordinationMechanism {

	private IOGraph graph = null;

	private Integer eventNumber = null;

	public IOGraphMechanism(CoordinationSpace space) {
		super(space);
		eventNumber = 0;
	}

	@Override
	public void start() {
		// read in the IOGraph from the specified XML File
		String graphFile = mechanismConfiguration.getProperty("graph_file");
		try {
			graphFile = new File("..").getCanonicalPath() + "/sodekovs-coordination/src/main/java/" + graphFile;
			graph = (IOGraph) XmlUtil.retrieveFromXML(IOGraph.class, graphFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
			List<IComponentDescription> descriptions = getComponentDescriptions(receiver);
			space.publishCoordinationEvent(obj, descriptions, getRealisationName(), ++eventNumber);
		}
	}

	private List<IComponentDescription> getComponentDescriptions(List<String> receiver) {
		List<IComponentDescription> descriptions = new ArrayList<IComponentDescription>();

		for (String receiverName : receiver) {
			descriptions.add(getSpace().getDescriptionMapping().get(receiverName));
		}

		return descriptions;
	}

	/**
	 * Looks up in the graph which agents should receive coordination information from the given sender agent.
	 * 
	 * @param senderAgent
	 *            the given sender agent
	 * @return a {@link List} of receiving agents (their local names)
	 */
	private List<String> lookupReceiver(IComponentIdentifier senderAgent) {
		if (graph != null) {
			List<String> receiver = new ArrayList<String>();

			GraphEntry entry = graph.lookupEntry(senderAgent.getLocalName());
			if (entry != null) {
				receiver.addAll(entry.getOutputs());
			}

			return receiver;
		}
		return null;
	}
}