package jadex.platform.service.processengine;

import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.runtime.exttask.ITaskExecutionService;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;

/**
 * 
 */
@Agent(autoprovide=true)
public class ActivityExecutionAgent implements ITaskExecutionService
{
	@Agent
	protected IInternalAccess agent;
	
//	/**
//	 * 
//	 */
//	public <T> IFuture<T> execute(Map<String, Object> args, IResultCommand<IFuture<T>, Map<String, Object>> activity)
//	{
//		return activity.execute(args);
//	}
	
	public IFuture<Void> execute(ITask task, ITaskContext context)
	{
		// todo return results
		return task.execute(context, agent);
	}
}
