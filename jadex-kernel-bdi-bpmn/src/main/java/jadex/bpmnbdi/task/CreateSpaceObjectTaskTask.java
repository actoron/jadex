package jadex.bpmnbdi.task;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.bpmn.runtime.BpmnInstance;
import jadex.bpmn.runtime.IProcessInstance;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.concurrent.IResultListener;

import java.util.Map;

/**
 *  Create a task for a space object.
 */
public class CreateSpaceObjectTaskTask	implements	ITask
{
	/**
	 *  Execute the task.
	 */
	public void execute(ITaskContext context, IProcessInstance instance, IResultListener listener)
	{
		try
		{
			String type	= (String)context.getParameterValue("type");
			IEnvironmentSpace	space	= (IEnvironmentSpace)context.getParameterValue("space");
			Object	objectid	= context.getParameterValue("objectid");
			Map	properties	= context.hasParameterValue("properties")
				? (Map)context.getParameterValue("properties") : null;
			
			Object	taskid	= space.createObjectTask(type, properties, objectid);
			
			if(context.hasParameterValue("taskid"))
				context.setParameterValue("taskid", taskid);
			
			if(context.hasParameterValue("contextvar"))
				((BpmnInstance)instance).setContextVariable((String)context.getParameterValue("contextvar"), taskid);

			boolean	wait	= context.hasParameterValue("wait")
				? ((Boolean)context.getParameterValue("wait")).booleanValue() : true;
			if(wait)
			{
				space.addTaskListener(taskid, objectid, listener);
			}
			else
			{
				listener.resultAvailable(null);
			}
		}
		catch(Exception e)
		{
			listener.exceptionOccurred(e);
		}
	}
}
