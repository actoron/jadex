package jadex.bdi.examples.garbagecollector2;

import java.util.HashMap;
import java.util.Map;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bdi.runtime.Plan.SyncResultListener;

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
		params.put(ISpaceObject.OWNER, getAgentIdentifier().getLocalName());
		SyncResultListener srl	= new SyncResultListener();
		env.performAgentAction("pickup", params, srl); // todo: garbage as parameter?
		if(!((Boolean)srl.waitForResult()).booleanValue()) 
			fail();
		
//		System.out.println("pickup plan end");
	}
}
