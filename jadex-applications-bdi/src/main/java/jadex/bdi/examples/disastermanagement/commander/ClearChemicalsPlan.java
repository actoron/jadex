package jadex.bdi.examples.disastermanagement.commander;

import jadex.bdi.examples.disastermanagement.IClearChemicalsService;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 * 
 */
public class ClearChemicalsPlan extends Plan
{
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public void	body()
	{
		ISpaceObject disaster = (ISpaceObject)getParameter("disaster").getValue();
		IClearChemicalsService force = (IClearChemicalsService)getParameter("rescueforce").getValue();
		force.clearChemicals(disaster).get(this);
	}
	
	/**
	 *  Called when the plan is aborted.
	 */
	public void aborted()
	{
		IClearChemicalsService force = (IClearChemicalsService)getParameter("rescueforce").getValue();
		force.abort().get(this);
	}
}
