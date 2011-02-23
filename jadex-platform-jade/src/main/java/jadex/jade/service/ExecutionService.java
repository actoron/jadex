package jadex.jade.service;

import jadex.commons.ICommand;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.service.BasicService;
import jadex.commons.service.IServiceProvider;
import jadex.commons.service.execution.IExecutionService;
import jadex.jade.JadeComponentAdapter;

import java.util.Map;

/**
 *  Simple execution service for JADE agent-based Jadex components.
 *  Does not (yet) support execution of other task kinds.
 */
public class ExecutionService extends BasicService implements IExecutionService
{
	//-------- constructors --------
	
	/**
	 *  Create a new execution service.
	 * @param providerid
	 */
	public ExecutionService(IServiceProvider provider, Map properties)
	{
		super(provider.getId(), IExecutionService.class, properties);
	}
	
	//-------- IExecutionService interface --------
	
	/**
	 *  Execute a task. Triggers the task to
	 *  be executed in future. 
	 *  @param task The task to execute.
	 */
	public void execute(IExecutable task)
	{
		if(task instanceof JadeComponentAdapter)
		{
			((JadeComponentAdapter)task).doWakeup();
		}
		else
		{
			throw new UnsupportedOperationException("Operation unavailable for non-JADE tasks.");
		}
	}
	
	/**
	 *  Cancel a task. Triggers the task to
	 *  be not executed in future. 
	 *  @param task The task to execute.
	 *  @return Future signaling cancellation.
	 */
	public IFuture cancel(IExecutable task)
	{
		if(task instanceof JadeComponentAdapter)
		{
			// Hack!!! Asynchronously stop agent execution.
			((JadeComponentAdapter)task).getJadeAgent().cancel();
			return IFuture.DONE;
		}
		else
		{
			throw new UnsupportedOperationException("Operation unavailable for non-JADE tasks.");
		}
	}
	
	/**
	 *  Get the currently running or waiting tasks.
	 */
	public IExecutable[]	getTasks()
	{
		throw new UnsupportedOperationException("Operation unavailable in JADE.");
	}
	
	/**
	 *  Test if the executor is currently idle.
	 */
	public boolean isIdle()
	{
		throw new UnsupportedOperationException("Operation unavailable in JADE.");
	}

	/**
	 *  Get the next idle future.
	 */
	public IFuture getNextIdleFuture()
	{
		Future ret = new Future();
		ret.setException(new UnsupportedOperationException("Operation unavailable in JADE."));
		return ret;
	}
	
//	/**
//	 *  Add a command to be executed whenever the executor
//	 *  is idle (i.e. no executables running).
//	 */
//	public void addIdleCommand(ICommand command)
//	{
//		throw new UnsupportedOperationException("Operation unavailable in JADE.");
//	}
//
//	/**
//	 *  Remove a previously added idle command.
//	 */
//	public void removeIdleCommand(ICommand command)
//	{
//		throw new UnsupportedOperationException("Operation unavailable in JADE.");
//	}
}
