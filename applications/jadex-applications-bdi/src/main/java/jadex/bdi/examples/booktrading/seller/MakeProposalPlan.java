package jadex.bdi.examples.booktrading.seller;

import jadex.bdi.examples.booktrading.common.NegotiationReport;
import jadex.bdi.examples.booktrading.common.Order;
import jadex.bdiv3x.runtime.Plan;

/**
 * The plan has the purpose to make an proposal for selling a book.
 */
public class MakeProposalPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		// Search suitable open orders.
//		Order[] suitableorders = (Order[])getParameterSet("suitableorders").getValues();
		Order	order	= (Order)createExpression("select one Order $order from $beliefbase.orders"
			+ " where $order.getTitle().equals($task) && $order.getState().equals(Order.OPEN)"
			+ " order by ($beliefbase.time - $order.getStartTime()) / ($order.getDeadline().getTime()-$order.getStartTime())")
			.execute("$task", getParameter("cfp").getValue());
		
		// Use most urgent order for preparing proposal.
//		if(suitableorders.length > 0)
		if(order!=null)
		{
//			Order order = suitableorders[0];
			
			double time_span = order.getDeadline().getTime() - order.getStartTime();
			double elapsed_time = getTime() - order.getStartTime();
			double price_span = order.getLimit() - order.getStartPrice();
			int acceptable_price =  (int)(price_span * elapsed_time / time_span) + order.getStartPrice();
			getLogger().info(getComponentName()+" proposed: " + acceptable_price);
			
			// Store proposal data in plan parameters.
			getParameter("proposal").setValue(Integer.valueOf(acceptable_price));
			getParameter("proposal_info").setValue(order);
			
			String report = "Made proposal: "+acceptable_price;
			NegotiationReport nr = new NegotiationReport(order, report, getTime());
			getBeliefbase().getBeliefSet("negotiation_reports").addFact(nr);
		}
	}
}
