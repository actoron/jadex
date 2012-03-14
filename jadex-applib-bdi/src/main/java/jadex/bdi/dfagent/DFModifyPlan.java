package jadex.bdi.dfagent;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.fipa.DFModify;
import jadex.bridge.fipa.Done;
import jadex.bridge.service.types.df.IDFComponentDescription;

/**
 *  The df modify plan has the task to receive a message
 *  andc reate a corresponding goal.
 */
public class DFModifyPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		DFModify mo = (DFModify)getParameter("action").getValue();

		IGoal reg = createGoal("df_modify");
		reg.getParameter("description").setValue(mo.getComponentDescription());
		dispatchSubgoalAndWait(reg);

		mo.setResult((IDFComponentDescription)reg.getParameter("result").getValue());
		getParameter("result").setValue(new Done(mo));
	}
}
