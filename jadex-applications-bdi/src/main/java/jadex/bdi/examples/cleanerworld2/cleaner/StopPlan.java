package jadex.bdi.examples.cleanerworld2.cleaner;

import jadex.bdi.planlib.simsupport.common.math.Vector2Double;
import jadex.bdi.planlib.simsupport.environment.simobject.task.SetDestinationTask;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

/** Stops the cleaner.
 */
public class StopPlan extends Plan
{
	public void body()
	{
		IBeliefbase b = getBeliefbase();
		Integer cleanerId =  (Integer) b.getBelief("simobject_id").getFact();
		// Remove destination
		IGoal removeDestination = createGoal("sim_remove_task");
		removeDestination.getParameter("object_id").setValue(cleanerId);
		removeDestination.getParameter("task_name").setValue(SetDestinationTask.DEFAULT_NAME);
		dispatchSubgoalAndWait(removeDestination);
		
		// Stop the cleaner
		IGoal stopCleaner = createGoal("sim_set_velocity");
		stopCleaner.getParameter("object_id").setValue(cleanerId);
		stopCleaner.getParameter("velocity").setValue(new Vector2Double(0.0));
		
		dispatchSubgoalAndWait(stopCleaner);
	}
}
