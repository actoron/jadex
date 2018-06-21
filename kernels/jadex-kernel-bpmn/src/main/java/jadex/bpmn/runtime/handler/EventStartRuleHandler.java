package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IInternalAccess;

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
	public void execute(final MActivity activity, final IInternalAccess instance, final ProcessThread thread)
	{
		// Top level event -> just move forward to next activity.
		// Or start event of event subprocess -> just move forward.
		if(thread.getParent().getParent()==null	// check that parent thread is the top thread.
			|| (thread.getParent().getModelElement() instanceof MSubProcess
			&& MSubProcess.SUBPROCESSTYPE_EVENT.equals(((MSubProcess)thread.getParent().getModelElement()).getSubprocessType())))
		{
			doExecute(activity, instance, thread);
			getBpmnFeature(instance).step(activity, instance, thread, null);
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
	public void cancel(MActivity activity, final IInternalAccess instance, ProcessThread thread)
	{
		// Internal subprocess -> treat like intermediate event.
//		if(thread.getThreadContext().getParent()!=null)
		if(thread.getParent()!=null)
		{
			super.execute(activity, instance, thread);
		}
	}
	
}
