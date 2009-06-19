package jadex.bdi.examples.marsworld_classic.producer;

import jadex.bdi.examples.marsworld_classic.Target;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

/**
 *  Production of Ore is done by increasing the amount.
 */
public class ProduceOrePlan extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public ProduceOrePlan()
	{
		getLogger().info("Created: "+this);
	}

	//-------- methods --------

	/**
	 *  The Amount of Ore at the current location is increased.
	 */
	public void body()
	{
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