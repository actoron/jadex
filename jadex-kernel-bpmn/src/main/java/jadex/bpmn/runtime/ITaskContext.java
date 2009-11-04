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
	 *  Check if the value of a parameter is set.
	 *  @param name	The parameter name. 
	 *  @return	True, if the parameter is set to some value. 
	 */
	public boolean	hasParameterValue(String name);

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
	
	/**
	 *  Get the value of a property.
	 *  @param name	The property name. 
	 *  @return	The property value. 
	 */
	public Object	getPropertyValue(String name);
}
