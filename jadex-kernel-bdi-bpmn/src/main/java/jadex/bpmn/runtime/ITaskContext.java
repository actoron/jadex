package jadex.bpmn.runtime;

import jadex.bpmn.model.MActivity;

/**
 *  The task context contains the data available to
 *  an application task implementation.
 */
public interface ITaskContext
{
	/**
	 *  Get the model element.
	 *  @return	The model of the task.
	 */
	public MActivity	getModelElement();

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
