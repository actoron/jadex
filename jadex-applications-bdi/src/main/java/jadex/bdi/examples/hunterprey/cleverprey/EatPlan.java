package jadex.bdi.examples.hunterprey.cleverprey;

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
 *  A plan to explore the map.
 */
public class EatPlan extends Plan
{
	/**
	 *  Plan body.
	 */
	public void body()
	{
		Grid2D	env	= (Grid2D)getBeliefbase().getBelief("env").getFact();
		ISpaceObject	myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
		ISpaceObject	food	= (ISpaceObject)getParameter("food").getValue();

		try
		{
			// Move towards food until position reached.
			while(!myself.getProperty(Space2D.PROPERTY_POSITION).equals(food.getProperty(Space2D.PROPERTY_POSITION)))
			{
				String	move	= MoveAction.getDirection(env, (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION),
					(IVector2)food.getProperty(Space2D.PROPERTY_POSITION));
				if(MoveAction.DIRECTION_NONE.equals(move))
					fail();
				SyncResultListener srl	= new SyncResultListener();
				Map params = new HashMap();
				params.put(ISpaceAction.ACTOR_ID, getComponentDescription());
				params.put(MoveAction.PARAMETER_DIRECTION, move);
				env.performSpaceAction("move", params, srl);
				srl.waitForResult();
//				System.out.println("Moved (eat): "+move+", "+getAgentName());
			}
	
			// Eat food.
			SyncResultListener srl	= new SyncResultListener();
			Map params = new HashMap();
			params.put(ISpaceAction.ACTOR_ID, getComponentDescription());
			params.put(ISpaceAction.OBJECT_ID, food);
			env.performSpaceAction("eat", params, srl);
			srl.waitForResult();
//			System.out.println("Eaten (eat): "+food+", "+getAgentName());
		}
		catch(Exception e)
		{
//			System.err.println("Eat plan failed: "+e);
			
			// Move or eat failed, forget food until seen again.
			startAtomic();
			if(getBeliefbase().getBeliefSet("known_food").containsFact(food))
				getBeliefbase().getBeliefSet("known_food").removeFact(food);
			if(getBeliefbase().getBeliefSet("seen_food").containsFact(food))
				getBeliefbase().getBeliefSet("seen_food").removeFact(food);
			endAtomic();
			
			fail();
		}
	}
}
