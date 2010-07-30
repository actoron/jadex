package deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy;

import jadex.bridge.IComponentIdentifier;
import java.util.SortedMap;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceProposal;

public interface ISelectionStrategy
{
	/**
	 * select a propsal from the sorted Map
	 * 
	 * @param orderedProposal
	 *            sorted Map
	 * @return selected ServiceProposal
	 */
	public ServiceProposal selectProposal(SortedMap<Double, ServiceProposal> orderedProposal);
}
