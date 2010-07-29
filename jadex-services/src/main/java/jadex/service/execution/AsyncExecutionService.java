package jadex.service.execution;

import jadex.commons.Future;
import jadex.commons.ICommand;
import jadex.commons.IFuture;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.CounterResultListener;
import jadex.commons.concurrent.Executor;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.concurrent.IThreadPool;
import jadex.service.BasicService;
import jadex.service.IServiceProvider;
import jadex.service.SServiceProvider;
import jadex.service.threadpool.ThreadPoolService;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *  The asynchronous executor service that executes all tasks in separate executors.
 */
public class AsyncExecutionService	extends BasicService implements IExecutionService
{
	//-------- attributes --------
	
	/** The threadpool. */
	protected IThreadPool threadpool;
	
	/** The currently waiting tasks. */
	protected Map executors;
		
	/** The idle commands. */
	protected Set idlecommands;
	
	/** The state. */
	protected boolean running;
	
	/** The shutdown flag. */
	protected boolean shutdown;
	
	/** The provider. */
	protected IServiceProvider provider;
	
	/** The executor cache. */
//	protected List executorcache;
	
	/** The maximum number of cached executors. */
//	protected int max;
	
	//-------- constructors --------
	
	/**
	 *  Create a new asynchronous executor service. 
	 * /
	public AsyncExecutorService(IThreadPool threadpool)
	{
		this(threadpool, 100);
	}*/
	
	/**
	 *  Create a new asynchronous executor service. 
	 */
	public AsyncExecutionService(IServiceProvider provider)//, int max)
	{
		super(BasicService.createServiceIdentifier(provider.getId(), AsyncExecutionService.class));

		this.provider = provider;
//		this.threadpool = threadpool;
//		this.max = max;
		this.running	= false;
		this.executors	= SCollection.createHashMap();
//		this.executors	= SCollection.createWeakHashMap();
//		this.executorcache = SCollection.createArrayList();
	}

	//-------- methods --------
	
	/**
	 *  Execute a task in its own thread.
	 *  @param task The task to execute.
	 *  (called from arbitrary threads)
	 */
	public synchronized void execute(final IExecutable task)
	{	
		//System.out.println("execute called: "+task);
		if(shutdown)
			throw new RuntimeException("Shutting down: "+task);
		
		Executor exe = (Executor)executors.get(task);

		if(exe==null)
		{
//			System.out.println("Created executor for:"+task);
//			if(executorcache.size()>0)
//			{
//				exe = (Executor)executorcache.remove(0);
//				exe.setExecutable(task);
//			}
//			else
//			{
				exe = new Executor(threadpool, task)
				{
					// Hack!!! overwritten to know, when executor ends.
					public void run()
					{
						super.run();
						
						synchronized(AsyncExecutionService.this)
						{
							synchronized(this)
							{
								// isRunning() refers to running state of executor!
								// Do not remove when a new executor has already been added for the task.
								if(!this.isRunning() && executors!=null && executors.get(task)==this)	
								{
//									System.out.println("Removing executor for: "+task+", "+this);
									executors.remove(task); // weak for testing
//									setExecutable(null);
//									if(executorcache.size()<max)
//										executorcache.add(this);
			
									// When no more executables, inform idle commands.				
									if(AsyncExecutionService.this.running && idlecommands!=null && executors.isEmpty())
									{
//										System.out.println("idle");
										Iterator it	= idlecommands.iterator();
										while(it.hasNext())
										{
											((ICommand)it.next()).execute(null);
										}
									}
//									else if(AsyncExecutionService.this.running && idlecommands!=null)
//									{
//										System.out.println("not idle: "+executors);										
//									}
								}
							}
						}
					}					
				};
//			}
			executors.put(task, exe);
		}
	
		if(running)
		{
//			System.out.println("Executing for: "+task+", "+exe);
			exe.execute();
		}
	}
	
	/**
	 *  Cancel a task. Triggers the task to
	 *  be not executed in future. 
	 *  @param task The task to execute.
	 *  @param listener The listener.
	 */
	public IFuture cancel(final IExecutable task)
	{
		// todo: repair me: problem is that method can interfere with execute?!
		final Future ret = new Future();
		
		if(!isValid())
		{
			ret.setException(new RuntimeException("Shutting down."));
		}
		else
		{
			Executor exe = (Executor)executors.get(task);
			if(exe!=null)
			{
				IResultListener lis = new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						// todo: do not call listener with holding lock
						synchronized(AsyncExecutionService.this)
						{
							ret.setResult(result);
							
							if(executors!=null)
								executors.remove(task);
						}
					}
	
					public void exceptionOccurred(Object source, Exception exception)
					{
						// todo: do not call future with holding lock
						synchronized(AsyncExecutionService.this)
						{
							ret.setResult(exception);
							
							if(executors!=null)
								executors.remove(task);
						}
						
					}
				};
				exe.shutdown().addResultListener(lis);
	//			executors.remove(task);
			}
			else
			{
				ret.setResult(null);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Start the execution service.
	 *  Resumes all scheduled tasks. 
	 */
	public synchronized IFuture	startService()
	{
		final  Future ret = new Future();
		
		if(shutdown)
		{
			ret.setException(new RuntimeException("Cannot start: shutdowning service."));
		}
		else
		{
			SServiceProvider.getService(provider, ThreadPoolService.class).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					threadpool = (IThreadPool)result;
					
					running	= true;
					
					if(!executors.isEmpty())
					{
						// Resume all suspended tasks.
						IExecutable[] keys = (IExecutable[])executors.keySet()
							.toArray(new IExecutable[executors.size()]);
						for(int i=0; i<keys.length; i++)
							execute(keys[i]);
					}
					else if(idlecommands!=null)
					{
			//			System.out.println("restart: idle");
						Iterator it	= idlecommands.iterator();
						while(it.hasNext())
						{
							((ICommand)it.next()).execute(null);
						}
					}
					
					ret.setResult(null);
				}
				
				public void exceptionOccurred(Object source, Exception exception)
				{
					ret.setException(exception);
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Shutdown the executor service.
	 *  // todo: make callable more than once
	 */
	public synchronized IFuture	shutdownService()
	{
		final Future ret	= new Future();
		
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
				IResultListener lis = new CounterResultListener(keys.length)
				{
					public void finalResultAvailable(Object source, Object result)
					{
						ret.setResult(result);
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						ret.setException(exception);
					}
				};
				
				for(int i=0; i<keys.length; i++)
				{
					Executor exe = (Executor)executors.get(keys[i]);
					if(exe!=null)
						exe.shutdown().addResultListener(lis);
				}
			}
			else
			{
				ret.setResult(null);
			}
		}
		
		executors = null;
		return ret;
	}

	/**
	 *  Test if the service is valid.
	 *  @return True, if service can be used.
	 */
	public synchronized boolean isValid()
	{
		return running && !shutdown;
	}
	
	/**
	 *  Test if the executor is currently idle.
	 */
	public synchronized boolean	isIdle()
	{
		return running && !executors.isEmpty();
	}
	
	/**
	 *  Add a command to be executed whenever the executor
	 *  is idle (i.e. no executables running).
	 */
	public void addIdleCommand(ICommand command)
	{
		if(idlecommands==null)
		{
			synchronized(this)
			{
				if(idlecommands==null)
				{
					idlecommands	= SCollection.createLinkedHashSet();
				}
			}
		}
		
		idlecommands.add(command);
	}

	/**
	 *  Remove a previously added idle command.
	 */
	public synchronized void removeIdleCommand(ICommand command)
	{
		if(idlecommands!=null)
			idlecommands.remove(command);
	}

}

