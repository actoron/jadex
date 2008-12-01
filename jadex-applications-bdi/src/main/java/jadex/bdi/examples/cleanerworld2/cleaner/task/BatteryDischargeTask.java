package jadex.bdi.examples.cleanerworld2.cleaner.task;

import jadex.bdi.examples.cleanerworld2.Configuration;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector1Double;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;
import jadex.bdi.planlib.simsupport.environment.simobject.task.GoToDestinationTask;
import jadex.bdi.planlib.simsupport.environment.simobject.task.SetDestinationTask;
import jadex.bdi.planlib.simsupport.environment.simobject.task.ISimObjectTask;
import jadex.bdi.planlib.simsupport.environment.simobject.task.MoveObjectTask;

/** Task simulating the slow discharging of the cleaner's battery.
 */
public class BatteryDischargeTask implements ISimObjectTask
{
	/** Default name
	 */
	public static final String DEFAULT_NAME = "battery_discharger";
	
	/** Task name
	 */
	private String name_;
	
	/** Battery state
	 */
	private IVector1 batteryState_;
	
	/** Default constructor.
	 */
	public BatteryDischargeTask()
	{
		name_ = DEFAULT_NAME;
	}
	
	/** This method will be executed by the object before
	 *  the task gets added to the execution queue.
	 *  
	 *  @param object the object that is executing the task
	 */
	public void start(SimObject object)
	{
		batteryState_ = (IVector1) object.getProperty("battery");
	}
	
	/** This method will be executed by the object before
	 *  the task is removed from the execution queue.
	 *  
	 *  @param object the object that is executing the task
	 */
	public void shutdown(SimObject object)
	{
	}
	
	/** Slowly discharges the battery.
	 */
	public void execute(IVector1 deltaT, SimObject object)
	{
		if (Vector1Double.ZERO.less(batteryState_))
		{
			IVector1 discharge = Configuration.CLEANER_DISCHARGE_RATE.copy().multiply(deltaT);
			batteryState_.subtract(discharge);
		}
		else
		{
			object.removeTask(GoToDestinationTask.DEFAULT_NAME);
			((IVector2) object.getProperty("velocity")).assign(Vector2Double.ZERO);
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
