package jadex.bdi.examples.cleanerworld2.cleaner;

import jadex.bdi.examples.cleanerworld2.Configuration;
import jadex.bdi.examples.cleanerworld2.environment.action.PickupWasteAction;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector1Double;
import jadex.bdi.planlib.simsupport.environment.simobject.task.SetDestinationTask;
import jadex.bdi.runtime.IBelief;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

/** Attempts to clean up some waste.
 */
public class AchieveCleanupPlan extends Plan
{
	public void body()
	{
		IBeliefbase b = getBeliefbase();
		Integer cleanerId = (Integer) b.getBelief("simobject_id").getFact();
		
		// Stop the cleaner.
		IGoal subGoal = createGoal("stop");
		dispatchSubgoalAndWait(subGoal);
		
		// Go to waste position
		boolean atPosition = false;
		while (!atPosition)
		{
			IVector2 wastePos = (IVector2) getParameter("waste_position").getValue();
			subGoal = createGoal("go_to_destination");
			subGoal.getParameter("destination").setValue(wastePos);
			
			dispatchSubgoalAndWait(subGoal);
			if (subGoal.isSucceeded())
			{
				atPosition = true;
			}
		}
		
		Integer wasteId = (Integer) getParameter("waste").getValue();
		IGoal pickupWaste = createGoal("sim_perform_action");
		pickupWaste.getParameter("action").setValue(PickupWasteAction.DEFAULT_NAME);
		pickupWaste.getParameter("actor_id").setValue(cleanerId);
		pickupWaste.getParameter("object_id").setValue(wasteId);
		dispatchSubgoalAndWait(pickupWaste);
		
		// Re-enable waste sensor
		IGoal reenableSensor = createGoal("enable_waste_sensor");
		dispatchSubgoalAndWait(reenableSensor);
	}
	
}
