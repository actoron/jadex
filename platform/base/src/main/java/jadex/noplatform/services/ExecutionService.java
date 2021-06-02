package jadex.noplatform.services;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.component.impl.AbstractComponentFeature;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.Executor;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  The asynchronous executor service that executes all tasks in separate executors.
 */
public class ExecutionService extends BaseService implements IExecutionService
{
	/**
	 *  The possible states of the service.
	 */
	public enum State
	{
		CREATED, RUNNING, SHUTDOWN
	}
	
	//-------- attributes --------
	
	/** The threadpool. */
	protected IThreadPool threadpool;
	
	/** The currently waiting tasks (task->executor). */
	protected Map<IExecutable, Executor> executors;
		
	/** The idle future. */
	protected Future<Void> idlefuture;
	
	/** The state. */
	protected State state;
	
	/** The running (i.e. non-blocked) executors. */
	protected Map<IExecutable, Executor> runningexes;
	
	//-------- constructors --------
	
	/**
	 *  Create a new asynchronous executor service. 
	 */
	public ExecutionService(IComponentIdentifier cid, IThreadPool threadpool)
	{
		super(cid, IExecutionService.class);
		
		this.threadpool = threadpool;
		this.executors	= SCollection.createHashMap();
		this.runningexes = SCollection.createHashMap();
		this.state	= State.CREATED;
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
		{
			throw new RuntimeException("Not running. Cannot execute: "+task);
		}
		
		Executor exe = executors.get(task);

		if(exe==null)
		{
			exe = new Executor(threadpool, task)
			{
				// Hack!!! overwritten to know, when executor ends.
				public void run()
				{
					synchronized(ExecutionService.this)
					{
//						System.err.println("non-idle: "+runningexes.keySet());
						runningexes.put(task, this);
					}
					
					try
					{
						super.run();
					}
					catch(Throwable e)
					{
						// Shouldn't happen. If it does, it will break simulation time.
						System.err.println("Uncatched exception in executable "+executable+": "+SUtil.getExceptionStacktrace(e));
					}
					
					Future<Void> idf = null;
					
					synchronized(ExecutionService.this)
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
//									System.err.println("after task: "+state+", "+runningexes.keySet());
								
								// When no more executable threads, inform idle commands.				
								if(state==State.RUNNING && idlefuture!=null && runningexes.isEmpty())
								{
									idf = idlefuture;
									idlefuture = null;
//										System.err.println("idle");
								}
							}
							else if(executors!=null && executors.get(task)!=this && runningexes.get(task)==this)
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

				// Hack!!! Skip shutdown of platform executor for "boot unstrapping" -> executor will finish after no more steps
				public IFuture<Void> shutdown()
				{
					if(task instanceof AbstractComponentFeature && ((AbstractComponentFeature)task).getComponent().getId().equals(getServiceId().getProviderId()))
					{
						return IFuture.DONE;
					}
					else
					{
						return super.shutdown();
					}
				}
			};
			executors.put(task, exe);
		}
	
		if(state==State.RUNNING)
		{
			boolean	exec = exe.execute();
			if(exec)
			{
				runningexes.put(task, exe);
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
			throw new RuntimeException("Not running. Cannot cancel: "+task);
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
						synchronized(ExecutionService.this)
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
						synchronized(ExecutionService.this)
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
		state	= State.RUNNING;
		
		return IFuture.DONE;
	}

	/**
	 *  Shutdown the executor service.
	 *  // todo: make callable more than once
	 */
	public synchronized IFuture<Void>	shutdownService()
	{
		final Future<Void> ret	= new Future<Void>();
		
		state = State.SHUTDOWN;
					
		IExecutable[] keys = (IExecutable[])executors.keySet()
			.toArray(new IExecutable[executors.size()]);
					
		if(keys.length>0)
		{
			// One listener counts until all executors have shutdowned.
			final IResultListener<Void> lis = new CounterResultListener<Void>(keys.length, new DelegationResultListener<Void>(ret));
			for(int i=0; i<keys.length; i++)
			{
				final Executor exe = (Executor)executors.get(keys[i]);
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
		
		return ret;
	}

	/**
	 *  Test if the service is valid.
	 *  @return True, if service can be used.
	 */
	public synchronized boolean customIsValid()
	{
		return state==State.RUNNING;
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
