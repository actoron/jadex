package jadex.bpmn.runtime.handler;

import java.util.List;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.IStepHandler;
import jadex.bpmn.runtime.ProcessThread;
import jadex.commons.IFilter;

/**
 *  Step handler that can be used for event-multiple elements.
 */
public class EventMultipleStepHandler implements IStepHandler
{
	/**
	 *  Make a process step, i.e. find the next edge or activity for a just executed thread.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void step(MActivity activity, BpmnInterpreter instance, ProcessThread thread, Object event)
	{
		// Hack!!! Should be in interpreter/thread?
		thread.updateParametersAfterStep(activity, instance);

		MSequenceEdge next	= null;
		
		List outgoing = activity.getOutgoingSequenceEdges();
		OrFilter filter = (OrFilter)thread.getWaitFilter();
		IFilter[] filters = filter.getFilters();
		Object[] waitinfos = (Object[])thread.getWaitInfo();
		
		// Remove the timer entry.
		// todo: how to remove timer generically
		
		for(int i=0; i<outgoing.size() && next==null; i++)
		{
			// Timeout edge has event=null and filter=null.
			if((event==null && filters[i]==null))
			{
				next = (MSequenceEdge)outgoing.get(i);
				MActivity act = next.getTarget();
				thread.setWaitInfo(waitinfos[i]);
				instance.getActivityHandler(act).cancel(act, instance, thread);
			}
			else if(filters[i]!=null && filters[i].filter(event))
			{
				next = (MSequenceEdge)outgoing.get(i);
			}
		}
		
		if(next==null)
			throw new RuntimeException("Could not determine next edge: "+this);
		
		instance.getStepHandler(next.getTarget()).step(next.getTarget(), instance, thread, event);
	}
}
