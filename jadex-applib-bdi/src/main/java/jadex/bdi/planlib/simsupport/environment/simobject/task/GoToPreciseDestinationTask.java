package jadex.bdi.planlib.simsupport.environment.simobject.task;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.SimulationEvent;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;


/**
 * Go precisely to the destination specified, even using sub-stimulation time
 * steps if necessary.
 */
public class GoToPreciseDestinationTask implements ISimObjectTask
{
	/** Default task name. */
	public static final String	DEFAULT_NAME	= "go_to_precise_destination";

	/** Name of the task. */
	private String				name_;

	/** Position of the destination. */
	private IVector2			targetPosition_;

	/** Speed with which to approach the destination */
	private IVector1			speed_;

	/** The object's velocity. */
	private IVector2			velocity_;

	/** The MoveObjectTask. */
	private MoveObjectTask		moveTask_;

	public GoToPreciseDestinationTask(IVector2 targetPosition, IVector1 speed)
	{
		this(DEFAULT_NAME, targetPosition, speed);
	}

	public GoToPreciseDestinationTask(String name, IVector2 targetPosition,
			IVector1 speed)
	{
		name_ = name;
		targetPosition_ = targetPosition.copy();
		speed_ = speed.copy();
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
					SimulationEvent.GO_TO_PRECISE_DESTINATION_REACHED);
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
			IVector2 velocity = targetPosition_.copy()
					.subtract(currentPosition).normalize().multiply(speed_);

			if(currentPosition.getDistance(targetPosition_).less(
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
}
