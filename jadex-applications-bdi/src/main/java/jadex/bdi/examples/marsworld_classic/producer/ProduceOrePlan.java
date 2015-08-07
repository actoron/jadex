package jadex.bdi.examples.marsworld_classic.producer;

import jadex.bdi.examples.marsworld_classic.Target;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Production of Ore is done by increasing the amount.
 */
public class ProduceOrePlan extends Plan
{
	//-------- methods --------

	/**
	 *  The Amount of Ore at the current location is increased.
	 */
	public void body()
	{
		getLogger().info("Created: "+this);
		
		// Get the target first.
		Target target = (Target)getParameter("target").getValue();

		IGoal go_dest = createGoal("move.move_dest");
		go_dest.getParameter("destination").setValue(target.getLocation());
		dispatchSubgoalAndWait(go_dest);

		int max = target.getOreCapacity();
		for(int i = 0; i<max; i++)
		{
			target.produceOre(1);
			waitFor(100);
		}
	}
}