package jadex.platform.service.execution;

import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.Executor;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.util.Map;

/**
 *  The asynchronous executor service that executes all tasks in separate executors.
 */
public class AsyncExecutionService	extends BasicService implements IExecutionService
{
	//-------- attributes --------
	
	/** The threadpool. */
	protected IThreadPoolService threadpool;
	
	/** The currently waiting tasks (task->executor). */
	protected Map<IExecutable, Executor> executors;
		
	/** The idle future. */
	protected Future<Void> idlefuture;
	
	/** The state. */
	protected boolean running;
	
	/** The shutdown flag. */
	protected boolean shutdown;
	
	/** The provider. */
	protected IServiceProvider provider;
	
	/** The running (i.e. non-blocked) executors. */
	protected Map<IExecutable, Executor>	runningexes;
	
	//-------- constructors --------
	
	/**
	 *  Create a new asynchronous executor service. 
	 */
	public AsyncExecutionService(IServiceProvider provider)//, int max)
	{
		this(provider, null);
	}
	
	/**
	 *  Create a new asynchronous executor service. 
	 */
	public AsyncExecutionService(IServiceProvider provider, Map<String, Object> properties)//, int max)
	{
		super(provider.getId(), IExecutionService.class, properties);

		this.provider = provider;
		this.running	= false;
		this.executors	= SCollection.createHashMap();
		this.runningexes	= SCollection.createHashMap();
	}

	//-------- methods --------
	
	/**
	 *  Execute a task in its own thread.
	 *  @param task The task to execute.
	 *  (called from arbitrary threads)
	 */
	public synchronized void execute(final IExecutable task)
	{	
		if(!customIsValid())
			throw new RuntimeException("Not running: "+task);
		
		if(shutdown)
			throw new RuntimeException("Shutting down: "+task);
		
		Executor exe = executors.get(task);

		if(exe==null)
		{
			exe = new Executor(threadpool, task)
			{
				// Hack!!! overwritten to know, when executor ends.
				public void run()
				{
					synchronized(AsyncExecutionService.this)
					{
						runningexes.put(task, this);
					}
					super.run();
					
					Future<Void> idf = null;
					
					synchronized(AsyncExecutionService.this)
					{
						synchronized(this)
						{
							// Do not remove when a new executor has already been added for the task.
							// isRunning() refers to running state of executor!
							if(!this.isRunning() && executors!=null && executors.get(task)==this)	
							{
								if(executors!=null && this.getThreadCount()==0)
								{
									executors.remove(task);
								}
								runningexes.remove(task);
								
								idf	= checkIdleFuture();
							}
							else if(executors!=null && executors.get(task)!=this)
							{
								runningexes.remove(task);								
							}
						}
					}
					
					if(idf!=null)
					{
						idf.setResult(null);
					}
				}					
			};
			executors.put(task, exe);
		}
	
		if(running)
		{
			synchronized(this)
			{
				boolean	exec	= exe.execute();
				if(exec)
				{
					runningexes.put(task, exe);
				}
			}
		}
	}
	
