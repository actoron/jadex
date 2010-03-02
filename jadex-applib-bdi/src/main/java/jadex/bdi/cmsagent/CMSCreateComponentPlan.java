package jadex.bdi.cmsagent;

import jadex.base.fipa.CMSCreateComponent;
import jadex.base.fipa.Done;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;

/**
 *  Create an component.
 */
public class CMSCreateComponentPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{		
		CMSCreateComponent ca = (CMSCreateComponent)getParameter("action").getValue();

		IGoal cag = createGoal("cms_create_component");
		cag.getParameter("name").setValue(ca.getName());
		cag.getParameter("type").setValue(ca.getType());
		cag.getParameter("configuration").setValue(ca.getConfiguration());
		cag.getParameter("arguments").setValue(ca.getArguments());
		cag.getParameter("parent").setValue(ca.getParent());
		dispatchSubgoalAndWait(cag);

		ca.setComponentIdentifier((IComponentIdentifier)cag.getParameter("componentidentifier").getValue());
		getParameter("result").setValue(new Done(ca));
	}
}
