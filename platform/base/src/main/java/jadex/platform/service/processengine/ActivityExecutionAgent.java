package jadex.platform.service.processengine;

import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.runtime.exttask.ITaskExecutionService;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.commons.Boolean3;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;

/**
 * 
 */
@Agent(autoprovide=Boolean3.TRUE)
@Service
public class ActivityExecutionAgent implements ITaskExecutionService
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 * 
	 */
	public IFuture<Void> execute(ITask task, ITaskContext context)
	{
		// todo return results
		return task.execute(context, agent);
	}
}
