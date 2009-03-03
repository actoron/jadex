package jadex.bdi.examples.cleanerworld2.cleaner;

import jadex.bdi.examples.cleanerworld2.Configuration;
import jadex.bdi.examples.cleanerworld2.environment.action.ChargeBatteryAction;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.commons.Tuple;

/** Attempts to clean up some waste.
 */
public class ChargeBatteryPlan extends Plan
{
	public void body()
	{
		IBeliefbase b = getBeliefbase();
		Integer cleanerId = (Integer) b.getBelief("simobject_id").getFact();
		
		// Stop the cleaner.
		IGoal subGoal = createGoal("stop");
		dispatchSubgoalAndWait(subGoal);
		
		// Look for stations if none are known
		IBeliefSet charging_stations = b.getBeliefSet("charging_stations");
		while (charging_stations.size() == 0)
		{
			subGoal = createGoal("sim_get_random_position");
			subGoal.getParameter("distance").setValue(Configuration.CLEANER_SIZE.copy());
			dispatchSubgoalAndWait(subGoal);
			// new search waypoint
			IVector2 waypoint = (IVector2) subGoal.getParameter("position").getValue();
			subGoal = createGoal("go_to_destination");
			subGoal.getParameter("destination").setValue(waypoint);
			dispatchSubgoalAndWait(subGoal);
		}
		
		// Find closest charging station from our current location
		subGoal = createGoal("sim_get_position");
		subGoal.getParameter("object_id").setValue(cleanerId);
		dispatchSubgoalAndWait(subGoal);
		IVector2 myPosition = (IVector2) subGoal.getParameter("position").getValue();
		Object[] chargingStationsArray = charging_stations.getFacts();
		Integer chargingStationId = null;
		IVector2 chargingStationPos = null;
		IVector1 distance = null;
		for (int i = 0; i < chargingStationsArray.length; ++i)
		{
			Tuple t = (Tuple) chargingStationsArray[i];
			if (chargingStationId == null)
			{
				chargingStationId = (Integer) t.get(0);
				chargingStationPos = (IVector2) t.get(1);
				distance = chargingStationPos.getDistance(myPosition);
			}
			else 
			{
				IVector2 newCSPos = (IVector2) t.get(1);
				if (newCSPos.getDistance(myPosition).less(distance))
				{
					chargingStationId = (Integer) t.get(0);
					chargingStationPos = newCSPos;
					distance = chargingStationPos.getDistance(myPosition);
				}
			}
		}
		
		// Go to the station
		boolean atPosition = false;
		while (!atPosition)
		{
			subGoal = createGoal("go_to_destination");
			subGoal.getParameter("destination").setValue(chargingStationPos.copy());
			dispatchSubgoalAndWait(subGoal);
			if (subGoal.isSucceeded())
			{
				atPosition = true;
			}
		}
		
		// Start charging
		subGoal = createGoal("sim_perform_action");
		subGoal.getParameter("action").setValue(ChargeBatteryAction.DEFAULT_NAME);
		subGoal.getParameter("actor_id").setValue(cleanerId);
		subGoal.getParameter("object_id").setValue(chargingStationId);
		dispatchSubgoalAndWait(subGoal);
		
		// Wait for charging to finish
		IInternalEvent evt = null;
		do
		{
			evt = waitForInternalEvent("simulation_event");
		}
		while (evt.getParameter("type").equals("battery_charged"));
	}
	
}
