package jadex.micro.examples.hunterprey;

import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.bdi.examples.hunterprey.MoveAction;
import jadex.bridge.ComponentTerminatedException;
import jadex.commons.concurrent.IResultListener;
import jadex.microkernel.MicroAgent;

import java.util.HashMap;
import java.util.Map;

/**
 *  Simple agent participating in (bdi-based) hunter prey.
 */
public class MicroPreyAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The environment. */
	protected Grid2D	env;
	
	/** The creature's self representation. */
	protected ISpaceObject	myself;
	
	/** The last move direction (if any). */
	protected String	lastdir;
	
	/** The nearest food (if any). */
	protected ISpaceObject	food;
	
	/** The result listener starting the next action. */
	protected IResultListener	listener;
	
	//-------- MicroAgent methods --------

	/**
	 *  Execute a step.
	 */
	public void executeBody()
	{
		this.env	= (Grid2D)getApplicationContext().getSpace("my2dspace");
		this.myself	= env.getAvatar(getAgentIdentifier());
		this.listener	= new IResultListener()
		{
			public void exceptionOccurred(Object source, Exception e)
			{
//				e.printStackTrace();
				try
				{
					getExternalAccess().invokeLater(new Runnable()
					{
						public void run()
						{
							// If move failed, forget about food and turn 90°.
							food	= null;
							
//							System.out.println("Move failed: "+e);
							if(MoveAction.DIRECTION_LEFT.equals(lastdir) || MoveAction.DIRECTION_RIGHT.equals(lastdir))
							{
								lastdir	= Math.random()>0.5 ? MoveAction.DIRECTION_UP : MoveAction.DIRECTION_DOWN;
							}
							else
							{
								lastdir	= Math.random()>0.5 ? MoveAction.DIRECTION_LEFT : MoveAction.DIRECTION_RIGHT;
							}
	
							act();
						}
					});
				}
				catch(ComponentTerminatedException ate)
				{
				}
			}
			
			public void resultAvailable(Object source, Object result)
			{
				getExternalAccess().invokeLater(new Runnable()
				{
					public void run()
					{
						act();
					}
				});
			}
		};

		act();
	}
	
	//-------- methods --------
	
	/**
	 *  Choose and perform an action.
	 */
	protected void	act()
	{
//		System.out.println("nearest food for: "+getAgentName()+", "+food);
			
		// Get current position.
		IVector2	pos	= (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);
		
		if(food!=null && pos.equals(food.getProperty(Space2D.PROPERTY_POSITION)))
		{
			// Perform eat action.
			Map params = new HashMap();
			params.put(ISpaceAction.ACTOR_ID, getAgentIdentifier());
			params.put(ISpaceAction.OBJECT_ID, food);
			env.performSpaceAction("eat", params, listener);
		}

		else
		{
			// Move towards the food, if any.
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
					food	= null;
				}
			}
			
			// When no food, turn 90° with probability 0.25, otherwise continue moving in same direction.
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
			Map params = new HashMap();
			params.put(ISpaceAction.ACTOR_ID, getAgentIdentifier());
			params.put(MoveAction.PARAMETER_DIRECTION, lastdir);
			env.performSpaceAction("move", params, listener);
		}
	}
	
	//-------- attributes accessors --------
	
	/**
	 *  Get the known food.
	 */
	public ISpaceObject	getNearestFood()
	{
		return food;
	}
	
	/**
	 *  Set the known food.
	 */
	public void	setNearestFood(ISpaceObject food)
	{
		this.food	= food;
	}
}
