package jadex.bdi.examples.disastermanagement.commander;

import jadex.bdi.examples.disastermanagement.IClearChemicalsService;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.ITerminableFuture;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 * 
 */
public class ClearChemicalsPlan extends Plan
{
	protected ITerminableFuture<Void>	cc;
	
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public void	body()
	{
		ISpaceObject disaster = (ISpaceObject)getParameter("disaster").getValue();
		IClearChemicalsService force = (IClearChemicalsService)getParameter("rescueforce").getValue();
		cc	= force.clearChemicals(disaster);
		cc.get(Timeout.NONE);	// hack??? clearing chemicals may take longer than default timeout
	}
	
	/**
	 *  Called when the plan is aborted.
	 */
	public void aborted()
	{
		if(cc!=null)
		{
			cc.terminate();
		}
	}
}
