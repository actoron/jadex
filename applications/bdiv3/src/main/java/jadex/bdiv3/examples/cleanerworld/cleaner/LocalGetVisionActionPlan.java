package jadex.bdiv3.examples.cleanerworld.cleaner;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.examples.cleanerworld.world.Cleaner;
import jadex.bdiv3.examples.cleanerworld.world.IEnvironment;
import jadex.bdiv3.examples.cleanerworld.world.Vision;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.impl.RProcessableElement;



/**
 *  Pick up a piece of waste in the environment.
 */
@Plan
public class LocalGetVisionActionPlan
{
	@PlanCapability
	protected CleanerAgent capa;
	
	@PlanAPI
	protected IPlan rplan;

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	@PlanBody
	public void body()
	{
		IEnvironment environment = capa.getEnvironment();
		Cleaner cl = new Cleaner(capa.getMyLocation(),
			capa.getAgent().getId().getLocalName(), capa.getCarriedWaste(),
			capa.getMyVision(), capa.getMyChargestate());

		Vision	vision	= (Vision)environment.getVision(cl).clone();

		// hack
		CleanerAgent.GetVisionAction gva = (CleanerAgent.GetVisionAction)((RProcessableElement)rplan.getReason()).getPojoElement();
		gva.setVision(vision);
//		getParameter("vision").setValue(vision);
	}
}
