package jadex.platform.service.execution;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.JavaThreadPool;
import jadex.commons.future.IFuture;
import jadex.platform.service.threadpool.ThreadPoolService;

/**
 *  Helper class to allow bisimulation in same VM.
 */
public class BisimExecutionService extends SyncExecutionService
{
	/** The shared execution service. */
	protected static volatile BisimExecutionService	instance;

	/** The inited flag of the service. */
	protected static IFuture<Void>	inited;

	/**
	 *  Get the instance.
	 */
	public static IExecutionService	getInstance(IInternalAccess provider)
	{
		IExecutionService	ret;
		synchronized(BisimExecutionService.class)
		{
			if(instance==null)
			{
				instance	= new BisimExecutionService(provider);
				inited	= instance.doInit();
				ret	= instance;
			}
			else
			{
				// Create wrapper for different sid (grrr).
				ret	= new BisimExecutionService(provider)
				{
					@Override
					public IExecutable[] getRunningTasks()
					{
						return instance.getRunningTasks();
					}
					
					@Override
					public IFuture<Void> getNextIdleFuture()
					{
						return instance.getNextIdleFuture();
					}
					
					@Override
					public void execute(IExecutable task)
					{
						instance.execute(task);
					}
					
					@Override
					public IFuture<Void> cancel(IExecutable task)
					{
						return instance.cancel(task);
					}
					
					@Override
					public IFuture<Boolean> isValid()
					{
						return instance.isValid();
					}
				};
			}
		}
		return ret;
	}
	
	/**
	 *  Create a new synchronous executor service. 
	 */
	public BisimExecutionService(IInternalAccess provider)
	{
		super(provider);
	}
	
	@Override
	public IFuture<Void> startService()
	{
		return inited;
	}
	
	/**
	 *  Do the actual init (called once).
	 */
	private IFuture<Void>	doInit()
	{
		return super.startService();
	}
	
	@Override
	public IFuture<Void> shutdownService()
	{
		// Hack!!! never shut down as might be reused later
		return IFuture.DONE;
	}
	
	@Override
	protected IThreadPoolService getThreadPool()
	{
		// Extra thread pool that never shuts down
		return new ThreadPoolService(new JavaThreadPool(false), getProviderId());
	}
}
