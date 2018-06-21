package jadex.bdi.examples.hunterprey.dumbhunter;

import java.util.HashMap;
import java.util.Map;

import jadex.bdi.examples.hunterprey.MoveAction;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

/**
 *  The behavior of the dumb hunter.
 */
public class DumbHunterPlan extends Plan
{
	/**
	 *  Plan body.
	 */
	public void body()
	{
		Grid2D	env	= (Grid2D)getBeliefbase().getBelief("env").getFact();
		ISpaceObject	myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
		String	lastdir	= null;
		
		while(true)
		{
//			System.out.println("nearest prey for: "+getAgentName()+", "+getBeliefbase().getBelief("nearest_prey").getFact());
			
			// Get current position.
			IVector2	pos	= (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);
			
			ISpaceObject	prey	= (ISpaceObject)getBeliefbase().getBelief("nearest_prey").getFact();
			if(prey!=null && pos.equals(prey.getProperty(Space2D.PROPERTY_POSITION)))
			{
				// Perform eat action.
				try
				{
					Map<String, Object> params = new HashMap<String, Object>();
					params.put(ISpaceAction.ACTOR_ID, getComponentDescription());
					params.put(ISpaceAction.OBJECT_ID, prey);
					Future<Void> fut = new Future<Void>();
					env.performSpaceAction("eat", params, new DelegationResultListener<Void>(fut));
					fut.get();
				}
				catch(RuntimeException e)
				{
//					System.out.println("Eat failed: "+e);
				}
			}

			else
			{
				// Move towards the prey, if any
				if(prey!=null)
				{
					String	newdir	= MoveAction.getDirection(env, pos, (IVector2)prey.getProperty(Space2D.PROPERTY_POSITION));
					if(!MoveAction.DIRECTION_NONE.equals(newdir))
					{
						lastdir	= newdir;
					}
					else
					{
						// Prey unreachable.
						getBeliefbase().getBelief("nearest_prey").setFact(null);						
					}
				}
				
				// When no prey, turn 90 degrees with probability 0.25, otherwise continue moving in same direction.
				else if(lastdir==null || Math.random()>0.75)
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
				}
				catch(RuntimeException e)
				{
					// Move failed, forget about prey and turn 90 degrees.
					getBeliefbase().getBelief("nearest_prey").setFact(null);
					
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
