package jadex.bdi.examples.booktrading.seller;

import java.util.Date;

import jadex.bdi.examples.booktrading.common.NegotiationReport;
import jadex.bdi.examples.booktrading.common.Order;
import jadex.bdiv3x.runtime.Plan;

/**
 * Execute the order by setting execution price and date.
 */
public class ExecuteOrderPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{		
		// Extract order data.
		Integer price = (Integer)getParameter("proposal").getValue();
		Order order = (Order)getParameter("proposal_info").getValue();
		
//		getLogger().info("Execute order plan: "+price+" "+order);

		// Initiate payment and delivery.
		// IGoal pay = createGoal("payment");
		// pay.getParameter("order").setValue(order);
		// dispatchSubgoalAndWait(pay);
		// IGoal delivery = createGoal("delivery");
		// delivery.getParameter("order").setValue(order);
		// dispatchSubgoalAndWait(delivery);
	
		// Save successful transaction data.
		order.setState(Order.DONE);
		order.setExecutionPrice(price);
		order.setExecutionDate(new Date(getTime()));
		
		String report = "Sold for: "+price;
		NegotiationReport nr = new NegotiationReport(order, report, getTime());
		getBeliefbase().getBeliefSet("negotiation_reports").addFact(nr);

		getParameter("result").setValue(price);
	}
}
