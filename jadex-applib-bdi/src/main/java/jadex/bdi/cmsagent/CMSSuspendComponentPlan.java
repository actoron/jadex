package jadex.bdi.cmsagent;

import jadex.base.fipa.CMSSuspendComponent;
import jadex.base.fipa.Done;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

/**
 *  Suspend a component.
 */
public class CMSSuspendComponentPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		CMSSuspendComponent sa = (CMSSuspendComponent)getParameter("action").getValue();

		IGoal sag = createGoal("cms_suspend_component");
		sag.getParameter("componentidentifier").setValue(sa.getComponentIdentifier());
		dispatchSubgoalAndWait(sag);

		getParameter("result").setValue(new Done(sa));
	}
}
