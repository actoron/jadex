package jadex.bdi.examples.cleanerworld_classic.cleaner;

import jadex.bdi.examples.cleanerworld_classic.Location;
import jadex.bdi.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;


/**
 *  Patrol along the patrol points.
 */
public class PatrolPlan extends Plan
{

	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public PatrolPlan()
	{
//		getLogger().info("Created: "+this);
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		Location[] loci = (Location[])getBeliefbase().getBeliefSet("patrolpoints").getFacts();

		for(int i=0; i<loci.length; i++)
		{
			IGoal moveto = createGoal("achievemoveto");
			moveto.getParameter("location").setValue(loci[i]);
//			System.out.println("Created: "+loci[i]+" "+this);
			dispatchSubgoalAndWait(moveto);
//			System.out.println("Reached: "+loci[i]+" "+this);
		}
	}
}
