package jadex.bdi.dfagent;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.DFRegister;
import jadex.bridge.fipa.Done;
import jadex.bridge.service.types.df.IDFComponentDescription;

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