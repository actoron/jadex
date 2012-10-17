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
public class NewTmpAddTargetPlan extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public NewTmpAddTargetPlan()
	{
		getLogger().info("Created: "+this+" "+getLogger().getName());
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{		
		String targetID = (String) this.getBeliefbase().getBelief("latest_target").getFact();
		IEnvironmentSpace env = (IEnvironmentSpace)getBeliefbase().getBelief("move.environment").getFact();
		ISpaceObject target =  env.getSpaceObject( Long.valueOf(targetID)); 
		
		
		

		if(target!=null&& !getBeliefbase().getBeliefSet("my_targets").containsFact(target))
		{
			System.out.println("#Sentry-NewAddTargetPlan# Found a new target: "+target);
			getBeliefbase().getBeliefSet("my_targets").addFact(target);			
		}
	}
}
