package jadex.bdi.examples.cleanerworld2.cleaner.task;

import jadex.bdi.examples.cleanerworld2.Configuration;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.Vector1Double;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;
import jadex.bdi.planlib.simsupport.environment.SimulationEvent;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;
import jadex.bdi.planlib.simsupport.environment.simobject.task.SetDestinationTask;
import jadex.bdi.planlib.simsupport.environment.simobject.task.ISimObjectTask;
import jadex.bdi.planlib.simsupport.environment.simobject.task.MoveObjectTask;

/** Task simulating the slow discharging of the cleaner's battery.
 */
public class BatteryChargeTask implements ISimObjectTask
{
	/** Default name
	 */
	public static final String DEFAULT_NAME = "battery_charger";
	
	/** Task name
	 */
	private String name_;
	
	/** Battery state
	 */
	private IVector1 batteryState_;
	
	/** Default constructor.
	 */
	public BatteryChargeTask()
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
		System.out.print("Charging Battery: ");
		System.out.println(batteryState_.getAsDouble());
		if (batteryState_.less(Configuration.CHARGED_THRESHOLD))
		{
			IVector1 charge = Configuration.CLEANER_CHARGE_RATE.copy().multiply(deltaT);
			batteryState_.add(charge);
		}
		else
		{
			object.removeTask(name_);
			object.addTask(new LowBatteryWarnTask());
			SimulationEvent evt = new SimulationEvent("battery_charged");
			object.fireSimulationEvent(evt);
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
