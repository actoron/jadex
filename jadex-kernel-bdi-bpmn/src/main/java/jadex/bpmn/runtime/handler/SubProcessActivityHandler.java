package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.runtime.BpmnInstance;
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
	 *  @param context	The thread context.
	 */
	public void execute(MActivity activity, BpmnInstance instance, ProcessThread thread, ThreadContext context)
	{
		MSubProcess	proc	= (MSubProcess) activity;
		thread.setWaiting(ProcessThread.WAITING_FOR_SUBPROCESS);
		ThreadContext	subcontext	= new ThreadContext(proc, thread, context);
		context.addSubcontext(subcontext);
		
		List	start	= proc.getStartActivities();
		for(int i=0; i<start.size(); i++)
		{
			ProcessThread	newthread	= thread.createCopy();
			newthread.setNextActivity((MActivity)start.get(i));
			subcontext.addThread(newthread);
		}
	}
}
