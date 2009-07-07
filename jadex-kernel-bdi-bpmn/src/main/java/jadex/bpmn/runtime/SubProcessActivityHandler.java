package jadex.bpmn.runtime;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSubProcess;

import java.util.ArrayList;
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
	public void execute(final MActivity activity, final BpmnInstance instance, final ProcessThread thread)
	{
		MSubProcess	proc	= (MSubProcess) activity;
		List	stackframe	= new ArrayList();
//		thread.addStackFrame(stackframe);
		
		List	start	= proc.getStartEvents();
		for(int i=0; i<start.size(); i++)
		{
			if(i==0)
			{
				stackframe.add(thread);
				thread.setNextActivity((MActivity)start.get(i));
			}
			else
			{
				ProcessThread	newthread	= thread.createCopy();
				newthread.setNextActivity((MActivity)start.get(i));
//				instance.getThreads().add(newthread);
			}
		}
	}
}
