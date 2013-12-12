package jadex.bdi.planlib.watchdog;

import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

/**
 *  Observe an agent via ping requests.
 */
public class ObserveAgentPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		ObservationDescription desc = (ObservationDescription)getParameter("description").getValue();
		if(desc==null)
			fail();
		
		while(true)
		{
			try
			{
				waitFor(desc.getPingDelay());
				IGoal pinging = createGoal("pinging");
				pinging.getParameter("receiver").setValue(desc.getComponentIdentifier());
				pinging.getParameter("ping_delay").setValue(Long.valueOf(desc.getPingDelay()));
				dispatchSubgoalAndWait(pinging);
			}
			catch(GoalFailureException e1)
			{
				// Observed agent did not answer.
				try
				{
					// Try to recover.
					IGoal recover = createGoal("recover_component");
					recover.getParameter("description").setValue(desc);
					dispatchSubgoalAndWait(recover);
				}
				catch(GoalFailureException e2)
				{
					// Recovering failed. Try to notify.
					IGoal notify = createGoal("notify_admin");
					notify.getParameter("description").setValue(desc);
					dispatchSubgoalAndWait(notify);
					fail();
				}
			}
		}
	}
}
