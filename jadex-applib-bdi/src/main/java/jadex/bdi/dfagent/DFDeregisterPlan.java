package jadex.bdi.dfagent;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.DFDeregister;
import jadex.bridge.fipa.Done;

/**
 *  The df deregister plan has the task to receive a message 
 *  and create a corresponding goal.
 */
public class DFDeregisterPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		DFDeregister de = (DFDeregister)getParameter("action").getValue();

		IGoal dreg = createGoal("df_deregister");
		dreg.getParameter("description").setValue(de.getComponentDescription());
		dispatchSubgoalAndWait(dreg);

		getParameter("result").setValue(new Done(de));
	}
}
