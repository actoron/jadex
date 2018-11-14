package jadex.bdi.examples.cleanerworld.cleaner;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;
import jadex.extension.envsupport.math.IVector2;


/**
 *  Patrol along the patrol points.
 */
public class PatrolPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		IVector2[] loci = (IVector2[])getBeliefbase().getBeliefSet("patrolpoints").getFacts();

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
