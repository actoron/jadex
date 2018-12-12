package jadex.bdi.planlib.ping;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;

/**
 *  The pinging plan continously sends ping messages
 *  to another agent on the same platform.
 */
public class PingingPlan	extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
//		while(true) // Not possible because plan parameters are only written back when plan finishes (missed_cnt).
//		{
			// Send ping and wait for answer.
			IGoal ping = createGoal("ping");
			ping.getParameter("content").setValue(getParameter("content").getValue());
			ping.getParameter("receiver").setValue(getParameter("receiver").getValue());
			ping.getParameter("timeout").setValue(getParameter("timeout").getValue());
			
			try
			{
				dispatchSubgoalAndWait(ping);
				getParameter("missed_cnt").setValue(Integer.valueOf(0)); // Reset missed cnt.
			}
			catch(Exception e)
			{
				int cnt = ((Integer)getParameter("missed_cnt").getValue()).intValue();
				getParameter("missed_cnt").setValue(Integer.valueOf(cnt+1)); // Raise cnt.
//				System.out.println("Missed ping: "+cnt);
			}
			
			// When agent answered, wait before sending next ping.
			long sleep = ((Long)getParameter("ping_delay").getValue()).longValue();
			waitFor(sleep);
//		}
	}
}
