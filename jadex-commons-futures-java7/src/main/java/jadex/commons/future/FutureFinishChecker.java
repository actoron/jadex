package jadex.commons.future;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *  Allows monitoring a number of tasks (futures) and get a
 *  notification when set to finished (no more new tasks)
 *  and all tasks have been processed (futures notified).
 */
public class FutureFinishChecker 
{
	//-------- attributes --------
	
	/** The delegate to be notified. */
	protected IResultListener delegate;
	
	/** The list of future tasks to observe. */
	protected List tasks;
	
	/** Flag, if set to finished (no more tasks allowed and notfication when all tasks finished). */
	protected boolean finished;
	
	/** Flag to remember that delegate was notified. */
	protected boolean notified;
	
	//-------- constructors --------
	
	/**
	 *  Create a new checker.
	 */
	public FutureFinishChecker(IResultListener delegate)
	{
		this.delegate = delegate;
	}
	
	//-------- methods --------

	/**
	 *  Add a task.
	 */
	public void addTask(final Future future)
	{
		synchronized(this)
		{
			if(finished)
			{
//				finishedex.printStackTrace();
				throw new RuntimeException("Add task not allowed after finished.");
			}
			if(tasks==null)
				tasks = Collections.synchronizedList(new ArrayList());
			tasks.add(future);
		}
		
		future.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				tasks.remove(future);
				check();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				tasks.remove(future);
				check();
			}
		});
	}
	
	
//	Exception finishedex;
	
	/**
	 *  Set to finished.
	 */
	public void finished()
	{
		synchronized(this)
		{
//			finishedex	= new DebugException("finished called: "+this);
			finished = true;
		}
		check();
	}
	
	/**
	 *  Check if completed.
	 */
	public void check()
	{
		boolean notify;
		synchronized(this)
		{
			notify = finished && (tasks==null || tasks.size()==0) && !notified;
			if(notify)
				notified = true;
		}
		if(notify)
		{
			delegate.resultAvailable(null);
		}
	}
}