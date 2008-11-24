package jadex.bdi.examples.cleanerworld2.cleaner;

import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;

public class WasteFoundEventPlan extends Plan
{
	public void body()
	{
		
		IInternalEvent event = (IInternalEvent) getReason();
		Integer wasteId = (Integer) event.getParameter("waste_id").getValue();
		IVector2 wastePosition = (IVector2) event.getParameter("position").getValue();
		IGoal achieveCleanup = createGoal("achieve_cleanup");
		achieveCleanup.getParameter("waste").setValue(wasteId);
		achieveCleanup.getParameter("waste_position").setValue(wastePosition);
		dispatchTopLevelGoal(achieveCleanup);
	}
}
