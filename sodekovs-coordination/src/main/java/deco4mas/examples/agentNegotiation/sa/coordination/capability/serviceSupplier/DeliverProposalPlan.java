package deco4mas.examples.agentNegotiation.sa.coordination.capability.serviceSupplier;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.Bid;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.ServiceOffer;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.ServiceProposal;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.ServiceType;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.sa.masterSa.AgentType;

/**
 * Make a proposal
 */
public class DeliverProposalPlan extends Plan
{

	public void body()
	{
		if (!(Boolean)getBeliefbase().getBelief("blackout").getFact() && !(Boolean)getBeliefbase().getBelief("signed").getFact())
		{
			Logger saLogger = AgentLogger.getTimeEvent(this.getComponentName());

			// get offer
			IInternalEvent bidGoal = (IInternalEvent) getReason();
			ServiceOffer offer = (ServiceOffer) bidGoal.getParameter("offer").getValue();
			ServiceType myService = (ServiceType) getBeliefbase().getBelief("providedService").getFact();

			// check if my service
			if (offer.getServiceType().getName().equals(myService.getName()))
			{
				// get agentType
				AgentType agentType = (AgentType) getBeliefbase().getBelief("agentType").getFact();

				// cost = medCost if costCharacter = 0.5 / cost < medCost if
				// costCharacter < 0.5 / v.v
				// if (new Random().nextBoolean()) random = 0.1;
				Double cost = myService.getMedCost() * (0.5 + agentType.getCostCharacter());

				// s. cost
				Double duration = myService.getMedDuration() * (0.5 + agentType.getCostCharacter());

				System.out.println(this.getComponentName() + ": " + cost + "/" + duration);
				saLogger.info("deliver proposal(" + offer.getId() + ") with C(" + cost + ") and D(" + duration + ")");

				// announce a Proposal
				IInternalEvent announceProposal = createInternalEvent("announceProposal");
				Bid bid = new Bid();
				bid.setBid("cost", cost);
				bid.setBid("duration", duration);

				ServiceProposal proposal = new ServiceProposal(offer.getId(), offer.getServiceType(), this.getComponentIdentifier(), bid);
				announceProposal.getParameter("proposal").setValue(proposal);
				announceProposal.getParameter("task").setValue("proposal");
				dispatchInternalEvent(announceProposal);
			}
		}
	}
}
