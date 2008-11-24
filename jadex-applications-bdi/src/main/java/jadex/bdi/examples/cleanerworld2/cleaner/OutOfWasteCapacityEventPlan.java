package jadex.bdi.examples.cleanerworld2.cleaner;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

public class OutOfWasteCapacityEventPlan extends Plan
{
	public void body()
	{
		IGoal disposeWaste = createGoal("dispose_waste");
		dispatchTopLevelGoal(disposeWaste);
	}
}
