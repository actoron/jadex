package jadex.bpmn.model.task;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;

/**
 *  The task context contains the data available to
 *  an application task implementation.
 */
public interface ITaskContext
{
	/**
	 *  Get the model.
	 *  @return	The bpmn model.
	 */
	public MBpmnModel getBpmnModel();
	
	/**
	 *  Get the model element.
	 *  @return	The model of the task.
	 */
	public MActivity getModelElement();
	
	/**
	 *  Get the activity.
	 *  @return The activity.
	 */
	public MActivity getActivity();
	
	/**
	 *  Check if the value of a parameter is set.
	 *  @param name	The parameter name. 
	 *  @return	True, if the parameter is set to some value. 
	 */
	public boolean hasParameterValue(String name);

	/**
	 *  Get the value of a parameter.
	 *  @param name	The parameter name. 
	 *  @return	The parameter value. 
	 */
	public Object getParameterValue(String name);

	/**
	 *  Set the value of a parameter.
	 *  @param name	The parameter name. 
	 *  @param value	The parameter value. 
	 */
	public void	setParameterValue(String name, Object value);

	/**
	 *  Set the value of a parameter.
	 *  @param name	The parameter name.
	 *  @param key An optional helper (index, key etc.) 
	 *  @param value The parameter value. 
	 */
	public void	setParameterValue(String name, Object key, Object value);
	
	/**
	 *  Set or create a parameter value directly in this thread.
	 *  @param name	The parameter name.
	 *  @param value The parameter value. 
	 */
	public void setOrCreateParameterValue(String name, Object value);
	
	/**
	 *  Set or create a parameter value directly in this thread.
	 *  @param name	The parameter name.
	 *  @param key An optional helper (index, key etc.) 
	 *  @param value The parameter value. 
	 */
	public void setOrCreateParameterValue(String name, Object key, Object value);
	
//	/**
//	 *  Gets the hard constraints.
//	 *  @return The hard constraints.
//	 */
//	public RHardConstraints getHardConstraints();
	
	/**
	 *  Get the value of a property.
	 *  @param name	The property name. 
	 *  @return	The property value. 
	 */
	public Object getPropertyValue(String name);
	
//	/**
//	 *  Check if the value of a result is set.
//	 *  @param name	The result name. 
//	 *  @return	True, if the result is set to some value. 
//	 */
//	public boolean hasResultValue(String name);
//
//	/**
//	 *  Get the value of a result.
//	 *  @param name	The result name. 
//	 *  @return	The result value. 
//	 */
//	public Object getResultValue(String name);
//	
//	/**
//	 *  Set the value of a result.
//	 *  @param name	The result name. 
//	 *  @param value The result value. 
//	 */
//	public void	setResultValue(String name, Object value);
	
}
