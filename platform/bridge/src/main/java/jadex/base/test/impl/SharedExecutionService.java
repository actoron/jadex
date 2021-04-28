package jadex.base.test.impl;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.future.IFuture;

/**
 *  Helper class to allow sharing an execution service across platforms in same VM.
 */
public class SharedExecutionService	extends SharedService<IExecutionService>	implements IExecutionService
{
	/**
	 *  Get the instance.
	 */
	public  SharedExecutionService(IComponentIdentifier provider, SharedServiceFactory<IExecutionService> factory)
	{
		super(provider, IExecutionService.class, factory);
	}
	
	//-------- IExecutionService interface --------
		
	@Override
	public IExecutable[] getRunningTasks()
	{
		return getInstance().getRunningTasks();
	}
	
	@Override
	public IFuture<Void> getNextIdleFuture()
	{
		return getInstance().getNextIdleFuture();
	}
	
	@Override
	public void execute(IExecutable task)
	{
		getInstance().execute(task);
	}
	
	@Override
	public IFuture<Void> cancel(IExecutable task)
	{
		return getInstance().cancel(task);
	}
}
