package sodekovs.mapscollision;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.IExtensionInstance;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.IVector2;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract Class that models default behaviour for all Humanoid Agents
 * 
 * @author wolf.posdorfer
 * 
 */
public abstract class Humanoid
{

	@Agent
	protected MicroAgent _agent;

	/** The creature's self representation. */
	protected ISpaceObject _myself;

	/** The environment. */
	protected Grid2D _env;

	/** The result listener starting the next action. */
	protected IResultListener<Object> _actionListener;

	/**
	 * Call this within executeBody();
	 */
	protected void inheritedExecuteBody()
	{
		IFuture<IExtensionInstance> space = _agent.getParentAccess().getExtension("my2dspace");
		space.addResultListener(_agent
				.createResultListener(new DefaultResultListener<IExtensionInstance>()
				{
					@Override
					public void exceptionOccurred(Exception exception)
					{
						// DONT CARE LEAVE ME ALONE!!
					}

					public void resultAvailable(IExtensionInstance result)
					{
						if (result == null)
							return;

						_env = (Grid2D) result;
						_myself = _env.getAvatar(_agent.getComponentDescription());
						_actionListener = new DefaultResultListener<Object>()
						{
							@Override
							public void exceptionOccurred(Exception exception)
							{
								// I DONT CARE!
							}

							public void resultAvailable(Object result)
							{
								_agent.getExternalAccess().scheduleStep(new IComponentStep<Void>()
								{
									public IFuture<Void> execute(IInternalAccess ia)
									{
										act();
										// See if we need to adjust our Position
										if (!MapService.getInstance().isPassable(getMyPosition()))
										{
											IVector2 newpos = MapService.getInstance()
													.getValidPosition(getMyPosition());
											if (newpos != null)
												_myself.setProperty(Space2D.PROPERTY_POSITION,
														newpos);
										}
										return IFuture.DONE;
									}
								});
							}
						};

						act();

					}
				}));
	}

	/**
	 * The act Method, increments _infectionTimer, when infected also sets the
	 * infection-Tag when a Zombie gets within 0-Distance
	 */
	protected abstract void act();

	/**
	 * Returns the Position of this Agent on the Space2D Grid
	 * 
	 * @return
	 */
	public IVector2 getMyPosition()
	{
		if (_myself != null)
			return ((IVector2) _myself.getProperty(Space2D.PROPERTY_POSITION)).copy();
		else
			return null;
	}

	/**
	 * Returns the visible Range of this Agent set by the Space2D
	 * 
	 * @return
	 */
	public IVector1 getRange()
	{
		return ((IVector1) _myself.getProperty("range")).copy();
	}

	/**
	 * Moves the Agent in the specified direction
	 * 
	 * @param dir
	 */
	protected void moveTo(MoveAction.Direction dir)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(ISpaceAction.ACTOR_ID, _agent.getComponentDescription());
		params.put(MoveAction.PARAMETER_DIRECTION, dir);
		_env.performSpaceAction("move", params, _actionListener);
	}

}
