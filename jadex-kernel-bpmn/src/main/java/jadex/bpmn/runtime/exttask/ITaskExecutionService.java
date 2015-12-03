package jadex.bpmn.runtime.exttask;

import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 * 
 */
@Service
public interface ITaskExecutionService
{
//	/**
//	 * 
//	 */
//	public <T> IFuture<T> execute(IResultCommand<T, Map<String, Object>> activity);
	
//	/**
//	 * 
//	 */
//	public <T> IFuture<T> execute(Map<String, Object> args, Class<? extends IResultCommand<T, Map<String, Object>>> activity);

//	/**
//	 * 
//	 */
//	public IFuture<Map<String, Object>> execute(IExternalTask task);
	public IFuture<Void> execute(ITask task, ITaskContext context);
	
//	/**
//	 * 
//	 */
//	public <T> IFuture<T> execute(Map<String, Object> args, IResultCommand<IFuture<T>, Map<String, Object>> activity);


	
}
