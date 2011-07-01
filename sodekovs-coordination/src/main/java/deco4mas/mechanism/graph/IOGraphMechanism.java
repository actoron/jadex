package deco4mas.mechanism.graph;

import jadex.bridge.IComponentIdentifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import deco4mas.coordinate.environment.CoordinationSpace;
import deco4mas.util.xml.XmlUtil;

/**
 * Implements the coordination mechanism based on a given {@link IOGraph}.
 * 
 * @author Thomas Preisler
 */
public class IOGraphMechanism extends GenericGraphMechanism {

	public IOGraphMechanism(CoordinationSpace space) {
		super(space);
	}

	private IOGraph graph = null;

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

	/**
	 * Looks up in the graph which agents should receive coordination information from the given sender agent.
	 * 
	 * @param senderAgent
	 *            the given sender agent
	 * @return a {@link List} of receiving agents (their local names)
	 */
	protected List<String> lookupReceiver(IComponentIdentifier senderAgent) {
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