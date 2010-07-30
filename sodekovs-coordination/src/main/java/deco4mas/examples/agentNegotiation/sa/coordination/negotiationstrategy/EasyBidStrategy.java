package deco4mas.examples.agentNegotiation.sa.coordination.negotiationstrategy;

import jadex.bridge.IComponentIdentifier;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceAgentType;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceBid;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceOffer;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceProposal;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceType;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;

public class EasyBidStrategy implements IDeliverProposalStrategy
{
	private IComponentIdentifier owner;
	private ServiceType service;
	private ServiceAgentType agentType;
	Logger saLogger;

	public EasyBidStrategy(IComponentIdentifier owner, ServiceType service, ServiceAgentType agentType)
	{
		this.owner = owner;
		this.service = service;
		this.agentType = agentType;
		saLogger = AgentLogger.getTimeEvent(owner.getLocalName());
	}

	public ServiceProposal deliverProposal(ServiceOffer offer)
	{
		// check if my service
		if (offer.getServiceType().getName().equals(service.getName()))
		{
			// cost = medCost if costCharacter = 0.5 / cost < medCost if
			// costCharacter < 0.5 / v.v
			// if (new Random().nextBoolean()) random = 0.1;
			Double cost = service.getMedCost() * (0.5 + agentType.getCostCharacter());

			// s. cost
			Double duration = service.getMedDuration() * (0.5 + agentType.getCostCharacter());

			System.out.println(owner.getLocalName() + ": " + cost + "/" + duration);
			saLogger.info("deliver proposal(" + offer.getId() + ") with C(" + cost + ") and D(" + duration + ")");

			// announce a Proposal
			ServiceBid bid = new ServiceBid();
			bid.setBid("cost", cost);
			bid.setBid("duration", duration);

			ServiceProposal proposal = new ServiceProposal(offer.getId(), offer.getServiceType(), owner, bid);
			return proposal;
		}
		return null;
	}

	public IComponentIdentifier getOwner()
	{
		return owner;
	}
}
