package jadex.bdi.cmsagent;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.CMSDestroyComponent;
import jadex.bridge.fipa.Done;

/**
 *  Destroy a component.
 */
public class CMSDestroyComponentPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		CMSDestroyComponent da = (CMSDestroyComponent)getParameter("action").getValue();

		IGoal dag = createGoal("cms_destroy_component");
		dag.getParameter("componentidentifier").setValue(da.getComponentIdentifier());
		dispatchSubgoalAndWait(dag);

		getParameter("result").setValue(new Done(da));
	}
}
