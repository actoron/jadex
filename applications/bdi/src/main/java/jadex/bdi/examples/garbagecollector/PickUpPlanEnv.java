package jadex.bdi.examples.garbagecollector;

import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3x.runtime.Plan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;

/**
 *  Try to pickup some piece of garbage.
 */
public class PickUpPlanEnv extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
//		System.out.println("Pickup plan: "+getAgentName()+" "+getReason());
		
		IEnvironmentSpace env = (IEnvironmentSpace)getBeliefbase().getBelief("env").getFact();
		// todo: garbage as parameter?
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(ISpaceAction.ACTOR_ID, getComponentDescription());
		Future<Boolean> fut = new Future<Boolean>();
		env.performSpaceAction("pickup", params, new DelegationResultListener<Boolean>(fut));
		Boolean res = fut.get();
		if(!res.booleanValue()) 
			fail();
		
//		System.out.println("pickup plan end");
	}
}
