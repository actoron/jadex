package deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy;

import java.util.SortedMap;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceProposal;

public class SimpleSelectionStrategy implements ISelectionStrategy
{
	public ServiceProposal selectProposal(SortedMap<Double, ServiceProposal> orderedProposal)
	{
		ServiceProposal selected = null;

		// just select the best
		if (!orderedProposal.isEmpty())
		{
			selected = orderedProposal.get(orderedProposal.lastKey());
		}
		return selected;
	}

}
