package jadex.bpmn.features;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.IActivityHandler;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;

/**
 * 
 */
public interface IInternalBpmnComponentFeature
{
	/**
	 *  Test if the given context variable is declared.
	 *  @param name	The variable name.
	 *  @return True, if the variable is declared.
	 */
	public boolean hasContextVariable(String name);
	
	/**
	 *  Get the value of the given context variable.
	 *  @param name	The variable name.
	 *  @return The variable value.
	 */
	public Object getContextVariable(String name);
	
	/**
	 *  Set the value of the given context variable.
	 *  @param name	The variable name.
	 *  @param value	The variable value.
	 */
	public void setContextVariable(String name, Object value);
	
	/**
	 *  Set the value of the given context variable.
	 *  @param name	The variable name.
	 *  @param value	The variable value.
	 */
	public void setContextVariable(String name, Object key, Object value);
	
	/**
	 *  Create a thread event (creation, modification, termination).
	 */
	public IMonitoringEvent createThreadEvent(String type, ProcessThread thread);
	
	/**
	 *  Create an activity event (start, end).
	 */
	public IMonitoringEvent createActivityEvent(String type, ProcessThread thread, MActivity activity);

	/**
	 *  Get the activity handler for an activity.
	 *  @param actvity The activity.
	 *  @return The activity handler.
	 */
	public IActivityHandler getActivityHandler(MActivity activity);
	
	/**
	 *  Get the top level thread (is not executed and just acts as top level thread container).
	 */
	public ProcessThread getTopLevelThread();
	
	/**
	 *  Make a process step, i.e. find the next edge or activity for a just executed thread.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void step(MActivity activity, IInternalAccess instance, ProcessThread thread, Object event);
	
	/**
	 *  Method that should be called, when an activity is finished and the following activity should be scheduled.
	 *  Can safely be called from external threads.
	 *  @param activity	The timing event activity.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 *  @param event	The event that has occurred, if any.
	 */
	public void	notify(final MActivity activity, final ProcessThread thread, final Object event);
}
