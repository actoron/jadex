package jadex.bpmn.runtime;

/**
 *  Interface used for callbacks from the executing process to
 *  its execution environment.
 */
public interface IBpmnExecutor
{
	/**
	 *  Indicate that the process may have changed its ready state.
	 *  This method may be called from arbitrary threads!
	 *  It has to be assured that its implementation is thread safe.
	 */
	public void	wakeUp();
}
