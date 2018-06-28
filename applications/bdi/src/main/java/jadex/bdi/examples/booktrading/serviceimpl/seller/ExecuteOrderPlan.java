package jadex.bdi.examples.booktrading.serviceimpl.seller;

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
		
			// Extract order data.
			Integer price = (Integer)getParameter("proposal").getValue();
			
			if(price.intValue()>=acceptable_price)
			{
			
//				getLogger().info("Execute order plan: "+price+" "+order);
	
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
			}
			else
			{
				fail();
			}
		}
	}
}
