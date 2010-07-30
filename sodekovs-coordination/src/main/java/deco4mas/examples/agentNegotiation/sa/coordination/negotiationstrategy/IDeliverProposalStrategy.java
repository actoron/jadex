package deco4mas.examples.agentNegotiation.sa.coordination.negotiationstrategy;

import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceOffer;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceProposal;

/**
 * Should determine a bid strategy for a serviceSupplier
 * 
 * @author 5Haubeck
 */
public interface IDeliverProposalStrategy
{
	/**
	 * deliver a proposal on the given {@link ServiceOffer}
	 * 
	 * @param offer
	 *            the {@link ServiceOffer} to bid on
	 * @return the calculated {@link ServiceProposal}
	 */
	public ServiceProposal deliverProposal(ServiceOffer offer);
}
