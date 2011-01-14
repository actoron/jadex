package jadex.bdi.examples.disastermanagement.commander;

import jadex.bdi.examples.disastermanagement.IClearChemicalsService;
import jadex.bdi.examples.disastermanagement.IExtinguishFireService;
import jadex.bdi.examples.disastermanagement.ITreatVictimsService;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.commons.SUtil;

/**
 * 
 */
public class FreeUnitsPlan extends Plan
{
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public void	body()
	{	
		if(!((IGoal)getReason()).isSucceeded())
		{
			Object[] units = getParameterSet("units").getValues();
			System.out.println("Freeing units: "+SUtil.arrayToString(units));
			for(int i=0; i<units.length; i++)
			{
				if(units[i] instanceof IExtinguishFireService)
					((IExtinguishFireService)units[i]).abort();
				else if(units[i] instanceof ITreatVictimsService)
					((ITreatVictimsService)units[i]).abort();
				else if(units[i] instanceof IClearChemicalsService)
					((IClearChemicalsService)units[i]).abort();
			}
		}
	}
}
