package jadex.bdi.examples.hunterprey_env.dumbprey;

import jadex.adapter.base.envsupport.environment.IAgentAction;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.bdi.examples.hunterprey_env.MoveAction;
import jadex.bdi.runtime.Plan;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *  The behavior of the dumb prey.
 */
public class DumbPreyPlan extends Plan
{
	/**
	 *  Plan body.
	 */
	public void body()
	{
		Grid2D	env	= (Grid2D)getBeliefbase().getBelief("env").getFact();
		ISpaceObject	myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
//		IVector1	range	= new Vector1Int(3);	// Todo: vision range should be managed by environment.
		String	lastdir	= null;
		
		while(true)
		{
			System.out.println("new round for: "+getAgentName());
			
			// Get current vision.
			IVector2	pos	= (IVector2)myself.getProperty(Space2D.POSITION);
//			ISpaceObject[]	vision	= env.getNearObjects(pos, range);
//			System.out.println("Vision: "+getAgentName()+", "+SUtil.arrayToString(vision));
			
			Collection	foods	= env.getSpaceObjectsByGridPosition(pos, "food");
			if(foods!=null && !foods.isEmpty())
			{
				// Perform eat action.
				try
				{
					SyncResultListener srl	= new SyncResultListener();
					Map params = new HashMap();
					params.put(IAgentAction.ACTOR_ID, getAgentIdentifier());
					params.put(IAgentAction.OBJECT_ID, foods.iterator().next());
					env.performAgentAction("eat", params, srl);
					srl.waitForResult();
				}
				catch(RuntimeException e)
				{
//					System.out.println("Eat failed: "+e);
				}
			}

			else
			{
				// Turn 90° with probability 0.25, otherwise continue moving in same direction.
				if(lastdir==null || Math.random()>0.75)
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
					params.put(IAgentAction.ACTOR_ID, getAgentIdentifier());
					params.put(MoveAction.PARAMETER_DIRECTION, lastdir);
					env.performAgentAction("move", params, srl);
					srl.waitForResult();
				}
				catch(RuntimeException e)
				{
					// Move failed, turn 90°.
//					System.out.println("Move failed: "+e);
					if(MoveAction.DIRECTION_LEFT.equals(lastdir) || MoveAction.DIRECTION_RIGHT.equals(lastdir))
					{
						lastdir	= Math.random()>0.5 ? MoveAction.DIRECTION_UP : MoveAction.DIRECTION_DOWN;
					}
					else
					{
						lastdir	= Math.random()>0.5 ? MoveAction.DIRECTION_LEFT : MoveAction.DIRECTION_RIGHT;
					}
				}
			}
		}
	}
}
