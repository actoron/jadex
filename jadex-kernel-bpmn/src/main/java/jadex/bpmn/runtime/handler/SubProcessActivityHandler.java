package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ThreadContext;

import java.util.List;

/**
 *  Handler for (embedded) sub processes.
 */
public class SubProcessActivityHandler extends DefaultActivityHandler
{
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(MActivity activity, BpmnInterpreter instance, ProcessThread thread)
	{
		MSubProcess	proc	= (MSubProcess) activity;
//		thread.setWaitingState(ProcessThread.WAITING_FOR_SUBPROCESS);
		thread.setWaiting(true);
		ThreadContext	subcontext	= new ThreadContext(proc, thread);
		thread.getThreadContext().addSubcontext(subcontext);
		
		List start = proc.getStartActivities();
		for(int i=0; i<start.size(); i++)
		{
			ProcessThread	newthread	= new ProcessThread((MActivity)start.get(i), subcontext, instance);
			subcontext.addThread(newthread);
		}
		
	}
}
