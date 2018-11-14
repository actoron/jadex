package jadex.bridge.service.types.registry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;

/**
 *  A combination of multiple registry events.
 */
public class MultiRegistryEvent extends ARegistryEvent
{
	/** The events. */
	protected List<ARegistryEvent> events;
	
	/**
	 *  Create a new registry event.
	 */
	public MultiRegistryEvent()
	{
	}
	
	/**
	 *  Create a new registry event.
	 */
	public MultiRegistryEvent(int eventslimit, long timelimit)
	{
		super(eventslimit, timelimit);
	}

	/**
	 *  Get the events.
	 *  @return The events
	 */
	public List<ARegistryEvent> getEvents()
	{
		return events;
	}
	
	/**
	 *  Set the events.
	 *  @param events The events to set.
	 */
	public void setEvents(List<ARegistryEvent> events)
	{
		this.events = events;
	}

	/**
	 *  Add an event.
	 *  @param event The event.
	 */
	public void addEvent(ARegistryEvent event)
	{
		if(events==null)
			events = new ArrayList<ARegistryEvent>();
		events.add(event);
	}
	
	/**
	 * Returns the number of elements added to this event.
	 */
	public int size()
	{
		// Just use number of contained events?! or better their contained number?!
		return events==null? 0: events.size();
	}
	
	/**
	 *  Get the clients.
	 */
	public Set<IComponentIdentifier> getAggregatedClients()
	{
		Set<IComponentIdentifier> ret = new HashSet<IComponentIdentifier>();
		if(events!=null)
		{
			for(ARegistryEvent event: events)
			{
				Set<IComponentIdentifier> tmp = event.getClients();
				if(tmp!=null && tmp.size()>0)
				{
					for(IComponentIdentifier cid: tmp)
						ret.add(cid);
				}
			}
		}
		return ret;
	}

	/**
	 *  Get the string rep.
	 */
	public String toString()
	{
		return "MultiRegistryEvent [sender="+sender+", size=" + (events!=null? events.size(): 0) + "]";
	}
	
}
