package deco4mas.examples.agentNegotiation.sma.strategy;

import jadex.bridge.IComponentIdentifier;
import deco4mas.examples.agentNegotiation.sma.ServiceAgentHistory;

public class HistorytimeTrustFunction implements ITrustFunction
{
	private ServiceAgentHistory history;

	public HistorytimeTrustFunction(ServiceAgentHistory history)
	{
		this.history = history;
	}

	public Double getTrust(IComponentIdentifier sa)
	{
		return history.getReliabilityFor(sa, Long.MIN_VALUE);
	}

}
