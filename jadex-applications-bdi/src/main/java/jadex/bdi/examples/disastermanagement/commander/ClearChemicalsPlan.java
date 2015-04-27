package jadex.bdi.examples.disastermanagement.commander;

import jadex.bdi.examples.disastermanagement.IClearChemicalsService;
import jadex.bdi.runtime.Plan;
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
		cc.get();
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
