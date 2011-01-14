package jadex.bdi.examples.disastermanagement.commander;

import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.bdi.examples.disastermanagement.IClearChemicalsService;
import jadex.bdi.examples.disastermanagement.IExtinguishFireService;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.Plan;
import jadex.commons.concurrent.IResultListener;

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
}
