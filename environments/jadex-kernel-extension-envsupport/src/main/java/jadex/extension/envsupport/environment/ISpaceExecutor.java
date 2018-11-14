package jadex.extension.envsupport.environment;

import jadex.commons.IPropertyObject;

/**
 *  A space executor is responsible for executing an environment
 *  in a certain manner (e.g. round-based).
 */
public interface ISpaceExecutor extends IPropertyObject
{
	//-------- constants --------
	
	/** Property to enable execution monitoring
	 * (i.e. print warnings when components are still executing during advancement of time). */
	public static String	PROPERTY_EXECUTION_MONITORING	= "execution_monitoring";
	
	//-------- methods --------
	
	/**
	 *  Start the space executor.
	 */
	public void start();
	
	/**
	 *  Terminate the space executor.
	 */
	public void terminate();
}
