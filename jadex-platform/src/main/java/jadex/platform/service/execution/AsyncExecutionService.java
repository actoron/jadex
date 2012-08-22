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
//	//-------- static part --------
//	
//	public static MultiCollection	DEBUG	= new MultiCollection();
	
	//-------- attributes --------
	
	/** The threadpool. */
	protected IThreadPoolService threadpool;
	
	/** The currently waiting tasks (task->executor). */
	protected Map<IExecutable, Executor> executors;
		
	/** The idle commands. */
//	protected Set idlecommands;
	protected Future<Void> idlefuture;
	
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
		this(provider, null);
	}
	
	/**
	 *  Create a new asynchronous executor service. 
	 */
	public AsyncExecutionService(IServiceProvider provider, Map<String, Object> properties)//, int max)
	{
		super(provider.getId(), IExecutionService.class, properties);

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
//		synchronized(DEBUG)
//		{
//			DEBUG.put(task, "execute called");
//		}
//		System.out.println("execute called: "+task);
		if(!customIsValid())
			throw new RuntimeException("Not running: "+task);
		
		if(shutdown)
			throw new RuntimeException("Shutting down: "+task);
		
		Executor exe = executors.get(task);

		if(exe==null)
		{
//			synchronized(DEBUG)
//			{
//				DEBUG.put(task, "creating executor");
//			}
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
//					// Hack!!! overwritten for debugging.
//					protected boolean code()
//					{
//						synchronized(DEBUG)
//						{
//							DEBUG.put(task, "executing code(): "+this);
//						}
//						boolean	ret	= super.code();
//						synchronized(DEBUG)
//						{						
//							DEBUG.put(task, "code() finished: "+this);
//						}
//						return ret;
//					}
					
					// Hack!!! overwritten to know, when executor ends.
					public void run()
					{
						super.run();
						
//						ICommand[]	commands	= null;
						Future<Void> idf = null;
						
						synchronized(AsyncExecutionService.this)
						{
							synchronized(this)
							{
								// isRunning() refers to running state of executor!
								// Do not remove when a new executor has already been added for the task.
								if(!this.isRunning() && executors!=null && executors.get(task)==this)	
								{
									idf	= removeTask(task);
								}
							}
						}
						
						if(idf!=null)
							idf.setResult(null);
						
//						for(int i=0; commands!=null && i<commands.length; i++)
//						{
//							commands[i].execute(null);
//						}
					}					
				};
//			}
//			synchronized(DEBUG)
//			{
//				DEBUG.put(task, "new executor: "+exe);
//			}
			executors.put(task, exe);
		}
	
		if(running)
		{
//			synchronized(DEBUG)
//			{
//				DEBUG.put(task, "calling execute(): "+exe);
//			}
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
						Future<Void>	idf;
						// todo: do not call listener with holding lock
						synchronized(AsyncExecutionService.this)
						{
							ret.setResult(result);
							idf	= removeTask(task);
						}
						if(idf!=null)
							idf.setResult(null);
					}
	
					public void exceptionOccurred(Exception exception)
					{
						Future<Void>	idf;
						// todo: do not call future with holding lock
						synchronized(AsyncExecutionService.this)
						{
							ret.setException(exception);
							idf	= removeTask(task);
						}
						if(idf!=null)
							idf.setResult(null);
						
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
	 *  Get the currently running or waiting tasks.
	 */
	public synchronized IExecutable[]	getTasks()
	{
		return (IExecutable[])executors.keySet().toArray(new IExecutable[executors.size()]);
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
//								new RuntimeException("AsyncExecustionService.start()").printStackTrace();
								
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
//								else if(idlecommands!=null)
//								{
//						//			System.out.println("restart: idle");
//									Iterator it	= idlecommands.iterator();
//									while(it.hasNext())
//									{
//										((ICommand)it.next()).execute(null);
//									}
//								}
								
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
//								ret.setResult(getServiceIdentifier());
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
								exe.shutdown().addResultListener(lis);
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
	
//	/**
//	 *  Test if the executor is currently idle.
//	 */
//	public synchronized boolean	isIdle()
//	{
//		return running && !executors.isEmpty();
//		// todo: should be isEmpty()?
////		return running && executors.isEmpty();
//	}
	
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
	
//	/**
//	 *  Add a command to be executed whenever the executor
//	 *  is idle (i.e. no executables running).
//	 */
//	public void addIdleCommand(ICommand command)
//	{
//		if(idlecommands==null)
//		{
//			synchronized(this)
//			{
//				if(idlecommands==null)
//				{
//					idlecommands	= SCollection.createLinkedHashSet();
//				}
//			}
//		}
//		
//		idlecommands.add(command);
//	}
//
//	/**
//	 *  Remove a previously added idle command.
//	 */
//	public synchronized void removeIdleCommand(ICommand command)
//	{
//		if(idlecommands!=null)
//			idlecommands.remove(command);
//	}

	//-------- helper methods --------
	
	/**
	 *  Remove a task from the execution queue.
	 *  Must be called while holding service lock.
	 *  @return	An idle future to be notified (if any) after releasing service lock.
	 */
	protected Future<Void>	removeTask(IExecutable task)
	{
		Future<Void>	ret	= null;
		
//		System.out.println("Removing executor for: "+task+", "+this);
		if(executors!=null)
		{
			executors.remove(task); // weak for testing
	//		setExecutable(null);
	//		if(executorcache.size()<max)
	//			executorcache.add(this);
	
			// When no more executables, inform idle commands.				
			if(AsyncExecutionService.this.running && idlefuture!=null && executors.isEmpty())
			{
	//			System.out.println("idle");
	//			commands	= (ICommand[])idlecommands.toArray(new ICommand[idlecommands.size()]);
				ret = idlefuture;
				idlefuture = null;
			}
	//		else if(AsyncExecutionService.this.running && idlecommands!=null)
	//		{
	//			System.out.println("not idle: "+executors+", "+idlefuture+", "+AsyncExecutionService.this.running);										
	//		}
		}
		
		return ret;
	}
}

