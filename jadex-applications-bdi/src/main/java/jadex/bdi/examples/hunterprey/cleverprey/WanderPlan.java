package jadex.bdi.examples.hunterprey.cleverprey;

import java.util.HashMap;
import java.util.Map;

import jadex.bdi.examples.hunterprey.MoveAction;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.space2d.Grid2D;

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
				Map<String, Object> params = new HashMap<String, Object>();
				params.put(ISpaceAction.ACTOR_ID, getComponentDescription());
				params.put(MoveAction.PARAMETER_DIRECTION, lastdir);
				
				Future<Void> fut = new Future<Void>();
				env.performSpaceAction("move", params, new DelegationResultListener<Void>(fut));
				fut.get();
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
