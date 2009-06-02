package jadex.bdi.examples.cleanerworld_env.cleaner;

import jadex.bdi.examples.cleanerworld.Cleaner;
import jadex.bdi.examples.cleanerworld.IEnvironment;
import jadex.bdi.examples.cleanerworld.Location;
import jadex.bdi.examples.cleanerworld.Vision;
import jadex.bdi.examples.cleanerworld.Waste;
import jadex.bdi.runtime.Plan;


/**
 *  Pick up a piece of waste in the environment.
 */
public class LocalGetVisionActionPlan extends	Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		IEnvironment	environment	= (IEnvironment)getBeliefbase().getBelief("environment").getFact();
		Cleaner cl = new Cleaner((Location)getBeliefbase().getBelief("my_location").getFact(),
			getAgentName(),
			(Waste)getBeliefbase().getBelief("carriedwaste").getFact(),
			((Number)getBeliefbase().getBelief("my_vision").getFact()).doubleValue(),
			((Number)getBeliefbase().getBelief("my_chargestate").getFact()).doubleValue());

		Vision	vision	= (Vision)environment.getVision(cl).clone();
//		Vision	vision	= (Vision)environment.getVision(cl);

		getParameter("vision").setValue(vision);
	}
}
