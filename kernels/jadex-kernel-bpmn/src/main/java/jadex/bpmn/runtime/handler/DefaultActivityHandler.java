package jadex.bpmn.runtime.handler;

import jadex.bpmn.features.IBpmnComponentFeature;
import jadex.bpmn.features.IInternalBpmnComponentFeature;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.IActivityHandler;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IInternalAccess;


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
	public void execute(MActivity activity, IInternalAccess instance, ProcessThread thread)
	{
		doExecute(activity, instance, thread);
		getBpmnFeature(instance).step(activity, instance, thread, null);
//		return thread;
	}
	
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread The process thread.
	 *  @param info The info object.
	 */
	public void cancel(MActivity activity, IInternalAccess instance, ProcessThread thread)
	{
	}
	
	/**
	 *  Execute an activity. Empty default implementation.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	protected void doExecute(MActivity activity, IInternalAccess instance, ProcessThread thread)
	{
		if(DEBUG)
			System.out.println("Executed: "+activity+", "+instance);
	}
	
	/**
	 *  Get the internal bpmn feature from internal access.
	 */
	public static IInternalBpmnComponentFeature getBpmnFeature(IInternalAccess instance)
	{
		return (IInternalBpmnComponentFeature)instance.getComponentFeature(IBpmnComponentFeature.class);
	}
}
