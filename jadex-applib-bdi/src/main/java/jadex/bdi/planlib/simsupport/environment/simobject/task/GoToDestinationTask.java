package jadex.bdi.planlib.simsupport.environment.simobject.task;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.SimulationEvent;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;

/** Tasks that directs the object towards a destination and
 *  issues an event when it has been reached.
 */
public class GoToDestinationTask implements ISimObjectTask
{
	/** Position of the destination.
	 */
	private IVector2 targetPosition_;
	
	/** Speed with which to approach the destination
	 */
	private IVector1 speed_;
	
	/** Tolerance used when considering whether the destination has been reached.
	 */
	private IVector1 tolerance_;
	
	public GoToDestinationTask(IVector2 targetPosition,
							   IVector1 speed,
							   IVector1 tolerance)
	{
		targetPosition_ = targetPosition.copy();
		speed_ = speed.copy();
		tolerance_ = tolerance.copy();
	}
	
	public void executeTask(IVector1 deltaT, SimObject object)
	{
		IVector2 currentPosition = object.getPosition();
		IVector2 velocity = targetPosition_.copy().subtract(currentPosition).normalize().multiply(speed_);
		
		if (currentPosition.getDistance(targetPosition_).less(tolerance_))
		{
			// Destination reached, stop and trigger event.
			
			//Stop
			velocity.zero();
			
			SimulationEvent evt = new SimulationEvent(SimulationEvent.DESTINATION_REACHED);
			//TODO: Include parameters? yes, the object id, maybe more?
			evt.setParameter("object_id", object.getId());
			object.fireSimulationEvent(evt);
			object.removeTask(this);
		}
		
		object.setVelocity(velocity);
	}
	
	public boolean equals(Object obj)
	{
		if (obj instanceof GoToDestinationTask)
		{
			GoToDestinationTask other = (GoToDestinationTask) obj;
			if ((targetPosition_.equals(other.targetPosition_)) &&
				(speed_.equals(other.speed_)) &&
				(tolerance_.equals(other.tolerance_)))
			{
				return true;
			}
		}
		return false;
	}
}
