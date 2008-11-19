package jadex.bdi.examples.cleanerworld2.cleaner;

import jadex.bdi.examples.cleanerworld2.Configuration;
import jadex.bdi.examples.cleanerworld2.environment.action.PickupWasteAction;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector1Double;
import jadex.bdi.planlib.simsupport.environment.simobject.task.GoToDestinationTask;
import jadex.bdi.runtime.IBelief;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import sun.security.action.GetBooleanAction;

/** Attempts to clean up some waste.
 */
public class AchieveCleanupPlan extends Plan
{
	public void body()
	{
		IBeliefbase b = getBeliefbase();
		Integer cleanerId = (Integer) b.getBelief("simobject_id").getFact();
		
		// Stop the cleaner.
		IGoal stop = createGoal("stop");
		dispatchSubgoalAndWait(stop);
		stop = null;
		
		// Go to waste position
		IVector2 wastePos = (IVector2) b.getBelief("waste_target_position").getFact();
		IGoal moveToWaste = createGoal("set_destination");
		moveToWaste.getParameter("destination").setValue(wastePos);
		dispatchSubgoalAndWait(moveToWaste);
		moveToWaste = null;
		
		waitForInternalEvent("reached_waste_event");
		Integer wasteId = (Integer) b.getBelief("waste_target").getFact();
		IGoal pickupWaste = createGoal("sim_perform_action");
		pickupWaste.getParameter("action").setValue(PickupWasteAction.DEFAULT_NAME);
		pickupWaste.getParameter("actor_id").setValue(cleanerId);
		pickupWaste.getParameter("object_id").setValue(wasteId);
		dispatchSubgoalAndWait(pickupWaste);
		
		
		// Re-enable waste sensor
		IGoal reenableSensor = createGoal("enable_waste_sensor");
		dispatchSubgoalAndWait(reenableSensor);
		
		// Remove search waypoint
		b.getBelief("waste_search_waypoint").setFact(null);
		b.getBelief("waste_target").setFact(null);
		if (!pickupWaste.isSucceeded())
		{
			//fail();
		}
		
	}
	
}
