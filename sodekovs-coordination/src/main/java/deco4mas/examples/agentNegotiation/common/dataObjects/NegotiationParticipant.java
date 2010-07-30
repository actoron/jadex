package deco4mas.examples.agentNegotiation.common.dataObjects;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bridge.IComponentIdentifier;
import deco4mas.examples.agentNegotiation.common.negotiationInformation.DirectNegotiationParticipantInformation;
import deco4mas.examples.agentNegotiation.sa.coordination.negotiationstrategy.IDeliverProposalStrategy;

public class NegotiationParticipant
{
	private IDeliverProposalStrategy strategy;
	private IComponentIdentifier participant;
	private ServiceType service;
	private IBDIExternalAccess exta;

	public NegotiationParticipant(DirectNegotiationParticipantInformation information)
	{
		this.participant = information.getParticipant();
		this.strategy = information.getStrategy();
		this.service = information.getServiceType();
		this.exta = information.getExternalAccess();
	}

	public ServiceProposal deliverProposal(ServiceOffer offer)
	{
		return strategy.deliverProposal(offer);
	}

	public IComponentIdentifier getParticipant()
	{
		return participant;
	}

	public ServiceType getService()
	{
		return service;
	}

	public Boolean isBlackout()
	{
		return (Boolean) exta.getBeliefbase().getBelief("blackout").getFact();
	}
}
