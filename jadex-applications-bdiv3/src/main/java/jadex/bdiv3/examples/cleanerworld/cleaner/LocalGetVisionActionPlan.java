package jadex.bdiv3.examples.cleanerworld.cleaner;

import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPlan;
import jadex.bdiv3.examples.cleanerworld.world.Cleaner;
import jadex.bdiv3.examples.cleanerworld.world.IEnvironment;
import jadex.bdiv3.examples.cleanerworld.world.Vision;
import jadex.bdiv3.runtime.RGoal;
import jadex.bdiv3.runtime.RPlan;



/**
 *  Pick up a piece of waste in the environment.
 */
public class LocalGetVisionActionPlan
{
	@PlanCapability
	protected CleanerBDI capa;
	
	@PlanPlan
	protected RPlan rplan;

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	@PlanBody
	public void body()
	{
		IEnvironment environment = capa.getEnvironment();
		Cleaner cl = new Cleaner(capa.getMyLocation(),
			capa.getAgent().getAgentName(), capa.getCarriedWaste(),
			capa.getMyVision(), capa.getMyChargestate());

		Vision	vision	= (Vision)environment.getVision(cl).clone();

		// hack
		CleanerBDI.GetVisionAction gva = (CleanerBDI.GetVisionAction)((RGoal)rplan.getReason()).getPojoElement();
		gva.setVision(vision);
//		getParameter("vision").setValue(vision);
	}
}
