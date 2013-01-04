package jadex.bdiv3.example.cleanerworld.plans;



/**
 *  Patrol along the patrol points.
 */
public class PatrolPlan
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
//		Location[] loci = (Location[])getBeliefbase().getBeliefSet("patrolpoints").getFacts();
//
//		for(int i=0; i<loci.length; i++)
//		{
//			IGoal moveto = createGoal("achievemoveto");
//			moveto.getParameter("location").setValue(loci[i]);
////			System.out.println("Created: "+loci[i]+" "+this);
//			dispatchSubgoalAndWait(moveto);
////			System.out.println("Reached: "+loci[i]+" "+this);
//		}
	}
}
