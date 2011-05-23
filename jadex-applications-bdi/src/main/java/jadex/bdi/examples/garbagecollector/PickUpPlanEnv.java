package jadex.bdi.examples.garbagecollector;

import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceAction;
import jadex.bdi.runtime.Plan;

import java.util.HashMap;
import java.util.Map;

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
		
		Map params = new HashMap();
		params.put(ISpaceAction.ACTOR_ID, getComponentDescription());
		SyncResultListener srl	= new SyncResultListener();
		env.performSpaceAction("pickup", params, srl); // todo: garbage as parameter?
		if(!((Boolean)srl.waitForResult()).booleanValue()) 
			fail();
		
//		System.out.println("pickup plan end");
	}
}
