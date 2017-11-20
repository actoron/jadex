package jadex.platform.service.threadpool;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
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
	 */
	public synchronized IFuture<Void>	shutdownService()
	{
//		System.err.println("Shutdown threadpool: "+this+", "+new Date());
		final Future<Void> ret = new Future<Void>();
		
		final Timer	t	= new Timer(true);
		long delay = Starter.getScaledLocalDefaultTimeout(getProviderId(), 1.0 / 3); // hack!!! hard coded to 1/3 of default timeout
		t.schedule(new TimerTask()
		{
			public void run()
			{
				System.out.println("Shutdown threadpool timeout: "+this+", "+new Date());
				// stop waiting for threadpool if still no notifaction
				ret.setResultIfUndone(null);
			}
		}, delay > -1 ? delay : 0);
		
		threadpool.addFinishListener(new IChangeListener<Void>()
		{
			public void changeOccurred(ChangeEvent<Void> event)
			{
				ThreadPoolService.super.shutdownService().addResultListener(new DelegationResultListener<Void>(ret, true)
				{
					public void customResultAvailable(Void result)
					{
						t.cancel();
//						System.err.println("Shutdown threadpool finished: "+this+", "+new Date());
						super.customResultAvailable(result);
					}
				});
			}
		});
		
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
