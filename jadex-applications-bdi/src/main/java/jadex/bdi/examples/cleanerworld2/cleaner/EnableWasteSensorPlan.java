package jadex.bdi.examples.cleanerworld2.cleaner;

import jadex.bdi.examples.cleanerworld2.environment.process.WasteSensorProcess;
import jadex.bdi.planlib.simsupport.environment.process.IEnvironmentProcess;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

/** Enables the waste sensor.
 */
public class EnableWasteSensorPlan extends Plan
{
	public void body()
	{
		Integer cleanerId = (Integer) getBeliefbase().getBelief("simobject_id").getFact();
		String processName = WasteSensorProcess.DEFAULT_NAME + cleanerId.toString();
		IEnvironmentProcess sensorProcess = new WasteSensorProcess(processName, cleanerId);
		IGoal addProcess = createGoal("sim_add_environment_process");
		addProcess.getParameter("process").setValue(sensorProcess);
		dispatchSubgoalAndWait(addProcess);
	}
}
