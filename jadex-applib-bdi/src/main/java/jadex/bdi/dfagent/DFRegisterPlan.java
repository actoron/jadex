package jadex.bdi.dfagent;

import jadex.adapter.base.fipa.DFRegister;
import jadex.adapter.base.fipa.Done;
import jadex.adapter.base.fipa.IDFComponentDescription;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

/**
 *  The df register plan has the task to receive a message
 *  and create a corresponding goal.
 */
public class DFRegisterPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		DFRegister re = (DFRegister)getParameter("action").getValue();

		IGoal reg = createGoal("df_register");
		reg.getParameter("description").setValue(re.getComponentDescription());
		dispatchSubgoalAndWait(reg);

		re.setResult((IDFComponentDescription)reg.getParameter("result").getValue());
		getParameter("result").setValue(new Done(re));
	}
}