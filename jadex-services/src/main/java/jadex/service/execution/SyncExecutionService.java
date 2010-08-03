package jadex.service.execution;

import jadex.commons.Future;
import jadex.commons.ICommand;
import jadex.commons.IFuture;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.Executor;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.IResultListener;
import jadex.service.BasicService;
import jadex.service.IServiceProvider;
import jadex.service.SServiceProvider;
import jadex.service.threadpool.IThreadPoolService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
	
	/** The idle commands. */
	protected Set	idlecommands;
	
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
		super(BasicService.createServiceIdentifier(provider.getId(), SyncExecutionService.class));

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
		
		SServiceProvider.getService(provider, IThreadPoolService.class).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
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
						boolean perform = false;
						synchronized(SyncExecutionService.this)
						{
							if(running && queue.isEmpty())
							{
								perform = idlecommands!=null;
							}
						}
						if(perform)
						{
//							System.out.println("Idle");
							Iterator it	= idlecommands.iterator();
							while(it.hasNext())
							{
								((ICommand)it.next()).execute(null);
							}
						}
						
						// Perform next task when queue is not empty and service is running.
						synchronized(SyncExecutionService.this)
						{
							// todo: extract call from synchronized block
							if(stoplistener!=null)
							{
								stoplistener.resultAvailable(this, null);
								stoplistener = null;
							}
							
							return running && !queue.isEmpty();
						}
					}
				});
				
				ret.setResult(SyncExecutionService.this);
			}
			
			public void exceptionOccurred(Object source, Exception exception)
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
	public synchronized IFuture	shutdownService()
	{
		if(!isValid())
//		if(!running || shutdown)
		{
			Future	ret	= new Future();
			ret.setException(new RuntimeException("Not running."));
			return ret;
		}
		
		this.running = false;
		this.shutdown = true;
		IFuture	ret	= executor.shutdown();
		queue = null;
		return ret;
	}

	
	/**
	 *  Test if the executor is currently idle.
	 *  @return True, if idle.
	 */
	public synchronized boolean	isIdle()
	{
		//System.out.println(running+" "+queue);
		//return running && queue.isEmpty();
		return queue.isEmpty();
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
	public void removeIdleCommand(ICommand command)
	{
		if(idlecommands!=null)
			idlecommands.remove(command);
	}
}
