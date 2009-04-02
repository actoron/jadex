package jadex.adapter.base.envsupport.environment.agentaction;

import jadex.adapter.base.envsupport.environment.ISpaceProcess;
import jadex.adapter.base.envsupport.environment.SynchronizedObject;
import jadex.adapter.base.envsupport.math.IVector1;

/**
 *
 */
public interface IActionExecutor extends ISpaceProcess
{
	public static final String DEFAULT_EXECUTOR_NAME = "action_executor";
	
	/**
	 * Returns the synchronizer.
	 * @return synchronizer
	 */
	public ActionSynchronizer getSynchronizer();
}
