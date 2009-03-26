package jadex.bdi.examples.garbagecollector;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

/**
 *  Burn a piece of garbage.
 */
public class BurnPlanEnv extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
//		System.out.println("Burn plan activated!");
		
		IEnvironmentSpace env = (IEnvironmentSpace)getBeliefbase().getBelief("env").getFact();

		// Pickup the garbarge.
		IGoal pickup = createGoal("pick");
		dispatchSubgoalAndWait(pickup);

		// Burn the waste.
		waitFor(100);
		
		env.performAction("burn", null); // todo: garbage as parameter?
		
//		env.burn(getAgentName());
	}
}