	/**
	 *  Cancel a task. Triggers the task to
	 *  be not executed in future. 
	 *  @param task The task to execute.
	 *  @param listener The listener.
	 */
	public synchronized IFuture<Void> cancel(final IExecutable task)
	{
		// todo: repair me: problem is that method can interfere with execute?!
		final Future<Void> ret = new Future<Void>();
		
		if(!customIsValid())
		{
			ret.setException(new RuntimeException("Shutting down."));
		}
		else
		{
			final Executor exe = (Executor)executors.get(task);
			if(exe!=null)
			{
				IResultListener<Void> lis = new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						// todo: do not call listener with holding lock
						synchronized(AsyncExecutionService.this)
						{
							ret.setResult(result);
							if(executors!=null)	
							{
								executors.remove(task);
							}
						}
					}
	
					public void exceptionOccurred(Exception exception)
					{
						// todo: do not call future with holding lock
						synchronized(AsyncExecutionService.this)
						{
							ret.setException(exception);
							if(executors!=null)	
							{
								executors.remove(task);
							}
						}
					}
				};
				exe.shutdown().addResultListener(lis);
			}
			else
			{
				ret.setResult(null);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get the currently running tasks.
	 */
	public synchronized IExecutable[]	getRunningTasks()
	{
		return (IExecutable[])runningexes.keySet().toArray(new IExecutable[runningexes.size()]);
	}
	
	/**
	 *  Start the execution service.
	 *  Resumes all scheduled tasks. 
	 */
	public synchronized IFuture<Void>	startService()
	{
		final  Future<Void> ret = new Future<Void>();
		
		super.startService().addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				if(shutdown)
				{
					ret.setException(new RuntimeException("Cannot start: shutdowning service."));
				}
				else
				{
					SServiceProvider.getServiceUpwards(provider, IThreadPoolService.class)
						.addResultListener(new IResultListener<IThreadPoolService>()
					{
						public void resultAvailable(IThreadPoolService result)
						{
							try
							{
								threadpool = result;
								
								running	= true;
								
								if(!executors.isEmpty())
								{
									// Resume all suspended tasks.
									IExecutable[] keys = (IExecutable[])executors.keySet()
										.toArray(new IExecutable[executors.size()]);
									for(int i=0; i<keys.length; i++)
										execute(keys[i]);
								}
								else
								{
									Future<Void> idf = null;
									synchronized(AsyncExecutionService.this)
									{
										idf = idlefuture;
										idlefuture = null;
									}
									if(idf!=null)
										idf.setResult(null);
								}
								ret.setResult(null);
							}
							catch(RuntimeException e)
							{
								ret.setException(e);
							}
						}
						
						public void exceptionOccurred(Exception exception)
						{
							ret.setException(exception);
						}
					});
				}
			}
		});
		
		return ret;
	}
	
	/**
	 *  Shutdown the executor service.
	 *  // todo: make callable more than once
	 */
	public synchronized IFuture<Void>	shutdownService()
	{
		final Future<Void> ret	= new Future<Void>();
		
		super.shutdownService().addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				if(shutdown)
				{
					ret.setException((new RuntimeException("Already shutdowned.")));
				}
				else
				{
					shutdown = true;
					
					IExecutable[] keys = (IExecutable[])executors.keySet()
						.toArray(new IExecutable[executors.size()]);
					
					if(keys.length>0)
					{
						// One listener counts until all executors have shutdowned.
						IResultListener<Void> lis = new CounterResultListener<Void>(keys.length, new DelegationResultListener<Void>(ret));
						for(int i=0; i<keys.length; i++)
						{
							Executor exe = (Executor)executors.get(keys[i]);
							if(exe!=null)
							{
								exe.shutdown().addResultListener(lis);
							}
							else
							{
								lis.resultAvailable(null);
							}
						}
					}
					else
					{
						ret.setResult(null);
					}
					executors = null;
				}
			}
		});
		
		return ret;
	}

	/**
	 *  Test if the service is valid.
	 *  @return True, if service can be used.
	 */
	public synchronized boolean customIsValid()
	{
		return running && !shutdown;
	}
	
	/**
	 *  Get the future indicating that executor is idle.
	 */
	public synchronized IFuture<Void> getNextIdleFuture()
	{
		Future<Void> ret;
		if(shutdown)
		{
			ret = new Future<Void>(new RuntimeException("Shutdown"));
		}
		else
		{
			if(idlefuture==null)
				idlefuture = new Future<Void>();
			ret = idlefuture;
		}
		return ret;
	}

	//-------- helper methods --------
	
	/**
	 *  Get the idle future if any and if service is idle.
	 *  Must be called while holding service lock.
	 *  @return	An idle future to be notified (if any) after releasing service lock.
	 */
	protected Future<Void>	checkIdleFuture()
	{
		Future<Void>	ret	= null;

		// When no more executable threads, inform idle commands.				
		if(AsyncExecutionService.this.running && idlefuture!=null && runningexes.isEmpty())
		{
			ret = idlefuture;
			idlefuture = null;
		}
		
		return ret;
	}
}
