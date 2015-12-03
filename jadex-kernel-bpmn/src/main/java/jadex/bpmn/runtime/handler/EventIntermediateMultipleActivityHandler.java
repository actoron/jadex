package jadex.bpmn.runtime.handler;

import java.util.List;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IInternalAccess;
import jadex.commons.IFilter;

/**
 *  Event intermediate multi handler.
 */
public class EventIntermediateMultipleActivityHandler extends DefaultActivityHandler
{
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(final MActivity activity, final IInternalAccess instance, final ProcessThread thread)
	{
//		System.out.println("Executed: "+activity+", "+instance);
		
		// Call all connected intermediate event handlers.
		final List<MSequenceEdge> outgoing = activity.getOutgoingSequenceEdges();
		if(outgoing==null)
			throw new UnsupportedOperationException("Activity must have connected activities: "+activity);
		
		// Execute all connected activities.
		final IFilter<Object>[] filters = new IFilter[outgoing.size()];
		final ICancelable[] waitinfos = new ICancelable[outgoing.size()];
		
		for(int i=0; i<outgoing.size(); i++)
		{
			MSequenceEdge next	= (MSequenceEdge)outgoing.get(i);
			MActivity act = next.getTarget();
			getBpmnFeature(instance).getActivityHandler(act).execute(act, instance, thread);
			
			// Remember wait setting and delete them in the thread.
			filters[i] = thread.getWaitFilter();
			waitinfos[i] = thread.getWaitInfo();
			thread.setWaitFilter(null);
			thread.setWaitInfo(null);
		}
		
		// Set waiting state and filter.
//		thread.setWaitingState(ProcessThread.WAITING_FOR_MULTI);
		thread.setWaiting(true);
		thread.setWaitFilter(new OrFilter(filters));
//		thread.setWaitInfo(waitinfos);
		thread.setWaitInfo(new CompositeCancelable(outgoing, thread, instance, waitinfos));
	}
	
//	/**
//	 *  Execute an activity.
//	 *  @param activity	The activity to execute.
//	 *  @param instance	The process instance.
//	 *  @param thread The process thread.
//	 *  @param info The info object.
//	 */
//	public void cancel(MActivity activity, BpmnInterpreter instance, ProcessThread thread)
//	{
////		System.out.println(instance.getComponentIdentifier()+" cancel called: "+activity+", "+thread);
//		List<MSequenceEdge> outgoing = activity.getOutgoingSequenceEdges();
//		Object[] waitinfos = (Object[])thread.getWaitInfo();
//		
////		if(waitinfos==null)
////			System.out.println("here");
//	
//		for(int i=0; i<outgoing.size(); i++)
//		{
//			MSequenceEdge next = (MSequenceEdge)outgoing.get(i);
//			MActivity act = next.getTarget();
//			thread.setWaitInfo(waitinfos[i]);
//			instance.getActivityHandler(act).cancel(act, instance, thread);
//		}
//	}
}

/**
 *  Or filter implementation.
 */
class OrFilter implements IFilter<Object>
{
	//-------- attributes ---------
	
	/** The filters. */
	protected IFilter<Object>[] filters;
	
	//-------- constructors --------
	
	/**
	 *  Create a new or filter.
	 */
	public OrFilter(IFilter<Object>[] filters) 
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
	public IFilter<Object>[] getFilters()
	{
		return filters;
	}
}

