package jadex.bdi.examples.booktrading.buyer;

import java.util.Date;

import jadex.bdi.examples.booktrading.common.NegotiationReport;
import jadex.bdi.examples.booktrading.common.Order;
import jadex.bdi.planlib.protocols.NegotiationRecord;
import jadex.bdi.planlib.protocols.ParticipantProposal;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.fipa.DFComponentDescription;
import jadex.bridge.fipa.DFServiceDescription;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.df.IDF;
import jadex.bridge.service.types.df.IDFComponentDescription;
import jadex.bridge.service.types.df.IDFServiceDescription;

/**
 * The plan tries to purchase a book.
 */
public class PurchaseBookPlan extends Plan
{
	//-------- methods --------

	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
//		System.out.println("PurchaseBookPlan");
		
		// Get order properties and calculate acceptable price.
		Order order = (Order)getParameter("order").getValue();
		double time_span = order.getDeadline().getTime() - order.getStartTime();
		double elapsed_time = getTime() - order.getStartTime();
		double price_span = order.getLimit() - order.getStartPrice();
		int acceptable_price = (int)(price_span * elapsed_time / time_span)
			+ order.getStartPrice();

		// Find available seller agents.
//		IDF	df	= (IDF)SServiceProvider.searchService(getScope().getServiceProvider(), new ServiceQuery<>( IDF.class)).get();
		IDF	df	= (IDF)getAgent().getFeature(IRequiredServicesFeature.class).getService("dfservice").get();
		IDFServiceDescription	service	= new DFServiceDescription(null, "service_seller", null);
		IDFComponentDescription	desc	= new DFComponentDescription(null, service);
		IDFComponentDescription[] result = df.search(desc, null).get();
		if(result.length == 0)
		{
//			System.out.println("No seller found, purchase failed.");
			generateNegotiationReport(order, null, acceptable_price);
			fail();
		}
		
		IComponentIdentifier[] sellers = new IComponentIdentifier[result.length];
		for(int i = 0; i < result.length; i++)
			sellers[i] = result[i].getName();
		//System.out.println("found: "+SUtil.arrayToString(sellers));

		// Initiate a call-for-proposal.
		IGoal cnp = createGoal("cnp_initiate");
		cnp.getParameter("cfp").setValue(order.getTitle());
		cnp.getParameter("cfp_info").setValue(Integer.valueOf(acceptable_price));
		cnp.getParameterSet("receivers").addValues(sellers);		
		try
		{
			dispatchSubgoalAndWait(cnp);
			
			NegotiationRecord rec = (NegotiationRecord)cnp.getParameterSet("history").getValues()[0];
			generateNegotiationReport(order, rec, acceptable_price);
			
			// If contract-net succeeds, store result in order object.
			order.setState(Order.DONE);
			order.setExecutionPrice((Integer)(cnp.getParameterSet("result").getValues()[0]));
			order.setExecutionDate(new Date(getTime()));
		}
		catch(GoalFailureException e)
		{
			NegotiationRecord rec = (NegotiationRecord)cnp.getParameterSet("history").getValues()[0];
			generateNegotiationReport(order, rec, acceptable_price);
			
			fail();
		}
		//System.out.println("result: "+cnp.getParameter("result").getValue());
	}
	
	/**
	 *  Generate and add a negotiation report.
	 */
	protected void generateNegotiationReport(Order order, NegotiationRecord rec, double acceptable_price)
	{
		String report = "Accepable price: "+acceptable_price+", proposals: ";
		if(rec!=null)
		{
			ParticipantProposal[] proposals = rec.getProposals();
			for(int i=0; i<proposals.length; i++)
			{
				report += proposals[i].getProposal()+"-"+proposals[i].getParticipant().getLocalName();
				if(i+1<proposals.length)
					report += ", ";
			}
		}
		else
		{
			report	+= "No seller found, purchase failed.";
		}
		NegotiationReport nr = new NegotiationReport(order, report, rec!=null ? rec.getStarttime() : getScope().getTime());
		//System.out.println("REPORT of agent: "+getAgentName()+" "+report);
		getBeliefbase().getBeliefSet("negotiation_reports").addFact(nr);
	}
}