package jadex.bdi.examples.hunterprey.dumbprey;

import jadex.bdi.examples.hunterprey.MoveAction;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

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
		String	lastdir	= null;
		
		while(true)
		{
//			System.out.println("nearest food for: "+getAgentName()+", "+getBeliefbase().getBelief("nearest_food").getFact());
			
			// Get current position.
			IVector2	pos	= (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);
			
			ISpaceObject	food	= (ISpaceObject)getBeliefbase().getBelief("nearest_food").getFact();
			if(food!=null && pos.equals(food.getProperty(Space2D.PROPERTY_POSITION)))
			{
				// Perform eat action.
				try
				{
					SyncResultListener srl	= new SyncResultListener();
					Map params = new HashMap();
					params.put(ISpaceAction.ACTOR_ID, getComponentDescription());
					params.put(ISpaceAction.OBJECT_ID, food);
					env.performSpaceAction("eat", params, srl);
					srl.waitForResult();
				}
				catch(RuntimeException e)
				{
//					System.out.println("Eat failed: "+e);
				}
			}

			else
			{
				// Move towards the food, if any
				if(food!=null)
				{
					String	newdir	= MoveAction.getDirection(env, pos, (IVector2)food.getProperty(Space2D.PROPERTY_POSITION));
					if(!MoveAction.DIRECTION_NONE.equals(newdir))
					{
						lastdir	= newdir;
					}
					else
					{
						// Food unreachable.
						getBeliefbase().getBelief("nearest_food").setFact(null);						
					}
				}
				
				// When no food, turn 90 degrees with probability 0.25, otherwise continue moving in same direction.
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
					SyncResultListener srl	= new SyncResultListener();
					Map params = new HashMap();
					params.put(ISpaceAction.ACTOR_ID, getComponentDescription());
					params.put(MoveAction.PARAMETER_DIRECTION, lastdir);
					env.performSpaceAction("move", params, srl);
					srl.waitForResult();
				}
				catch(RuntimeException e)
				{
					// Move failed, forget about food and turn 90 degrees.
					getBeliefbase().getBelief("nearest_food").setFact(null);
					
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
