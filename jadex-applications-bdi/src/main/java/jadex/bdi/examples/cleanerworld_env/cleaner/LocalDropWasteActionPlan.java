package jadex.bdi.examples.cleanerworld_env.cleaner;

import jadex.bdi.examples.cleanerworld.IEnvironment;
import jadex.bdi.examples.cleanerworld.Waste;
import jadex.bdi.examples.cleanerworld.Wastebin;
import jadex.bdi.runtime.Plan;
import jadex.bdi.runtime.PlanFailureException;


/**
 *  Pick up a piece of waste in the environment.
 */
public class LocalDropWasteActionPlan extends	Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		IEnvironment	environment	= (IEnvironment)getBeliefbase().getBelief("environment").getFact();
		Waste waste = (Waste)getParameter("waste").getValue();
		Wastebin wastebin = (Wastebin)getParameter("wastebin").getValue();

		boolean	success	= environment.dropWasteInWastebin(waste, wastebin);

		if(!success)
			throw new PlanFailureException();
	}
}
