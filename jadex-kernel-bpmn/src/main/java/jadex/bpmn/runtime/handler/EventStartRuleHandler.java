package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ProcessThread;

/**
 *  When a subprocess has a rule start event it needs to be treated
 *  like an intermediate event (i.e. register an event matcher at the process engine).
 *  If its a top level start event, no special treatment is required, as
 *  the macthing is done by the process engine based on the model and the interpreter
 *  is started with the correct activity.
 */
public class EventStartRuleHandler extends EventIntermediateRuleHandler
{
	//-------- methods --------
	
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(final MActivity activity, final BpmnInterpreter instance, final ProcessThread thread)
	{
		// Top level event -> just move forward to next activity.
		if(thread.getThreadContext().getParent()==null)
		{
			doExecute(activity, instance, thread);
			instance.step(activity, instance, thread, null);
		}
		
		// Internal subprocess -> treat like intermediate event.
		else
		{
			super.execute(activity, instance, thread);
		}
	}
	
	/**
	 *  Called when the process thread is aborted and waiting is no longer wanted.
	 */
	public void cancel(MActivity activity, final BpmnInterpreter instance, ProcessThread thread)
	{
		// Internal subprocess -> treat like intermediate event.
		if(thread.getThreadContext().getParent()!=null)
		{
			super.execute(activity, instance, thread);
		}
	}
}
