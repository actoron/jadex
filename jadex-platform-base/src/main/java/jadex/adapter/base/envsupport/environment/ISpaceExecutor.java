package jadex.adapter.base.envsupport.environment;

import jadex.commons.IPropertyObject;

/**
 *  A space executor is responsible for executing an environment
 *  in a certain manner (e.g. round-based).
 */
public interface ISpaceExecutor extends IPropertyObject
{
	/**
	 *  Start the space executor.
	 */
	public void start();
	
	/**
	 *  Terminate the space executor.
	 */
	public void terminate();
}
