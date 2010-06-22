package deco4mas.examples.agentNegotiation.sma.strategy;

import jadex.bridge.IComponentIdentifier;

public interface ITrustFunction
{
	/**
	 * returns the trust for the given identifier
	 * 
	 * @param sa
	 *            the componentIdentifier to get trust for
	 * @return the trust ( 0.0 - 1.0
	 */
	public Double getTrust(IComponentIdentifier sa, Long time);
}
