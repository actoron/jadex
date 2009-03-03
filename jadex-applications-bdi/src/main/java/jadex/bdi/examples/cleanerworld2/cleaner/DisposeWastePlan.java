package jadex.bdi.examples.cleanerworld2.cleaner;

import jadex.bdi.examples.cleanerworld2.Configuration;
import jadex.bdi.examples.cleanerworld2.environment.action.DisposeWasteAction;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.commons.Tuple;

/** Attempts to clean up some waste.
 */
public class DisposeWastePlan extends Plan
{
	public void body()
	{
		IBeliefbase b = getBeliefbase();
		Integer cleanerId = (Integer) b.getBelief("simobject_id").getFact();
		
		// Stop the cleaner.
		IGoal subGoal = createGoal("stop");
		dispatchSubgoalAndWait(subGoal);
		
		// Look for bins if none are known
		IBeliefSet wasteBins = b.getBeliefSet("waste_bins");
		while (wasteBins.size() == 0)
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
		
		// Find closest bin from our current location
		subGoal = createGoal("sim_get_position");
		subGoal.getParameter("object_id").setValue(cleanerId);
		dispatchSubgoalAndWait(subGoal);
		IVector2 myPosition = (IVector2) subGoal.getParameter("position").getValue();
		Object[] wasteBinArray = wasteBins.getFacts();
		Integer bin = null;
		IVector2 binPos = null;
		IVector1 distance = null;
		for (int i = 0; i < wasteBinArray.length; ++i)
		{
			Tuple t = (Tuple) wasteBinArray[i];
			if (bin == null)
			{
				bin = (Integer) t.get(0);
				binPos = (IVector2) t.get(1);
				distance = binPos.getDistance(myPosition);
			}
			else
			{
				IVector2 newBinPos = (IVector2) t.get(1);
				if (newBinPos.getDistance(myPosition).less(distance))
				{
					bin = (Integer) t.get(0);
					binPos = newBinPos;
					distance = binPos.getDistance(myPosition);
				}
			}
		}
		
		// Go to the bin
		boolean atPosition = false;
		while (!atPosition)
		{
			subGoal = createGoal("go_to_destination");
			subGoal.getParameter("destination").setValue(binPos.copy());
			dispatchSubgoalAndWait(subGoal);
			if (subGoal.isSucceeded())
			{
				atPosition = true;
			}
		}
		
		// Dispose the waste
		subGoal = createGoal("sim_perform_action");
		subGoal.getParameter("action").setValue(DisposeWasteAction.DEFAULT_NAME);
		subGoal.getParameter("actor_id").setValue(cleanerId);
		subGoal.getParameter("object_id").setValue(bin);
		dispatchSubgoalAndWait(subGoal);
	}
	
}
