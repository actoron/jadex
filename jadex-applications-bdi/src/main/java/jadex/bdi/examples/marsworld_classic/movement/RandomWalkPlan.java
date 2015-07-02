package jadex.bdi.examples.marsworld_classic.movement;

import jadex.bdi.examples.marsworld_classic.Location;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Wander around randomly.
 */
public class RandomWalkPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		double x_dest = Math.random();
		double y_dest = Math.random();
		Location dest = new Location(x_dest, y_dest);
		IGoal moveto = createGoal("move_dest");
		moveto.getParameter("destination").setValue(dest);
		dispatchSubgoalAndWait(moveto);
		getLogger().info("Reached point: "+dest);
	}
}
