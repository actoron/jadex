package jadex.bridge.service.types.threadpool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

import jadex.commons.SUtil;
import jadex.commons.concurrent.IThreadPool;

/**
 *  
 *  Provides access to the Jadex thread pools as a Java ExecutorService.
 *
 */
public class JadexExecutorServiceAdapter extends AbstractExecutorService
{
	/** The Jadex thread pool. */
	protected IThreadPool threadpool;
	
	/** Current threads. */
	protected List<Thread> currentthreads;
	
	/**
	 *  Creates the adapter.
	 *  
	 *  @param threadpool The Jadex thread pool to use.
	 */
	public JadexExecutorServiceAdapter(IThreadPool threadpool)
	{
		this.threadpool = threadpool;
		this.currentthreads = Collections.synchronizedList(new ArrayList<Thread>());
	}

	/**
	 *  Override.
	 */
	public void shutdown()
	{
		threadpool = null;
	}

	/**
	 *  Override.
	 */
	public List<Runnable> shutdownNow()
	{
		threadpool = null;
		synchronized(currentthreads)
		{
			while (currentthreads.size() > 0)
			{
				Thread t = currentthreads.remove(currentthreads.size() - 1);
				t.interrupt();
			}
		}
		return new ArrayList<Runnable>();
	}

	/**
	 *  Override.
	 */
	public boolean isShutdown()
	{
		return threadpool == null;
	}

	/**
	 *  Override.
	 */
	public boolean isTerminated()
	{
		return threadpool == null;
	}

	/**
	 *  Override.
	 */
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException
	{
		SUtil.sleep(unit.toMillis(timeout));
		return false;
	}

	/**
	 *  Override.
	 */
	public void execute(final Runnable command)
	{
		threadpool.execute(new Runnable()
		{
			public void run()
			{
				currentthreads.add(Thread.currentThread());
				command.run();
				currentthreads.remove(Thread.currentThread());
			}
		});
	}


}
