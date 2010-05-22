package deco4mas.examples.agentNegotiation.sma.strategy;

import jadex.bridge.IComponentIdentifier;
import java.util.SortedMap;

public interface ISelectionStrategy
{
	/**
	 * select a propsal from the sorted Map
	 * @param orderedProposal sorted Map
	 * @return selected IComponentIdentifier
	 */
	public IComponentIdentifier selectProposal(SortedMap<Double, IComponentIdentifier> orderedProposal);
}
