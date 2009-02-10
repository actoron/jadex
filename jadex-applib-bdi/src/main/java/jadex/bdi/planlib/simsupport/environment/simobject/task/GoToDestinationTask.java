package jadex.bdi.planlib.simsupport.environment.simobject.task;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.SimulationEvent;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;


/**
 * Tasks that directs the object towards a destination and issues an event when
 * it has been reached.
 */
public class GoToDestinationTask implements ISimObjectTask
{
	/** Default task name. */
	public static final String	DEFAULT_NAME	= "go_to_destination";

	/** Name of the task. */
	private String				name_;

	/** Position of the destination. */
	private IVector2			targetPosition_;

	/** Speed with which to approach the destination. */
	private IVector1			speed_;

	/** Tolerance used when considering whether the destination has been reached. */
	private IVector1			tolerance_;

	/** The object's velocity. */
	private IVector2			velocity_;

	public GoToDestinationTask(IVector2 targetPosition, IVector1 speed,
			IVector1 tolerance)
	{
		this(DEFAULT_NAME, targetPosition, speed, tolerance);
	}

	public GoToDestinationTask(String name, IVector2 targetPosition,
			IVector1 speed, IVector1 tolerance)
	{
		name_ = name;
		targetPosition_ = targetPosition.copy();
		speed_ = speed.copy();
		tolerance_ = tolerance.copy();
	}

	/**
	 * This method will be executed by the object before the task gets added to
	 * the execution queue.
	 * 
	 * @param object the object that is executing the task
	 */
	public void start(SimObject object)
	{
		// MoveObjectTask must have initialized velocity
		assert object.getProperty("velocity") != null;
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
					SimulationEvent.GO_TO_DESTINATION_REACHED);
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

			if(currentPosition.getDistance(targetPosition_).less(tolerance_))
			{
				// Destination reached, stop and trigger event.

				// Stop
				velocity.zero();

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
