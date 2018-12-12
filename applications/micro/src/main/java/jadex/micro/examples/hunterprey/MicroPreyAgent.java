package jadex.micro.examples.hunterprey;

import java.util.HashMap;
import java.util.Map;

import jadex.application.EnvironmentService;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.transformation.annotations.Classname;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 *  Simple agent participating in hunter prey.
 */
@Agent
public class MicroPreyAgent
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
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	//-------- MicroAgent methods --------

	/**
	 *  Execute a step.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
		final Future<Void>	ret	= new Future<Void>();
		
		EnvironmentService.getSpace(agent, "my2dspace")
			.addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
		{
			public void customResultAvailable(Object result)
			{
				env	= (Grid2D)result;
		
				myself	= env.getAvatar(agent.getDescription());
				listener = new IResultListener()
				{
					public void exceptionOccurred(Exception e)
					{
		//				e.printStackTrace();
						try
						{
							agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
							{
								@Classname("act")
								public IFuture<Void> execute(IInternalAccess agent)
								{
									// If move failed, forget about food and turn 90 degrees.
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
									
									return IFuture.DONE;
								}
								
								public String toString()
								{
									return "prey.act()";
								}
							});
						}
						catch(ComponentTerminatedException ate)
						{
						}
					}
					
					public void resultAvailable(Object result)
					{
						agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
						{
							@Classname("act2")
							public IFuture<Void> execute(IInternalAccess ia)
							{
								act();
								return IFuture.DONE;
							}
							
							public String toString()
							{
								return "prey.act()";
							}
						});
					}
				};
		
				act();
			}
		});
		
		return ret; // never kill!
	}
	
	//-------- methods --------
	
	/**
	 *  Choose and perform an action.
	 */
	protected void	act()
	{
//		System.out.println("nearest food for: "+agent.getComponentIdentifier().getLocalName()+", "+food);
			
		// Get current position.
		IVector2	pos	= (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);
		
		if(food!=null && pos.equals(food.getProperty(Space2D.PROPERTY_POSITION)))
		{
			// Perform eat action.
			Map params = new HashMap();
			params.put(ISpaceAction.ACTOR_ID, agent.getDescription());
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
			Map params = new HashMap();
			params.put(ISpaceAction.ACTOR_ID, agent.getDescription());
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
//		System.out.println("setting food: "+food+" "+agent.getComponentIdentifier().getLocalName());
		this.food	= food;
	}
}
