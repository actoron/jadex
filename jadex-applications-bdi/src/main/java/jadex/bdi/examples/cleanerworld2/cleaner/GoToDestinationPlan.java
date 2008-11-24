package jadex.bdi.examples.cleanerworld2.cleaner;

import jadex.bdi.examples.cleanerworld2.Configuration;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector1Double;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

/** Moves the cleaner to a new position.
 */
public class GoToDestinationPlan extends Plan
{
	private static final IVector1 TOLERANCE = Configuration.REACH_DISTANCE.copy().multiply(new Vector1Double(0.5));
	
	public void body()
	{
		Integer cleanerId = (Integer) getBeliefbase().getBelief("simobject_id").getFact();
		IVector2 destination = (IVector2) getParameter("destination").getValue();
		IGoal goToDestination = createGoal("sim_go_to_destination");
		goToDestination.getParameter("object_id").setValue(cleanerId);
		goToDestination.getParameter("destination").setValue(destination);
		goToDestination.getParameter("speed").setValue(Configuration.CLEANER_SPEED);
		goToDestination.getParameter("tolerance").setValue(TOLERANCE.copy());
		dispatchSubgoalAndWait(goToDestination);
		
		if (!goToDestination.isSucceeded())
		{
			fail();
		}
	}
}
