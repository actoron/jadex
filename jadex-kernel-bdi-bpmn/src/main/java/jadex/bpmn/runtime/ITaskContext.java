package jadex.bpmn.runtime;

/**
 *  The task context contains the data available to
 *  an application task implementation.
 */
public interface ITaskContext
{
	/**
	 *  Get the value of a parameter.
	 *  @param name	The parameter name. 
	 *  @return	The parameter value. 
	 */
	public Object	getParameterValue(String name);

	/**
	 *  Set the value of a parameter.
	 *  @param name	The parameter name. 
	 *  @param value	The parameter value. 
	 */
	public void	setParameterValue(String name, Object value);
}
