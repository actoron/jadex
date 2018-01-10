package jadex.platform.service.execution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.Executor;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  The synchronous execution service that executes all tasks in zero to one thread.
 */
public class SyncExecutionService extends BasicService implements IExecutionService
{
	/**
	 *  The possible states of the service.
	 */
	public enum State
	{
		CREATED, RUNNING, SHUTDOWN
	}
		
	//-------- attributes --------
	
	/** The queue of tasks to be executed. */
	protected Set<IExecutable> queue;
	
	/** The executor. */
	protected Executor executor;
	
	/** The idle future. */
	protected Future<Void> idlefuture;
	
	/** The state of the service. */
	protected State state;

	/** The current task. */
	protected IExecutable task;
	
	/** Flag that indicates that the current task has been removed. */
	protected IExecutable removedtask;
	
	/** The removed listeners. */
	protected List<Future<Void>> removedfut;
	
	/** The provider. */
	protected IInternalAccess provider;
	
	//-------- constructors --------
	
	/**
	 *  Create a new synchronous executor service. 
	 */
	public SyncExecutionService(IInternalAccess provider)
	{
		this(provider, null);
	}
	
	/**
	 *  Create a new synchronous executor service. 
	 */
	public SyncExecutionService(IInternalAccess provider, Map<String, Object> properties)
	{
		super(provider.getComponentIdentifier(), IExecutionService.class, properties);

		this.provider = provider;
		this.state	= State.CREATED;
		this.queue	= SCollection.createLinkedHashSet();
		this.removedfut = new ArrayList<Future<Void>>();
	}

	//-------- methods --------
	
	/**
	 *  Execute a task. Triggers the task to
	 *  be executed in future. 
	 *  @param task The task to execute.
	 */
	public void execute(IExecutable task)
	{
		if(state==State.SHUTDOWN)
			return;

//		if(DEBUG)
//			System.out.println("execute called: "+task+", "+this.task);
		boolean	added;
		synchronized(this)
		{
//			new RuntimeException().printStackTrace(System.out);
			added = queue.add(task);
//			if(DEBUG)
//				System.out.println("Task added: "+queue+", "+this.task);
		}
		
		// On change, wake up the main executor for executing tasks
		if(added)
		{
			if(state==State.RUNNING) 
				executor.execute();
		}
	}
	
	/**
	 *  Cancel a task. Triggers the task to
	 *  be not executed in future. 
	 *  @param task The task to execute.
	 */
	public synchronized IFuture<Void> cancel(IExecutable task)
	{
		Future<Void> ret = new Future<Void>();
		
		if(!customIsValid())
		{
			ret.setException(new RuntimeException("Shutting down."));
		}
		else
		{
			// Remove from scheduled tasks.
			synchronized(this)
			{
				// Is current task removed
				if(this.task == task)
				{
					removedtask = task;
					removedfut.add(ret);
				}
				else
				{
					queue.remove(task);
					ret.setResult(null);
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get the currently running or waiting tasks.
	 */
	public synchronized IExecutable[]	getRunningTasks()
	{
		return (IExecutable[])queue.toArray(new IExecutable[queue.size()]);
	}
	
	/**
	 *  Test if the service is valid.
	 *  @return True, if service can be used.
	 */
	public boolean customIsValid()
	{
		return state==State.RUNNING;
	}

	/**
	 *  Start the executor service.
	 *  Resumes all tasks.
	 */
	public synchronized IFuture<Void>	startService()
	{	
		final  Future<Void> ret = new Future<Void>();
	
		if(state==State.SHUTDOWN)
		{
			ret.setResult(null);
//			ret.setResult(getServiceIdentifier());
			return ret;
		}
		
		super.startService().addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				SServiceProvider.getService(provider, IThreadPoolService.class, RequiredServiceInfo.SCOPE_PLATFORM, false)
					.addResultListener(new IResultListener<IThreadPoolService>()
				{
					public void resultAvailable(IThreadPoolService result)
					{
						executor = new Executor(result, new IExecutable()
						{
							public boolean execute()
							{
								// Perform one task a time.
								
								// assert task==null;
								synchronized(SyncExecutionService.this)
								{
									if(state==State.RUNNING && !queue.isEmpty())
									{
										// Hack!!! Is there a better way to get first element from queue without creating iterator?
										Iterator<IExecutable> iterator = queue.iterator();
										task = iterator.next();
										iterator.remove();
									}
								}
								
								boolean again = false;
								if(task!=null)
								{
									try
									{
//										if(DEBUG)
//											System.out.println("Executing task: "+task);
										again = task.execute();
									}
									catch(Throwable e)
									{
										System.out.println("Exception during executing task: "+task);
										e.printStackTrace();
									}									
								}

								Future<Void> idf = null;
								List<Future<Void>>	remfuts	= null;
								synchronized(SyncExecutionService.this)
								{
									if(removedtask==null)
									{
										if(again && state==State.RUNNING)
										{
											queue.add(task);
										}
									}
									else if(removedtask==task)
									{
										removedtask = null;
										remfuts	= new ArrayList<Future<Void>>(removedfut);
										removedfut.clear();
									}
									else
									{
										throw new RuntimeException("Removedtask!=task: "+task+" "+removedtask);
									}
									
									task = null;
//									System.out.println("task finished: "+running+", "+queue.isEmpty());
									if(state==State.RUNNING && queue.isEmpty())
									{
										idf = idlefuture;
										idlefuture = null;
		//								perform = idlefuture!=null;
									}

									// Perform next task when queue is not empty and service is running.
									again	= state==State.RUNNING && !queue.isEmpty();
								}

								
								// When no more executables, inform idle commands.
								if(idf!=null)
								{
//									System.out.println("Idle");
									idf.setResult(null);
		//							Iterator it	= idlecommands.iterator();
		//							while(it.hasNext())
		//							{
		//								((ICommand)it.next()).execute(null);
		//							}
								}
								if(remfuts!=null)
								{
									for(int i=0; i<remfuts.size(); i++)
										remfuts.get(i).setResult(null);									
								}
								
								return again;
							}
						});
						
						state	= State.RUNNING;
						ret.setResult(null);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						ret.setException(exception);
					}
				});
			}
		});
		
		return ret;
	}

	/**
	 *  Shutdown the executor service.
	 */
	public IFuture<Void>	shutdownService()
	{
		final Future<Void> ret = new Future<Void>();
		
		super.shutdownService().addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				Future<Void> idf = null;
				
				synchronized(this)
				{
					if(!customIsValid())
					{
						ret.setException(new RuntimeException("Not running."));
					}
					else
					{
						state	= State.SHUTDOWN;
						executor.shutdown().addResultListener(new DelegationResultListener<Void>(ret));
						queue = null;
						idf = idlefuture;
					}
				}
				
				if(idf!=null)
					idf.setException(new RuntimeException("Shutdown"));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the future indicating that executor is idle.
	 */
	public synchronized IFuture<Void> getNextIdleFuture()
	{
		Future<Void> ret;
		if(state==State.SHUTDOWN)
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
}
