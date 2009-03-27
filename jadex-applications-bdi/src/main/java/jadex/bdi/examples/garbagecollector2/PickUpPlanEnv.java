package jadex.bdi.examples.garbagecollector2;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

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
		//System.out.println("Calling pickup: "+getAgentName()+" "+getRootGoal());
//		Environment env = (Environment)getBeliefbase().getBelief("env").getFact();
//		if(!env.pickup(getAgentName()))
//			fail();
		
		IEnvironmentSpace env = (IEnvironmentSpace)getBeliefbase().getBelief("env").getFact();
		// todo: garbage as parameter?
		if(!((Boolean)env.performAction("pickup", null)).booleanValue()) 
			fail();
	}
}
