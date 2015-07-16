package jadex.platform.service.threadpool;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.TimeoutResultListener;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Service wrapper for a threadpool.
 */
public class ThreadPoolService extends BasicService implements IThreadPoolService, IDaemonThreadPoolService
{
	//-------- attributes --------
	
	/** The threadpool. */
	protected IThreadPool threadpool;
	
	//-------- constructors -------- 
	
	/**
	 *  Create a new threadpool service.
	 */
	public ThreadPoolService(IThreadPool threadpool, IComponentIdentifier provider)
	{
		super(provider, IThreadPoolService.class, null);

		this.threadpool = threadpool;
	}
	
	//-------- methods -------- 

	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 * /
	public synchronized IFuture	startService()
	{
		return super.startService();
	}*/
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public synchronized IFuture<Void>	shutdownService()
	{
//		System.out.println("start fini: "+threadpool+" "+Thread.currentThread());
		final Future<Void> ret = new Future<Void>();
		threadpool.addFinishListener(new IChangeListener<Void>()
		{
			public void changeOccurred(ChangeEvent<Void> event)
			{
//				System.out.println("end fini: "+threadpool+" "+Thread.currentThread());
				ThreadPoolService.super.shutdownService().addResultListener(new DelegationResultListener<Void>(ret, true));
			}
		});
		
		getInternalAccess().getExternalAccess().scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				// terminate waiting for threadpool if still no notifaction
				ret.setResultIfUndone(null);
				return IFuture.DONE;
			}
		}, 8000);
		
		threadpool.dispose();
		return ret;
	}
	
	/**
	 *  Execute a task in its own thread.
	 *  @param task The task to execute.
	 */
	public synchronized void execute(Runnable task)
	{
//		if(!isValid())
//			throw new RuntimeException("Service invalid: "+this);
		
		threadpool.execute(task);
	}
	
	/**
	 *  Shutdown the thread pool.
	 */
	public void dispose()
	{
		shutdownService();
	}
	
	/**
	 *  Test if the thread pool is running.
	 */
	public boolean	isRunning()
	{
		return threadpool.isRunning();
	}
	
	/**
	 *  Add a finished listener.
	 */
	public void addFinishListener(IChangeListener<Void> listener)
	{
		threadpool.addFinishListener(listener);
	}
}
