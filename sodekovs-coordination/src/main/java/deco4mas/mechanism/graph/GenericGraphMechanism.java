package deco4mas.mechanism.graph;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.cms.IComponentDescription;

import java.util.ArrayList;
import java.util.List;

import deco4mas.coordinate.environment.CoordinationSpace;
import deco4mas.helper.Constants;
import deco4mas.mechanism.CoordinationInfo;
import deco4mas.mechanism.ICoordinationMechanism;

/**
 * A generic graph based coordination mechanism.
 * 
 * @author Thomas Preisler
 */
public abstract class GenericGraphMechanism extends ICoordinationMechanism {

	/** The event number */
	private Integer eventNumber = null;

	/**
	 * Constructor.
	 * 
	 * @param space
	 */
	public GenericGraphMechanism(CoordinationSpace space) {
		super(space);
		this.eventNumber = 0;
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

	/**
	 * Returns the {@link IComponentDescription}s for the given agent's local name as provided by the mapping in {@link CoordinationSpace#getDescriptionMapping()}.
	 * 
	 * @param receiver
	 *            the given agent's local names
	 * @return a {@link List} of {@link IComponentDescription}s
	 */
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
	protected abstract List<String> lookupReceiver(IComponentIdentifier senderAgent);
}