package jadex.bdi.examples.cleanerworld_classic.cleaner;

import jadex.bdi.examples.cleanerworld_classic.Location;
import jadex.bdi.examples.cleanerworld_classic.Waste;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;


/**
 *  Clean-up some waste.
 */
public class PickUpWastePlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		Waste waste = (Waste)getParameter("waste").getValue();

		// Move to the waste position when necessary
//		getLogger().info("Moving to waste!");
		IGoal moveto = createGoal("achievemoveto");
		Location location = waste.getLocation();
		moveto.getParameter("location").setValue(location);
//		System.out.println("Created: "+location+" "+this);
		dispatchSubgoalAndWait(moveto);
//		System.out.println("Reached: "+location+" "+this);

		//----- new -----
		IGoal dg = createGoal("pickup_waste_action");
		dg.getParameter("waste").setValue(waste);
		dispatchSubgoalAndWait(dg);
		getBeliefbase().getBelief("carriedwaste").setFact(waste);
		getBeliefbase().getBeliefSet("wastes").removeFact(waste);
//		getLogger().info("Picked up-waste!");

		//----- old -----
//		// Needed???
//		if(!getBeliefbase().getBeliefSet("wastes").containsFact(waste))
//			throw new PlanFailureException();
//
//		// Hack to block that other achieve goals can't be created
//		// while requesting to pick up.
//		getBeliefbase().getBelief("carriedwaste").setFact(waste);
//
//		//IEnvironment env = (IEnvironment)getBeliefbase().getBelief("environment").getFact();
//		//boolean success = env.pickUpWaste(waste);
//
//		IGoal dg = createGoal("pickup_waste_action");
//		dg.getParameter("waste").setValue(waste);
//		dispatchSubgoalAndWait(dg);
//
//		if(dg.isSucceeded())
//		{
//			getLogger().info("Picking up-waste!");
//			getBeliefbase().getBeliefSet("wastes").removeFact(waste);
//		}
//		else
//		{
//			// Remove the waste from my beliefs to avoid
//			// creating a new clean up goal for it.
//			getBeliefbase().getBeliefSet("wastes").removeFact(waste);
//			getBeliefbase().getBelief("carriedwaste").setFact(null);
//			getLogger().warning("Failed to pick up waste: "+waste);
//			getLogger().warning("wastes: "+jadex.commons.SUtil.arrayToString(getBeliefbase()
//				.getBeliefSet("wastes").getFacts()));
//			throw new PlanFailureException();
//		}
	}
}
