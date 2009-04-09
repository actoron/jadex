package jadex.adapter.base.envsupport.environment;

import java.util.Map;

/**
 *  Interface for agent actions. These actions are asynchronous and
 *  first scheduled in the environment. They will be executed when the
 *  next step() is called.
 */
public interface IAgentAction
{
	/**
	 * Executes the delayed action. Called by the Executor.
	 * @param parameters parameters for the action
	 * @param space the environment space
	 */
	public Object perform(Map parameters, IEnvironmentSpace space);
}
