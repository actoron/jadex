package jadex.bpmn.runtime;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSequenceEdge;

import java.util.List;


/**
 *  Default activity handler, which provides some
 *  useful helper methods.
 */
public class DefaultActivityHandler implements IActivityHandler
{	
	/**
	 *  Execute an activity. Empty default implementation.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	protected void doExecute(MActivity activity, BpmnInstance instance, ProcessThread thread)
	{
		System.out.println("Executed: "+activity+", "+instance);
	}

	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(MActivity activity, BpmnInstance instance, ProcessThread thread)
	{
		executeDefault(activity, instance, thread);
	}
	
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void executeDefault(MActivity activity, BpmnInstance instance, ProcessThread thread)
	{
		doExecute(activity, instance, thread);
		
		MSequenceEdge	next	= getOutgoingEdge(activity, instance, thread);
		if(next!=null)
		{
			thread.setLastEdge(next);
		}
		else
		{
			instance.getThreads().remove(thread);
		}
	}
	
	/**
	 *  Get the outgoing edge.
	 *  @param activity	The current activity.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	protected MSequenceEdge	getOutgoingEdge(MActivity activity, BpmnInstance instance, ProcessThread thread)
	{
		MSequenceEdge	ret;
		
		List	outgoing	= activity.getOutgoingEdges();
		if(outgoing==null || outgoing.size()==0)
		{
			ret	= null;
		}
		else if(outgoing.size()==1)
		{
			ret	= (MSequenceEdge)outgoing.get(0);
		}
		else
		{
			throw new UnsupportedOperationException("Activity has more than one one outgoing edge. Please overridge getOutgoingEdge() for disambiguation: "+activity);
		}
		
		return ret; 
	}
	
	/**
	 *  Method that should be called, when the timer event occurs in the platform.
	 *  @param activity	The timing event activity.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void	notify(MActivity activity, BpmnInstance instance, ProcessThread thread)
	{
		thread.setWaiting(false);
		executeDefault(activity, instance, thread);
		instance.wakeUp();
	}
}
