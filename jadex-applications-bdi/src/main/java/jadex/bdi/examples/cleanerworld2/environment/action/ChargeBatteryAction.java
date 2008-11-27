package jadex.bdi.examples.cleanerworld2.environment.action;

import java.util.List;

import jadex.bdi.examples.cleanerworld2.Configuration;
import jadex.bdi.examples.cleanerworld2.cleaner.task.BatteryChargeTask;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.environment.action.ISimAction;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;

public class ChargeBatteryAction implements ISimAction
{
	public static final String DEFAULT_NAME = "charge_cleaner";
	
	/** Name of the action.
	 */
	private String name_;
	
	public ChargeBatteryAction()
	{
		name_ = DEFAULT_NAME;
	}
	
	public boolean perform(SimObject actor, SimObject object, List parameters, ISimulationEngine engine)
	{
		if ((actor.getType() == "cleaner") &&
			(object != null) &&
			(object.getType() == "charging_station") &&
			(actor.getPosition().getDistance(object.getPosition()).less(Configuration.REACH_DISTANCE)))
		{
			actor.addTask(new BatteryChargeTask());
			return true;
		}
		return false;
	}
	
	public String getName()
	{
		return name_;
	}
}
