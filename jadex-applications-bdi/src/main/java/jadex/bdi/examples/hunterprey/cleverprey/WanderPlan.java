package jadex.bdi.examples.hunterprey.cleverprey;

import jadex.bdi.examples.hunterprey.MoveAction;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.space2d.Grid2D;

import java.util.HashMap;
import java.util.Map;

/**
 *  A plan to explore the map.
 */
public class WanderPlan extends Plan
{
	/**
	 *  Plan body.
	 */
	public void body()
	{
		Grid2D	env	= (Grid2D)getBeliefbase().getBelief("env").getFact();
		String	lastdir	= null;
		boolean	failed	= false;
		
		while(true)
		{
			// Turn 90 degrees with probability 0.25, otherwise continue moving in same direction.
			if(lastdir==null || failed || Math.random()>0.75)
			{
				if(MoveAction.DIRECTION_LEFT.equals(lastdir) || MoveAction.DIRECTION_RIGHT.equals(lastdir))
				{
					lastdir	= Math.random()>0.5 ? MoveAction.DIRECTION_UP : MoveAction.DIRECTION_DOWN;
				}
				else
				{
					lastdir	= Math.random()>0.5 ? MoveAction.DIRECTION_LEFT : MoveAction.DIRECTION_RIGHT;
				}
			}
				
			// Perform move action.
			try
			{
				SyncResultListener srl	= new SyncResultListener();
				Map params = new HashMap();
				params.put(ISpaceAction.ACTOR_ID, getComponentDescription());
				params.put(MoveAction.PARAMETER_DIRECTION, lastdir);
				env.performSpaceAction("move", params, srl);
				srl.waitForResult();
//				System.out.println("Moved (wander): "+lastdir+", "+getAgentName());
				failed	= false;
			}
			catch(RuntimeException e)
			{
//				System.err.println("Wander plan failed: "+e);
				// Move failed, turn 90 degrees on next move.
				failed	= true;
			}
		}
	}
}
