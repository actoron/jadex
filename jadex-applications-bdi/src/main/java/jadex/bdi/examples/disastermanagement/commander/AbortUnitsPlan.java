package jadex.bdi.examples.disastermanagement.commander;

import jadex.bdi.examples.disastermanagement.IClearChemicalsService;
import jadex.bdi.examples.disastermanagement.IExtinguishFireService;
import jadex.bdi.examples.disastermanagement.ITreatVictimsService;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

/**
 *  Called to abort all units, when a handle_disaster goal
 *  becomes inactive.
 */
public class AbortUnitsPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		IGoal	goal	= (IGoal)getParameter("goal").getValue();
		IExtinguishFireService[]	fireunits	= (IExtinguishFireService[])goal.getParameterSet("fireunits").getValues();
		IClearChemicalsService[]	chemicalunits	= (IClearChemicalsService[])goal.getParameterSet("chemicalunits").getValues();
		ITreatVictimsService[]	ambulanceunits	= (ITreatVictimsService[])goal.getParameterSet("ambulanceunits").getValues();
		
//		System.out.println("Units: "+SUtil.arrayToString(fireunits)+", "+SUtil.arrayToString(chemicalunits)+", "+SUtil.arrayToString(ambulanceunits));
		for(int i=0; i<fireunits.length; i++)
			fireunits[i].abort();
		for(int i=0; i<chemicalunits.length; i++)
			chemicalunits[i].abort();
		for(int i=0; i<ambulanceunits.length; i++)
			ambulanceunits[i].abort();
	}
}
