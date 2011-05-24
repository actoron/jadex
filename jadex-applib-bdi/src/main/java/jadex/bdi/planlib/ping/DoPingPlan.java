package jadex.bdi.planlib.ping;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

/**
 *  Send a ping and wait for the reply.
 */
public class DoPingPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instantiated plan instance from the scheduler.
	 */
	public void body()
	{
//		long timeout;
//		if(getParameter("timeout").getValue()!=null)
//			timeout	= ((Long)getParameter("timeout").getValue()).longValue();
//		else
//			timeout	= -1;
		
		// Send ping and wait for answer.
		IGoal query = createGoal("procap.qp_initiate");
		query.getParameter("receiver").setValue(getParameter("receiver").getValue());
		query.getParameter("timeout").setValue(getParameter("timeout").getValue());
		query.getParameter("action").setValue(getParameter("content").getValue());
		dispatchSubgoalAndWait(query);
	}
}
