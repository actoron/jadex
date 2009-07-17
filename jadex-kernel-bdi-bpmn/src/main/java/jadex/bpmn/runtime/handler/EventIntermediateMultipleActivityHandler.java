package jadex.bpmn.runtime.handler;

import jadex.bdi.bpmn.BpmnPlanBodyInstance;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.runtime.BpmnInstance;
import jadex.bpmn.runtime.ProcessThread;
import jadex.commons.IFilter;

import java.util.List;

/**
 *  Event intermediate multi handler.
 */
public class EventIntermediateMultipleActivityHandler extends DefaultActivityHandler
{
	/**
	 *  Execute an activity. Empty default implementation.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(MActivity activity, BpmnInstance instance, ProcessThread thread)
	{
		System.out.println("Executed: "+activity+", "+instance);
		
		// Call all connected intermediate event handlers.
		List outgoing = activity.getOutgoingSequenceEdges();
		if(outgoing==null)
			throw new UnsupportedOperationException("Activity must have connected activities: "+activity);
		
		// Execute all connected activities.
		final IFilter[] filters = new IFilter[outgoing.size()];
		Object[] waitinfos = new Object[outgoing.size()];
		for(int i=0; i<outgoing.size(); i++)
		{
			MSequenceEdge next	= (MSequenceEdge)outgoing.get(i);
			MActivity act = next.getTarget();
			instance.getActivityHandler(act).execute(act, instance, thread);
			filters[i] = thread.getWaitFilter();
			waitinfos[i] = thread.getWaitInfo();
			thread.setWaitFilter(null);
			thread.setWaitInfo(null);
		}
		
		// Set waiting state and filter.
//		thread.setWaitingState(ProcessThread.WAITING_FOR_MULTI);
		thread.setWaiting(true);
		thread.setWaitFilter(new OrFilter(filters));
		thread.setWaitInfo(waitinfos);
	}
	
	/**
	 *  Make a process step, i.e. find the next edge or activity for a just executed thread.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 *  @param context	The thread context.
	 */
	public void step(MActivity activity, BpmnInstance instance, ProcessThread thread, Object event)
	{
		MSequenceEdge next	= null;
		
		List outgoing = activity.getOutgoingSequenceEdges();
		OrFilter filter = (OrFilter)thread.getWaitFilter();
		IFilter[] filters = filter.getFilters();
		
		// Remove the timer entry.
		((BpmnPlanBodyInstance)instance).removeTimer(thread);
		
		for(int i=0; i<outgoing.size() && next==null; i++)
		{
			// Timeout edge has event=null and filter=null.
			if((event==null && filters[i]==null) || (filters[i]!=null && filters[i].filter(event)))
			{
				next = (MSequenceEdge)outgoing.get(i);
			}
		}
		
		if(next==null)
			throw new RuntimeException("Could not determine next edge: "+this);
		
		super.step(next.getTarget(), instance, thread, event);
	}
}

/**
 *  Or filter implementation.
 */
class OrFilter implements IFilter
{
	//-------- attributes ---------
	
	/** The filters. */
	protected IFilter[] filters;
	
	//-------- constructors --------
	
	/**
	 *  Create a new or filter.
	 */
	public OrFilter(IFilter[] filters) 
	{
		this.filters = filters;
	}
	
	//-------- methods --------
	
	/**
	 *  Test if an object passes the filter.
	 *  @return True, if passes the filter.
	 */
	public boolean filter(Object obj)
	{
		boolean ret = false;
		for(int i=0; !ret && i<filters.length; i++)
		{
			if(filters[i]!=null)
			{
				try
				{
					ret = filters[i].filter(obj);
				}
				catch(Exception e)
				{
					// just catch.
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Get the filters.
	 *  @return The filters.
	 */
	public IFilter[] getFilters()
	{
		return filters;
	}
}
