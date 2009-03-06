package jadex.bdi.planlib.simsupport.environment.grid.simobject.task;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;
import jadex.bdi.planlib.simsupport.environment.simobject.task.ISimObjectTask;


/**
 * Tasks that moves the object according to its velocity.
 */
public class MoveObjectTask implements ISimObjectTask
{
	/** Default task name. */
	public static final String	DEFAULT_NAME	= "grid_move_obj";

	/** Name of the task. */
	private String				name_;

	/** Current object velocity. */
	private IVector2			velocity_;

	/** Last movement delta. */
	private IVector2			lastMovementDelta_;

	// TODO: use simobject property? Should the sim object know this about the area?
	/** The size of the Area */
	private IVector2 areaSize_;

//	/** Creates a new default MoveObjectTask. */
//	public MoveObjectTask(IVector2 areaSize)
//	{
//		this(new Vector2Double(0.0), areaSize);
//	}

	/**
	 * Creates a new default MoveObjectTask with the given start velocity.
	 */
	public MoveObjectTask(IVector2 velocity, IVector2 areaSize)
	{
		this(DEFAULT_NAME, velocity, areaSize);
	}

	/**
	 * Creates a special MoveObjectTask with a special name and velocity.
	 * 
	 * @param name the name of the task
	 * @param velocity start velocity
	 */
	public MoveObjectTask(String name, IVector2 velocity, IVector2 areaSize)
	{
		name_ = name;
		velocity_ = velocity.copy();
		lastMovementDelta_ = new Vector2Double(0.0);
		areaSize_ = areaSize.copy();
	}

	/**
	 * This method will be executed by the object before the task gets added to
	 * the execution queue.
	 * 
	 * @param object the object that is executing the task
	 */
	public synchronized void start(SimObject object)
	{
		object.setProperty("velocity", velocity_);
	}

	/**
	 * This method will be executed by the object before the task is removed
	 * from the execution queue.
	 * 
	 * @param object the object that is executing the task
	 */
	public synchronized void shutdown(SimObject object)
	{
	}

	/**
	 * Moves the object.
	 * 
	 * @param deltaT time passed
	 * @param object the object that is executing the task
	 */
	public synchronized void execute(IVector1 deltaT, SimObject object)
	{
		synchronized(object)
		{
			IVector2 position = object.getPositionAccess();
			lastMovementDelta_ = velocity_.copy().multiply(deltaT);
			position.add(lastMovementDelta_).mod(areaSize_);
		}
	}

	/**
	 * Returns the name of the task.
	 * 
	 * @return name of the task.
	 */
	public synchronized String getName()
	{
		return name_;
	}

	/**
	 * Returns the last movement delta that was added to the position.
	 */
	public synchronized IVector2 getLastMovementDelta()
	{
		return lastMovementDelta_;
	}
}
