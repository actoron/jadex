package jadex.service.execution;

import jadex.commons.ICommand;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.IResultListener;

/**
 *  Common interface for different execution services.
 *  An executor service is responsible for executing
 *  for IExecutables.  
 *  
 *  todo: make available the state of the executor service.
 *  It should be possible to stop the service. Get its state
 *  transfer it to some other executor and start the other one. 
 *  Therefore suspend should either come back when service really
 *  is suspended or execution status should be readable.
 *  State representation? = all executables and their state?
 *  todo: use callbacks?
 */
public interface IExecutionService
{
	/**
	 *  Execute a task. Triggers the task to
	 *  be executed in future. 
	 *  @param task The task to execute.
	 *  @param listener Called when execution has started.
	 */
	public void execute(IExecutable task);
	
	/**
	 *  Cancel a task. Triggers the task to
	 *  be not executed in future. 
	 *  @param task The task to execute.
	 *  @param listener Called when execution has stopped.
	 */
	public void cancel(IExecutable task, IResultListener listener);

	/**
	 *  (Re-) Start the service (all tasks).  
	 */
	public void startService();

	/**
	 *  Stop the service (all tasks).
	 *  @param listener Called when execution has stopped.
	 */
	public void stop(IResultListener listener);
		
	/**
	 *  Shutdown the executor service.
	 *  @param listener Called when execution has stopped.
	 */
//	Already defined in IPlatformService
//	public void shutdown(IResultListener listener);
	
	/**
	 *  Test if the executor is currently idle.
	 */
	public boolean isIdle();

	/**
	 *  Add a command to be executed whenever the executor
	 *  is idle (i.e. no executables running).
	 */
	public void addIdleCommand(ICommand command);

	/**
	 *  Remove a previously added idle command.
	 */
	public void removeIdleCommand(ICommand command);
}
