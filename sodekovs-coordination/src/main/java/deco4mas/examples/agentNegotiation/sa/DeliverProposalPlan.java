package deco4mas.examples.agentNegotiation.sa;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import java.util.Random;
import deco4mas.examples.agentNegotiation.deco.ServiceProposal;
import deco4mas.examples.agentNegotiation.deco.ServiceOffer;

/**
 * Make a simple bid based on a random value.
 */
public class DeliverProposalPlan extends Plan
{
	/**
	 * The plan body.
	 */
	public void body()
	{
		// get offer
		IGoal bidGoal = (IGoal) getReason();
		ServiceOffer offer = (ServiceOffer) bidGoal.getParameter("offer").getValue();
		
		//check if my service
		if (offer.getServiceType().equals(getBeliefbase().getBelief("providedService").getFact()))
			{
			// get last proposal
			Integer proposal = (Integer) getBeliefbase().getBelief("proposalBase").getFact();

			Random rnd = new Random();
			Double add = new Double(0);
			//add something
//			if (rnd.nextDouble() > new Double(0.6))
//			{
				add = rnd.nextDouble() * 100;
				proposal = proposal + add.intValue();
//			}
//			getBeliefbase().getBelief("proposalBase").setFact(proposal);
			
			System.out.println(((String) getBeliefbase().getBelief("providedService").getFact()).substring(0, 1) + ": "
				+ getComponentIdentifier().getLocalName() + " [" + proposal + "]");

			// announce a Proposal
			IInternalEvent bidE = createInternalEvent("announceProposal");
			ServiceProposal bid = new ServiceProposal(offer.getId(), offer.getServiceType(), this.getComponentIdentifier(), proposal.doubleValue());
			bidE.getParameter("proposal").setValue(bid);
			bidE.getParameter("task").setValue("proposal");
			dispatchInternalEvent(bidE);
			}

		
	}
}
