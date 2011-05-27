package jadex.bdi.examples.garbagecollector;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;

import java.util.HashMap;
import java.util.Map;

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

		SyncResultListener srl	= new SyncResultListener();
		Map params = new HashMap();
		params.put(ISpaceAction.ACTOR_ID, getComponentDescription());
		env.performSpaceAction("burn", params, srl);
		srl.waitForResult();
	}
}
