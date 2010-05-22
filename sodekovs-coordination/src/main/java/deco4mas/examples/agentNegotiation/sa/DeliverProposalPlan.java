package deco4mas.examples.agentNegotiation.sa;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import java.util.Random;
import deco4mas.examples.agentNegotiation.ServiceType;
import deco4mas.examples.agentNegotiation.deco.Bid;
import deco4mas.examples.agentNegotiation.deco.ServiceProposal;
import deco4mas.examples.agentNegotiation.deco.ServiceOffer;

/**
 * Make a proposal
 */
public class DeliverProposalPlan extends Plan
{
	public void body()
	{
		// get offer
		IGoal bidGoal = (IGoal) getReason();
		ServiceOffer offer = (ServiceOffer) bidGoal.getParameter("offer").getValue();

		ServiceType myService = (ServiceType) getBeliefbase().getBelief("providedService").getFact();

		// check if my service
		if (offer.getServiceType().getName().equals(myService.getName()))
		{
			// get agentType
			AgentType agentType = (AgentType) getBeliefbase().getBelief("agentType").getFact();

			// cost = medCost if costCharacter = 0.5 / cost < medCost if
			// costCharacter < 0.5 / v.v
			Double cost = myService.getMedCost() * (0.5 + agentType.getCostCharacter());

			// s. cost
			Double duration = myService.getMedDuration() * (0.5 + agentType.getCostCharacter());

			// Random rnd = new Random();
			// Double add = new Double(0);
			// add something
			// if (rnd.nextDouble() > new Double(0.6))
			// {
			// add = rnd.nextDouble() * 100;
			// proposal = proposal + add.intValue();
			// }
			// getBeliefbase().getBelief("proposalBase").setFact(proposal);

			System.out.println(this.getComponentName() + ": " + cost + "/" + duration);

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
