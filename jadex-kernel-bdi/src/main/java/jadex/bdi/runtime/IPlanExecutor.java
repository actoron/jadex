package jadex.bdi.runtime;

import jadex.bdi.runtime.interpreter.BDIInterpreter;


/**
 *  The plan executor is responsible for creating
 *  plan bodies and executing plan steps.
 *  Different implementations may exist for different
 *  types of plan bodies.
 */
public interface IPlanExecutor
{
	/**
	 *  Create the body of a plan.
	 *  @param plan The plan.
	 *  @return	The created body.
	 *  May throw any kind of exception, when the body creation fails
	 */
	public Object createPlanBody(BDIInterpreter interpreter, Object rplan, Object rcapability) throws Exception;

	/**
	 *  Execute a step of a plan.
	 *  Executing a step should cause the latest event to be handled.
	 *  
	 *  Will be called by the scheduler for every event to be handled.
	 *  May throw any kind of exception, when the plan execution fails
	 *  @return True, if plan was interrupted (micro plan step).
	 */
	public boolean	executePlanStep(BDIInterpreter interpreter, Object rcapability, Object rplan)	throws Exception;

	/**
	 *  Execute a step of the plans passed() code.
	 *  This method is called, after the plan has finished
	 *  successfully (i.e. without exception).
	 *  
	 *  Will be called by the scheduler for the first time and 
	 *  every subsequent event to be handled.
	 *  May throw any kind of exception, when the execution fails
	 *  @return True, if execution was interrupted (micro plan step).
	 */
	public boolean	executePassedStep(BDIInterpreter interpreter, Object rplan)	throws Exception;

	/**
	 *  Execute a step of the plans failed() code.
	 *  This method is called, when the plan has failed
	 *  (i.e. due to an exception occurring in the plan body).
	 *  
	 *  Will be called by the scheduler for the first time and 
	 *  every subsequent event to be handled.
	 *  May throw any kind of exception, when the execution fails
	 *  @return True, if execution was interrupted (micro plan step).
	 */
	public boolean	executeFailedStep(BDIInterpreter interpreter, Object rplan)	throws Exception;

	/**
	 *  Execute a step of the plans aborted() code.
	 *  This method is called, when the plan is terminated
	 *  from the outside (e.g. when the corresponding goal is
	 *  dropped or the context condition of the plan becomes invalid)
	 *  
	 *  Will be called by the scheduler for the first time and 
	 *  every subsequent event to be handled.
	 *  May throw any kind of exception, when the execution fails
	 *  @return True, if execution was interrupted (micro plan step).
	 */
	public boolean	executeAbortedStep(BDIInterpreter interpreter, Object rplan)	throws Exception;

	/**
	 *  Interrupt a plan step during execution (micro plan step).
	 *  The plan is requested to stop the execution to allow
	 *  consequences of performed plan actions (like belief changes)
	 *  taking place.
	 *  
	 *  When the plan type does not support interruption inside
	 *  plan steps, interruption can be ignored. In this case
	 *  all plan steps are executed as if they were atomic blocks.
	 *  @param rplan The plan handle. 
	 */
	public void	interruptPlanStep(Object rplan);
	
	/**
	 *  Cleanup after plan execution.
	 *  In general, plan execution should be finished, when
	 *  this method is called. but in exceptional situations
	 *  (e.g. unexpected death of agent) this method might
	 *  get called for a still running (or still aborting) plan.
	 *  In this case the execution should be terminated
	 *  (without further abort) and all resources be freed. 
	 */
	public void	cleanup(BDIInterpreter interpreter, Object rplan);

	/**
	 *  Get the executing thread of a plan.
	 *  @param rplan The plan.
	 * /
	public Thread getExecutionThread(RPlan rplan);*/

	/**
	 *  Block a plan until an event matching the wait abstraction occurs.
	 *  Only used for standard plans, which block during execution.
	 */
	public void eventWaitFor(BDIInterpreter interpreter, Object rplan);
	
	/**
	 *  Get the monitor of a plan.
	 *  @return The monitor.
	 */
	public Object getMonitor(Object rplan);
}
