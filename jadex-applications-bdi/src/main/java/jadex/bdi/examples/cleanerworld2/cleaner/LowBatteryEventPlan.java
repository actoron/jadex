package jadex.bdi.examples.cleanerworld2.cleaner;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

public class LowBatteryEventPlan extends Plan
{
	public void body()
	{
		IGoal chargeBattery = createGoal("charge_battery");
		dispatchTopLevelGoal(chargeBattery);
	}
}
