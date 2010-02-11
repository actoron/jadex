package jadex.bpmnbdi.task;

import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.ParameterMetaInfo;
import jadex.bpmn.runtime.task.TaskMetaInfo;
import jadex.commons.concurrent.IResultListener;

/**
 *  Create a task for a space object.
 */
public class RemoveSpaceObjectTaskTask	implements ITask
{
	/**
	 *  Execute the task.
	 *  @param context	The accessible values.
	 *  @param process	The process instance executing the task.
	 *  @param listener	To be notified, when the task has completed.
	 */
	public void	execute(ITaskContext context, BpmnInterpreter process, IResultListener listener)
	{
		IEnvironmentSpace	space	= (IEnvironmentSpace)context.getParameterValue("space");
		Object	objectid	= context.getParameterValue("objectid");
		Object	taskid	= context.getParameterValue("taskid");
		space.removeObjectTask(taskid, objectid);
	}
	
	//-------- static methods --------
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "The remove space object task task can be used to remove a task in an" +
			"EnvSupport environment.";
		
		ParameterMetaInfo spacemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			IEnvironmentSpace.class, "space", null, "The space parameter defines the space.");
		ParameterMetaInfo objectid = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Object.class, "objectid", null, "The objectid parameter for identifying the space object.");
		ParameterMetaInfo taskidmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_OUT, 
			Object.class, "taskid", null, "The taskid parameter for identifying the task.");

		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{spacemi, objectid, taskidmi}); 
	}
}
