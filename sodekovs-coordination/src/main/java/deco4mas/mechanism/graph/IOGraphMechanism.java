/**
 * 
 */
package deco4mas.mechanism.graph;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.ThreadSuspendable;
import jadex.micro.ExternalAccess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	
	public static final String GRAPH_ID_FIELD = "graphId";

	private IOGraph graph = null;

	private Integer eventNumber = null;
	
	private IComponentManagementService cms = null;

	public IOGraphMechanism(CoordinationSpace space) {
		super(space);
		eventNumber = 0;
		cms = (IComponentManagementService) SServiceProvider.getServiceUpwards(space.getExternalAccess().getServiceProvider(), IComponentManagementService.class).get(new ThreadSuspendable());
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
		
		List<IComponentDescription> receiverDescriptions = new ArrayList<IComponentDescription>();
		
		List<String> receiver = lookupReceiver(senderAgent);
		if (receiver != null && !receiver.isEmpty()) {
			IComponentDescription[] descriptions = (IComponentDescription[]) cms.getComponentDescriptions().get(new ThreadSuspendable());
			for (IComponentDescription description : descriptions) {
				Object value = getArgumentValue(description.getName(), GRAPH_ID_FIELD);
				if (value != null && receiver.contains((String) value)) {
					receiverDescriptions.add(description);
					receiver.remove(value);
				}
			}
			
			if (!receiverDescriptions.isEmpty()) {
				space.publishCoordinationEvent(obj, receiverDescriptions, getRealisationName(), ++eventNumber);
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	private Object getArgumentValue(IComponentIdentifier identifier, String argumentKey) {
		IExternalAccess exta = (IExternalAccess) cms.getExternalAccess(identifier).get(new ThreadSuspendable());
				
		if (exta instanceof ExternalAccess) {
			ExternalAccess microExta = (ExternalAccess) exta;
			Map arguments = microExta.getInterpreter().getArguments();
			
			if (arguments != null) {
				return arguments.get(argumentKey);		
			}
		}
		
		return null;
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
			
			Object value = getArgumentValue(senderAgent, GRAPH_ID_FIELD);
			if (value != null && value instanceof String) {
				String sender = (String) value;
				
				GraphEntry entry = graph.lookupEntry(sender);
				if (entry != null) {
					receiver.addAll(entry.getOutputs());
				}

				return receiver;
			}
		}
		
		return null;
	}
}