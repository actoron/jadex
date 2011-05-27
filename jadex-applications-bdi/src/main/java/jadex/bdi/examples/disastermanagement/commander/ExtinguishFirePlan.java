package jadex.bdi.examples.disastermanagement.commander;

import jadex.bdi.examples.disastermanagement.IExtinguishFireService;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 * 
 */
public class ExtinguishFirePlan extends Plan
{
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public void	body()
	{
		ISpaceObject disaster = (ISpaceObject)getParameter("disaster").getValue();
		IExtinguishFireService force = (IExtinguishFireService)getParameter("rescueforce").getValue();
		force.extinguishFire(disaster).get(this);
	}
	
	/**
	 *  Called when the plan is aborted.
	 */
	public void aborted()
	{
		IExtinguishFireService force = (IExtinguishFireService)getParameter("rescueforce").getValue();
		force.abort().get(this);
	}
}
