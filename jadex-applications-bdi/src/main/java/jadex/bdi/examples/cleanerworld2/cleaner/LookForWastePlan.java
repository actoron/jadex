package jadex.bdi.examples.cleanerworld2.cleaner;

import jadex.bdi.examples.cleanerworld2.Configuration;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector1Double;
import jadex.bdi.runtime.IBelief;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import sun.security.action.GetBooleanAction;

public class LookForWastePlan extends Plan
{
	public void body()
	{
		IBeliefbase b = getBeliefbase();
		IBelief wasteTarget = b.getBelief("waste_target");
		Integer cleanerId = (Integer) b.getBelief("simobject_id").getFact();
		
		while (wasteTarget.getFact() == null)
		{
			IGoal subGoal = createGoal("sim_get_random_position");
			subGoal.getParameter("distance").setValue(Configuration.CLEANER_SIZE.copy());
			dispatchSubgoalAndWait(subGoal);
			// new search waypoint
			IVector2 waypoint = (IVector2) subGoal.getParameter("position");
			//Go there
			subGoal = createGoal("sim_set_destination");
			subGoal.getParameter("object_id").setValue(cleanerId);
			subGoal.getParameter("destination").setValue(waypoint);
			subGoal.getParameter("speed").setValue(Configuration.CLEANER_SPEED);
			subGoal.getParameter("tolerance").setValue(Configuration.REACH_DISTANCE.copy().multiply(new Vector1Double(0.5)));
			dispatchSubgoalAndWait(subGoal);
		}
	}
	
}
