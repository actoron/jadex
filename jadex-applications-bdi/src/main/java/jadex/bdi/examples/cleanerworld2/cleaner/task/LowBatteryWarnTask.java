package jadex.bdi.examples.cleanerworld2.cleaner.task;

import jadex.bdi.examples.cleanerworld2.Configuration;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.environment.SimulationEvent;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;
import jadex.bdi.planlib.simsupport.environment.simobject.task.ISimObjectTask;

/** This task will warn on low battery.
 */
public class LowBatteryWarnTask implements ISimObjectTask
{
	/** Default name
	 */
	public static final String DEFAULT_NAME = "battery_warner";
	
	/** Task name
	 */
	private String name_;
	
	/** Default constructor.
	 */
	public LowBatteryWarnTask()
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
	}
	
	/** This method will be executed by the object before
	 *  the task is removed from the execution queue.
	 *  
	 *  @param object the object that is executing the task
	 */
	public void shutdown(SimObject object)
	{
	}
	
	/** Warns when the battery is low.
	 */
	public void execute(IVector1 deltaT, SimObject object)
	{
		IVector1 batteryState = (IVector1) object.getProperty("battery");
		if (batteryState.less(Configuration.LOW_BATTERY_THRESHOLD))
		{
			object.removeTask(name_);
			SimulationEvent lowBattery = new SimulationEvent("battery_low");
			lowBattery.setParameter("object_id", object.getId());
			object.fireSimulationEvent(lowBattery);
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
