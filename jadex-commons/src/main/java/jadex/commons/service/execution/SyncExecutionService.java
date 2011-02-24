package jadex.commons.service.execution;

import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.Executor;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.service.BasicService;
import jadex.commons.service.IServiceProvider;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.threadpool.IThreadPoolService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  The synchronous execution service that executes all tasks in zero to one thread.
 */
public class SyncExecutionService extends BasicService implements IExecutionService
{
	//-------- attributes --------
	
	/** The queue of tasks to be executed. */
	protected Set queue;
	
	/** The executor. */
	protected Executor executor;
	
//	/** The idle commands. */
//	protected Set	idlecommands;
	protected Future idlefuture;
	
	/** Flag, indicating if executor is running. */
	protected boolean running;

	/** The shutdown flag. */
	protected boolean shutdown;
	
	/** The current task. */
	protected IExecutable task;
	
	/** Flag that indicates that the current task has been removed. */
	protected IExecutable removedtask;
	
	/** The removed listeners. */
	protected List removedfut;
	
	/** The stop listener. */
	protected IResultListener stoplistener;
	
	/** The provider. */
	protected IServiceProvider provider;
	
	//-------- constructors --------
	
	/**
	 *  Create a new synchronous executor service. 
	 */
	public SyncExecutionService(IServiceProvider provider)
	{
		this(provider, null);
	}
	
	/**
	 *  Create a new synchronous executor service. 
	 */
	public SyncExecutionService(IServiceProvider provider, Map properties)
	{
		super(provider.getId(), IExecutionService.class, properties);

		this.provider = provider;
		this.running	= false;
		this.queue	= SCollection.createLinkedHashSet();
		this.removedfut = new ArrayList();
	}

	//-------- methods --------
	
	/**
	 *  Execute a task. Triggers the task to
	 *  be executed in future. 
	 *  @param task The task to execute.
	 */
	public void execute(IExecutable task)
	{
		if(shutdown)
			return;

//		System.out.println("execute called: "+task);
		boolean	added;
		synchronized(this)
		{
//			new RuntimeException().printStackTrace(System.out);
			added = queue.add(task);
//			System.out.println("Task added: "+queue);
		}
		
		// On change, wake up the main executor for executing tasks
		if(added)
		{
			if(running) 
				executor.execute();
		}
	}
	
	/**
	 *  Cancel a task. Triggers the task to
	 *  be not executed in future. 
	 *  @param task The task to execute.
	 */
	public synchronized IFuture cancel(IExecutable task)
	{
		Future ret = new Future();
		
		if(!isValid())
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
	public synchronized IExecutable[]	getTasks()
	{
		return (IExecutable[])queue.toArray(new IExecutable[queue.size()]);
	}
	
	/**
	 *  Test if the service is valid.
	 *  @return True, if service can be used.
	 */
	public boolean isValid()
	{
		return running && !shutdown;
	}

	/**
	 *  Start the executor service.
	 *  Resumes all tasks.
	 */
	public synchronized IFuture	startService()
	{	
		final  Future ret = new Future();
	
		if(shutdown)
		{
			ret.setResult(null);
			return ret;
		}
		
		SServiceProvider.getService(provider, IThreadPoolService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				executor = new Executor((IThreadPoolService)result, new IExecutable()
				{
					public boolean execute()
					{
						// Perform one task a time.
						
						// assert task==null;
						synchronized(SyncExecutionService.this)
						{
							if(running && !queue.isEmpty())
							{
								// Hack!!! Is there a better way to get first element from queue without creating iterator?
								Iterator iterator = queue.iterator();
								task = (IExecutable)iterator.next();
								iterator.remove();
							}
						}
						
						if(task!=null)
						{
							boolean again = false;
							try
							{
								again = task.execute();
							}
							catch(Exception e)
							{
								System.out.println("Exception during executing task: "+task);
								e.printStackTrace();
							}
							
							synchronized(SyncExecutionService.this)
							{
								// assert task!=null;
								
								if(removedtask==null)
								{
									if(again && running)
									{
										queue.add(task);
									}
								}
								else if(removedtask==task)
								{
									removedtask = null;
									for(int i=0; i<removedfut.size(); i++)
										((Future)removedfut.get(i)).setResult(null);
									removedfut.clear();
								}
								else
								{
									throw new RuntimeException("Removedtask!=task: "+task+" "+removedtask);
								}
								
								task = null;
							}
						}
						
						
						// When no more executables, inform idle commands.
//						boolean perform = false;
						Future ifc = null;
						synchronized(SyncExecutionService.this)
						{
							if(running && queue.isEmpty())
							{
								ifc = idlefuture;
								idlefuture = null;
//								perform = idlefuture!=null;
							}
						}
						if(ifc!=null)
						{
//							System.out.println("Idle");
							ifc.setResult(null);
//							Iterator it	= idlecommands.iterator();
//							while(it.hasNext())
//							{
//								((ICommand)it.next()).execute(null);
//							}
						}
						
						// Perform next task when queue is not empty and service is running.
						synchronized(SyncExecutionService.this)
						{
							// todo: extract call from synchronized block
							if(stoplistener!=null)
							{
								stoplistener.resultAvailable(null);
								stoplistener = null;
							}
							
							return running && !queue.isEmpty();
						}
					}
				});
				
				ret.setResult(SyncExecutionService.this);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		running = true;
		// Wake up the main executor for executing tasks
		executor.execute();

		return ret;
	}

		
	/**
	 *  Shutdown the executor service.
	 */
	public IFuture	shutdownService()
	{
		IFuture	ret = null;
		Future idf = null;
		
		synchronized(this)
		{
			if(!isValid())
//			if(!running || shutdown)
			{
				ret = new Future();
				((Future)ret).setException(new RuntimeException("Not running."));
			}
			else
			{
				this.running = false;
				this.shutdown = true;
				ret	= executor.shutdown();
				queue = null;
				idf = idlefuture;
			}
		}
		
		if(idf!=null)
			idf.setException(new RuntimeException("Shutdown"));
		
		return ret;
	}
	
//	/**
//	 *  Test if the executor is currently idle.
//	 *  @return True, if idle.
//	 */
//	public synchronized boolean	isIdle()
//	{
//		//System.out.println(running+" "+queue);
//		//return running && queue.isEmpty();
//		return queue.isEmpty();
//	}
	
	/**
	 *  Get the future indicating that executor is idle.
	 */
	public synchronized IFuture getNextIdleFuture()
	{
		Future ret;
		if(shutdown)
		{
			ret = new Future(new RuntimeException("Shutdown"));
		}
		else
		{
			if(idlefuture==null)
				idlefuture = new Future();
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
//	public void removeIdleCommand(ICommand command)
//	{
//		if(idlecommands!=null)
//			idlecommands.remove(command);
//	}
}
