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
	private Boolean blackout;
//	private IBDIExternalAccess exta;

	public NegotiationParticipant(DirectNegotiationParticipantInformation information)
	{
		this.participant = information.getParticipant();
		this.strategy = information.getStrategy();
		this.service = information.getServiceType();
		this.blackout = information.getBlackout();
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
		return blackout;
	}
	
	public void setBlackout(Boolean blackout)
	{
		this.blackout = blackout;
	}
	
	@Override
	public boolean equals(Object obj)
	{	
		if (obj instanceof NegotiationParticipant)
		{
			NegotiationParticipant neg = (NegotiationParticipant) obj;
			if (this.getParticipant().getLocalName().equals(neg.getParticipant().getLocalName()))
			{
				return true;
			}
		}
		return false;
	}
	
	
}
