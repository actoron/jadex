package jadex.bdi.examples.spaceworld3d.sentry;

import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 *  Add a new unknown target to test.
 */
public class AddTargetPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		getLogger().info("Created: "+this+" "+getLogger().getName());
		
		//System.out.println("AddPlan found");
		IEnvironmentSpace env = (IEnvironmentSpace)getBeliefbase().getBelief("move.environment").getFact();
		IMessageEvent req = (IMessageEvent)getReason();

		ISpaceObject ot = (ISpaceObject)req.getParameter(SFipa.CONTENT).getValue();
		ISpaceObject target = env.getSpaceObject(ot.getId());

		//if(ts.length>0)
		//	System.out.println("Sees: "+SUtil.arrayToString(ts));

		if(target!=null&& !getBeliefbase().getBeliefSet("my_targets").containsFact(target))
		{
			//System.out.println("Found a new target: "+target);
			getBeliefbase().getBeliefSet("my_targets").addFact(target);
		}
	}
}
