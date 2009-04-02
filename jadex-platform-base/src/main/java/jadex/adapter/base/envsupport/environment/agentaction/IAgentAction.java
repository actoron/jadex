package jadex.adapter.base.envsupport.environment.agentaction;

import java.util.Map;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.commons.concurrent.IResultListener;

public interface IAgentAction
{
	
	/**
	 * Executes the delayed action. Called by the Executor.
	 * 
	 * @param parameters parameters for the action
	 * @param space the environment space
	 * 
	 */
	public Object execute(Map parameters, IEnvironmentSpace space);
}
