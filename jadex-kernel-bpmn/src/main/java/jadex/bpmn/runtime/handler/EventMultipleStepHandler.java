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
		assert !instance.getComponentAdapter().isExternalThread();
		
		// Hack!!! Should be in interpreter/thread?
		thread.updateParametersAfterStep(activity, instance);

		MSequenceEdge next	= null;
		Object	wi	= null;	
		
		List outgoing = activity.getOutgoingSequenceEdges();
		OrFilter filter = (OrFilter)thread.getWaitFilter();
		IFilter[] filters = filter.getFilters();
		Object[] waitinfos = (Object[])thread.getWaitInfo();
		
		for(int i=0; i<outgoing.size(); i++)
		{
			MSequenceEdge	tmp = (MSequenceEdge)outgoing.get(i);

			// Timeout edge has event=null and filter=null.
			if(event==null && filters[i]==null
				|| filters[i]!=null && filters[i].filter(event))
			{
				next = tmp;
				wi = waitinfos[i];
			}
			else
			{
				MActivity act = tmp.getTarget();
				thread.setWaitInfo(waitinfos[i]);	// Hack!!! change wait infos for cancel() call
				instance.getActivityHandler(act).cancel(act, instance, thread);
				thread.setWaitInfo(waitinfos);
			}
		}
		
		if(next==null)
			throw new RuntimeException("Could not determine next edge: "+this);
		
		thread.setWaitInfo(wi);
		// Move thread to triggered event. Todo: process edge in between
		thread.setActivity(next.getTarget());	
		instance.step(next.getTarget(), instance, thread, event);
	}
}
