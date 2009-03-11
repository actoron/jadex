/**
 * 
 */
package jadex.bdi.planlib.simsupport.environment.grid.simobject.task;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector2Int;
import jadex.bdi.planlib.simsupport.environment.SimulationEvent;
import jadex.bdi.planlib.simsupport.environment.grid.GridPosition;
import jadex.bdi.planlib.simsupport.environment.grid.IGridSimulationEngine;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;
import jadex.bdi.planlib.simsupport.environment.simobject.task.ISimObjectTask;

/**
 * @author Claas
 *
 */
public abstract class GoToDirectionTask implements ISimObjectTask
{
	// ------ constants -------
	
	/** Default task name. */
	public static final String	DEFAULT_NAME	= "go_to_direction";
	
	/** one grid field up (e.g. +1 in Y direction) */
	public static final IVector2	DIRECTION_UP	= new Vector2Int(0,1);
	/** one grid field down (e.g. -1 in Y direction) */
	public static final IVector2	DIRECTION_DOWN	= new Vector2Int(0,-1);
	/** one grid field right (e.g. +1 in X direction) */
	public static final IVector2	DIRECTION_RIGHT	= new Vector2Int(1,0);
	/** one grid field left (e.g. -1 in Y direction) */
	public static final IVector2	DIRECTION_LEFT	= new Vector2Int(-1,0);

	// ------ fields -------
	
	/** Name of the task. */
	protected String			name_;

	/** Position of the destination. */
	protected IVector2			targetPosition_;

	/** Speed with which to approach the destination */
	protected IVector1			speed_;

	/** The object's velocity. */
	protected IVector2			velocity_;

	/** The MoveObjectTask. */
	protected MoveObjectTask	moveTask_;

	// TODO: get area_behavior_ and areaSize_ from sim object? like velocity?
	
	/** The behavior of the environment. */
	protected int				area_behavior_;
	
	/** The environment size. */
	protected IVector2			areaSize_;
	
	/** The direction to move. */
	protected IVector2			direction_;
	
	
	// ------ constructor -------
	
	protected GoToDirectionTask(String name, IVector2 direction, IVector1 speed,IVector2 areaSize, int area_behavior)
	{
		this.name_ = name;
		this.direction_ = direction;
		this.area_behavior_ = area_behavior;
		this.areaSize_ = areaSize.copy();
		this.speed_ = speed.copy();
		
		
	}

	/**
	 * This method will be executed by the object before the task gets added to
	 * the execution queue.
	 * 
	 * @param object the object that is executing the task
	 */
	public void start(SimObject object)
	{
		// MoveObjectTask must exist
		assert object.getTask(MoveObjectTask.DEFAULT_NAME) != null;
		assert object.getTask(MoveObjectTask.DEFAULT_NAME) instanceof MoveObjectTask;
		// MoveObjectTask must have initialized velocity
		assert object.getProperty("velocity") != null;
		moveTask_ = (MoveObjectTask)object.getTask(MoveObjectTask.DEFAULT_NAME);
		velocity_ = (IVector2)object.getProperty("velocity");
		
		targetPosition_ = createTargetPosition(object.getPositionAccess().copy(), direction_, areaSize_, area_behavior_);

	}

	/**
	 * This method will be executed by the object before the task is removed
	 * from the execution queue.
	 * 
	 * @param object the object that is executing the task
	 */
	public void shutdown(SimObject object)
	{
		synchronized(object)
		{
			IVector2 currentPosition = object.getPositionAccess();
			SimulationEvent evt = new SimulationEvent(
					SimulationEvent.DESTINATION_REACHED);
			evt.setParameter("position", currentPosition.copy());
			object.fireSimulationEvent(evt);
		}
	}

	/**
	 * Directs the object towards the destination.
	 * 
	 * @param deltaT time passed
	 * @param object the object that is executing the task
	 */
	public void execute(IVector1 deltaT, SimObject object)
	{
		synchronized(object)
		{
			IVector2 currentPosition = object.getPositionAccess();
			
			IVector2 velocity = direction_.copy().multiply(speed_);

			if(currentPosition.copy().mod(areaSize_).getDistance(targetPosition_).less(
					moveTask_.getLastMovementDelta().getLength()))
			{
				// Destination reached, stop, set correct position and trigger
				// event.

				// Stop
				velocity.zero();

				// Set precise position
				object.assignPosition(targetPosition_);

				object.removeTask(name_);
			}


			velocity_.assign(velocity);
		}
	}

	/**
	 * Returns the name of the task.
	 * 
	 * @return name of the task.
	 */
	public String getName()
	{
		return name_;
	}
	
	// ----- static helper methods ------
	
	/**
	 * Create target position for GoToDirection tasks 
	 */
	public static GridPosition createTargetPosition(IVector2 position, IVector2 direction, IVector2 areaSize, int area_behavior)
	{
		IVector2 targetPosition = null;
		switch (area_behavior)
		{
			case IGridSimulationEngine.AREA_BEHAVIOR_TORUS:
				targetPosition = position.copy().add(direction).mod(areaSize);
				break;
				
			case IGridSimulationEngine.AREA_BEHAVIOR_EUCLID:
				targetPosition = position.copy().add(direction);
				int x = targetPosition.getXAsInteger();
				int y = targetPosition.getYAsInteger();
				// don't move out of the grid
				if (0 > x || x > areaSize.getXAsInteger() || 0 > y || y > areaSize.getYAsInteger())
				{
					targetPosition = position.copy();
				}
				break;

			default:
				targetPosition = position.copy();
				break;
		}
		
		// ensure targets are Int vectors
		return new GridPosition(targetPosition);
	}
	
}
