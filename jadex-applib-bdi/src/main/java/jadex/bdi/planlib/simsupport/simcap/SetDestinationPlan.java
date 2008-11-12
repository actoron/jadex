package jadex.bdi.planlib.simsupport.simcap;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;
import jadex.bdi.planlib.simsupport.environment.simobject.task.GoToDestinationTask;
import jadex.bdi.planlib.simsupport.environment.simobject.task.ISimObjectTask;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

/** Plan setting the velocity (direction and speed) of the object.
 */
public class SetDestinationPlan extends Plan
{
	/** Sets the new velocity of the object.
	 */
	public void body()
	{
		Integer objectId = (Integer) getParameter("object_id").getValue();
		IVector2 destination = (IVector2) getParameter("destination").getValue();
		IVector1 speed = (IVector1) getParameter("speed").getValue();
		IVector1 tolerance = (IVector1) getParameter("tolerance").getValue();
		
		ISimObjectTask task = new GoToDestinationTask(destination, speed, tolerance);
		
		IGoal setTask = createGoal("sim_add_task");
		setTask.getParameter("object_id").setValue(objectId);
		setTask.getParameter("task").setValue(task);
		dispatchSubgoalAndWait(setTask);
	}
}
