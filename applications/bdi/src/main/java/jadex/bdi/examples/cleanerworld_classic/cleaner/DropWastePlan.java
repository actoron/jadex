package jadex.bdi.examples.cleanerworld_classic.cleaner;

import jadex.bdi.examples.cleanerworld_classic.Location;
import jadex.bdi.examples.cleanerworld_classic.Waste;
import jadex.bdi.examples.cleanerworld_classic.Wastebin;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.bdiv3x.runtime.IBeliefSet;
import jadex.bdiv3x.runtime.Plan;


/**
 *  Clean-up some waste.
 */
public class DropWastePlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		Waste waste = (Waste)getBeliefbase().getBelief("carriedwaste").getFact();
//		System.out.println("carriedwaste a ="+waste);
//		if(waste==null)
//			System.out.println("here");
		
		// Move to a not full waste-bin
		Wastebin wastebin = (Wastebin)getParameter("wastebin").getValue();
		if(wastebin==null)
			throw new PlanFailureException();

		Location location = wastebin.getLocation();
		IGoal moveto = createGoal("achievemoveto");
		moveto.getParameter("location").setValue(location);
//		System.out.println("Created: "+location+" "+this);
		dispatchSubgoalAndWait(moveto);
//		System.out.println("Reached: "+location+" "+this);		

		// Drop waste to waste-bin.
		//IEnvironment env = (IEnvironment)getBeliefbase().getBelief("environment").getFact();
		//boolean success = env.dropWasteInWastebin(waste, wastebin);
		IGoal dg = createGoal("drop_waste_action");
		dg.getParameter("waste").setValue(waste);
		dg.getParameter("wastebin").setValue(wastebin);
		dispatchSubgoalAndWait(dg);

		// Update beliefs.
//		getLogger().info("Dropping waste to wastebin!");
		wastebin.addWaste(waste);

		// Todo: Find out why atomic is needed.
		startAtomic();
		IBeliefSet wbs = getBeliefbase().getBeliefSet("wastebins");
		if(wbs.containsFact(wastebin))
		{
			((Wastebin)wbs.getFact(wastebin)).update(wastebin);
//			wbs.updateFact(wastebin);
		}
		else
		{
			wbs.addFact(wastebin);
		}
		//getBeliefbase().getBeliefSet("wastebins").updateOrAddFact(wastebin);
		getBeliefbase().getBelief("carriedwaste").setFact(null);
//		System.out.println("carriedwaste b =null");
		endAtomic();
	}
}
