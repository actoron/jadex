package jadex.bdi.planlib.simsupport.environment.simobject.task;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.SimulationEvent;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;

/** Tasks that directs the object towards a destination and
 *  issues an event when it has been reached.
 */
public class SetDestinationTask implements ISimObjectTask
{
	/** Default task name.
	 */
	public static final String DEFAULT_NAME = "goto_dest";
	
	/** Name of the task.
	 */
	private String name_;
	
	/** Position of the destination.
	 */
	private IVector2 targetPosition_;
	
	/** Speed with which to approach the destination
	 */
	private IVector1 speed_;
	
	/** Tolerance used when considering whether the destination has been reached.
	 */
	private IVector1 tolerance_;
	
	/** Task that moves the object.
	 */
	private MoveObjectTask moveTask_;
	
	public SetDestinationTask(IVector2 targetPosition,
			   				   IVector1 speed,
			   				   IVector1 tolerance)
	{
		this(DEFAULT_NAME, targetPosition, speed, tolerance);
	}
	
	public SetDestinationTask(String name,
							   IVector2 targetPosition,
							   IVector1 speed,
							   IVector1 tolerance)
	{
		name_ = name;
		targetPosition_ = targetPosition.copy();
		speed_ = speed.copy();
		tolerance_ = tolerance.copy();
		moveTask_ = null;
	}
	
	/** This method will be executed by the object before
	 *  the task gets added to the execution queue.
	 *  
	 *  @param object the object that is executing the task
	 */
	public void start(SimObject object)
	{
		moveTask_ = (MoveObjectTask) object.getTask(MoveObjectTask.DEFAULT_NAME);
	}
	
	/** This method will be executed by the object before
	 *  the task is removed from the execution queue.
	 *  
	 *  @param object the object that is executing the task
	 */
	public void shutdown(SimObject object)
	{
	}
	
	/** Directs the object towards the destination.
	 * 
	 *  @param deltaT time passed
	 *  @param object the object that is executing the task
	 */
	public void execute(IVector1 deltaT, SimObject object)
	{
		synchronized (object)
		{
			IVector2 currentPosition = object.getPositionAccess();
			IVector2 velocity = targetPosition_.copy().subtract(currentPosition).normalize().multiply(speed_);
		
			if (currentPosition.getDistance(targetPosition_).less(tolerance_))
			{
				// Destination reached, stop and trigger event.
			
				//Stop
				velocity.zero();
				
				object.removeTask(name_);
				
				SimulationEvent evt = new SimulationEvent(SimulationEvent.DESTINATION_REACHED);
				//TODO: Include parameters? yes, the object id, maybe more?
				evt.setParameter("object_id", object.getId());
				object.fireSimulationEvent(evt);
				
				
			}
			
			
			moveTask_.setVelocity(velocity);
		}
	}
	
	/** Returns the name of the task.
	 * 
	 *  @return name of the task.
	 */
	public String getName()
	{
		return name_;
	}
}
