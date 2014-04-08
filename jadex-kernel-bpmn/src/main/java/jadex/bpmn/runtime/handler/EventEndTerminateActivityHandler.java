package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ProcessThread;

/**
 *  On error end propagate an exception.
 */
public class EventEndTerminateActivityHandler extends DefaultActivityHandler
{
	/**
	 *  Execute the activity.
	 */
	protected void doExecute(MActivity activity, BpmnInterpreter instance, ProcessThread thread)
	{
		// Top level event -> kill the component.
		if(thread.getThreadContext().getParent()==null)
		{
			instance.killComponent();
		}
		
		// Internal subprocess -> terminate all threads in context, except own thread (which is terminated after interpreter step).
		else
		{
			for(ProcessThread pt: thread.getThreadContext().getThreads().toArray(new ProcessThread[thread.getThreadContext().getThreads().size()]))
			{
				if(pt!=thread)
				{
					thread.getThreadContext().removeThread(pt);
				}
			}
		}
	}
}
