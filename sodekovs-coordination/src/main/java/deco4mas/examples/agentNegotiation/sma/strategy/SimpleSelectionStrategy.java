package deco4mas.examples.agentNegotiation.sma.strategy;

import jadex.bridge.IComponentIdentifier;
import java.util.SortedMap;

public class SimpleSelectionStrategy implements ISelectionStrategy
{

	public IComponentIdentifier selectProposal(SortedMap<Double, IComponentIdentifier> orderedProposal)
	{
		IComponentIdentifier selected = null;

		// just select the best
		if (!orderedProposal.isEmpty())
		{
			selected = orderedProposal.get(orderedProposal.lastKey());
		}
		return selected;
	}

}
