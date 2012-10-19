package sodekovs.marsworld.sentry;

import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.fipa.SFipa;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;

/**
 *  Add a new unknown target to test.
 */
public class AddTargetPlan extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public AddTargetPlan()
	{
		getLogger().info("Created: "+this+" "+getLogger().getName());
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{		
		ISpaceObject[] targets = (ISpaceObject[]) this.getBeliefbase().getBeliefSet("latest_target").getFacts();
		ISpaceObject latestTarget = targets[targets.length-1];
//		IEnvironmentSpace env = (IEnvironmentSpace)getBeliefbase().getBelief("move.environment").getFact();
//		ISpaceObject target =  env.getSpaceObject( Long.valueOf(targetID)); 
		
		
		if(latestTarget!=null&& !getBeliefbase().getBeliefSet("my_targets").containsFact(latestTarget))
		{
			System.out.println("#Sentry-NewAddTargetPlan# Found a new target: "+latestTarget);
			getBeliefbase().getBeliefSet("my_targets").addFact(latestTarget);			
		}		
	}
}
