package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.IActivityHandler;
import jadex.bpmn.runtime.ProcessThread;


/**
 *  Default activity handler, which provides some
 *  useful helper methods.
 */
public class DefaultActivityHandler implements IActivityHandler
{	
	/** Debug flag for printing. */
	public static final boolean DEBUG = false;
	
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(MActivity activity, BpmnInterpreter instance, ProcessThread thread)
	{
		doExecute(activity, instance, thread);
		instance.step(activity, instance, thread, null);
//		return thread;
	}
	
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread The process thread.
	 *  @param info The info object.
	 */
	public void cancel(MActivity activity, BpmnInterpreter instance, ProcessThread thread)
	{
	}
	
	/**
	 *  Execute an activity. Empty default implementation.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	protected void doExecute(MActivity activity, BpmnInterpreter instance, ProcessThread thread)
	{
		if(DEBUG)
			System.out.println("Executed: "+activity+", "+instance);
	}
}
