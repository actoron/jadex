package jadex.commons.service.execution;

import jadex.commons.ICommand;
import jadex.commons.IFuture;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.service.IService;

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
public interface IExecutionService	extends IService
{
	/**
	 *  Execute a task. Triggers the task to
	 *  be executed in future. 
	 *  @param task The task to execute.
	 */
	public void execute(IExecutable task);
	
	/**
	 *  Cancel a task. Triggers the task to
	 *  be not executed in future. 
	 *  @param task The task to execute.
	 *  @return Future signaling cancellation.
	 */
	public IFuture cancel(IExecutable task);
	
	/**
	 *  Get the currently running or waiting tasks.
	 */
	public IExecutable[]	getTasks();
	
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
