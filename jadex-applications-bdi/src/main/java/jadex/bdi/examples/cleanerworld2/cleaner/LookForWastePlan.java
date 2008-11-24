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
		Integer cleanerId = (Integer) b.getBelief("simobject_id").getFact();
		
		IGoal subGoal = createGoal("sim_get_random_position");
		subGoal.getParameter("distance").setValue(Configuration.CLEANER_SIZE.copy());
		dispatchSubgoalAndWait(subGoal);
		// new search waypoint
		IVector2 waypoint = (IVector2) subGoal.getParameter("position").getValue();
		//Go there
		subGoal = createGoal("go_to_destination");
		subGoal.getParameter("destination").setValue(waypoint);
		dispatchSubgoalAndWait(subGoal);
	}
	
}
