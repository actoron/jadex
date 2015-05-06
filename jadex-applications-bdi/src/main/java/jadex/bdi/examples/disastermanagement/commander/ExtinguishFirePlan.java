package jadex.bdi.examples.disastermanagement.commander;

import jadex.bdi.examples.disastermanagement.IExtinguishFireService;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.future.ITerminableFuture;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 * 
 */
public class ExtinguishFirePlan extends Plan
{
	protected ITerminableFuture<Void>	ef;
	
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public void	body()
	{
		ISpaceObject disaster = (ISpaceObject)getParameter("disaster").getValue();
		IExtinguishFireService force = (IExtinguishFireService)getParameter("rescueforce").getValue();
		ef	= force.extinguishFire(disaster);
		ef.get();
	}
	
	/**
	 *  Called when the plan is aborted.
	 */
	public void aborted()
	{
		if(ef!=null)
		{
			ef.terminate();
		}
	}
}
