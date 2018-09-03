package jadex.platform.service.execution;

import java.util.concurrent.atomic.AtomicInteger;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.commons.future.IFuture;

/**
 *  Helper class to allow bisimulation in same VM.
 */
public class BisimExecutionService extends SyncExecutionService
{
	/** The shared execution service. */
	protected static volatile IExecutionService	instance;
	
	/**
	 *  Get the instance.
	 */
	public static IExecutionService	getInstance(IInternalAccess provider)
	{
		if(instance==null)
		{
			instance	= new BisimExecutionService(provider);
		}
		return instance;
	}
	
	AtomicInteger	users	= new AtomicInteger(0);
	
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
		if(users.incrementAndGet()==1)
		{
			return super.startService();
		}
		else
		{
			return IFuture.DONE;
		}
	}
	
	@Override
	public IFuture<Void> shutdownService()
	{
		if(users.decrementAndGet()==1)
		{
			// Todo: synchronize!?
			if(instance==this)
				instance	= null;
			
			return super.shutdownService();
		}
		else
		{
			return IFuture.DONE;
		}
	}
}
