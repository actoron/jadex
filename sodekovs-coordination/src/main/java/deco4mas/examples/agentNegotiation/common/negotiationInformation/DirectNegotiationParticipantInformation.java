package deco4mas.examples.agentNegotiation.common.negotiationInformation;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bridge.IComponentIdentifier;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceOffer;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceProposal;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceType;
import deco4mas.examples.agentNegotiation.deco.media.NegotiationMechanism;
import deco4mas.examples.agentNegotiation.sa.coordination.negotiationstrategy.IDeliverProposalStrategy;

public class DirectNegotiationParticipantInformation extends NegotiationInformation
{
	private IDeliverProposalStrategy strategy;
	private IComponentIdentifier participant;
	private IBDIExternalAccess externalAcccess;

	public DirectNegotiationParticipantInformation(Integer id, IComponentIdentifier participant, ServiceType serviceType,
		IDeliverProposalStrategy strategy, IBDIExternalAccess externalAcccess)
	{
		super(id, NegotiationMechanism.NAME, serviceType);
		this.participant = participant;
		this.strategy = strategy;
		this.externalAcccess = externalAcccess;
	}

	public IDeliverProposalStrategy getStrategy()
	{
		return strategy;
	}

	public IComponentIdentifier getParticipant()
	{
		return participant;
	}
	
	public IBDIExternalAccess getExternalAccess()
	{
		return externalAcccess;
	}

	@Override
	public String toString()
	{
		return "DirectNegotiationParticipantInformation(" + id + " , " + mediumType + " , " + participant + " , " + serviceType + ")";
	}
}
