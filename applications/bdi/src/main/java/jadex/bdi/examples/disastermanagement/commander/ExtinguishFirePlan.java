package jadex.bdi.examples.disastermanagement.commander;

import jadex.bdi.examples.disastermanagement.IExtinguishFireService;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.service.annotation.Timeout;
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
//		System.out.println("Calling forces to handle fire: " + disaster);
		ef	= force.extinguishFire(disaster.getId());
		ef.get(Timeout.NONE);	// hack??? extinguishing fire may take longer than default timeout
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
