package deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy;

public interface ITrustFunction
{
	/**
	 * returns the trust for the given identifier
	 * 
	 * @param sa
	 *            the sa to get trust for
	 * @return the trust ( 0.0 - 1.0
	 */
	public Double getTrust(String sa, Long time);
}
