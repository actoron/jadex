package jadex.bdi.examples.cleanerworld_classic.cleaner;

import jadex.bdi.examples.cleanerworld_classic.IEnvironment;
import jadex.bdi.examples.cleanerworld_classic.Waste;
import jadex.bdiv3x.runtime.Plan;
import jadex.bdiv3x.runtime.PlanFailureException;


/**
 *  Pick up a piece of waste in the environment.
 */
public class LocalPickUpWasteActionPlan extends	Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		IEnvironment	environment	= (IEnvironment)getBeliefbase().getBelief("environment").getFact();
		Waste waste = (Waste)getParameter("waste").getValue();

		boolean	success	= environment.pickUpWaste(waste);

		if(!success)
			throw new PlanFailureException();
	}
}
