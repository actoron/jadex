package jadex.bridge.service.types.registry;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class MultiRegistryResponseEvent extends RegistryResponseEvent
{
	/** The events. */
	protected List<ARegistryResponseEvent> events;
	
	/**
	 *  Create a new registry event.
	 */
	public MultiRegistryResponseEvent()
	{
	}
	
	/**
	 *  Create a new RegistryUpdateEvent.
	 */
	public MultiRegistryResponseEvent(boolean unknown, long leasetime)
	{
		super(unknown, leasetime);
	}
	
	/**
	 *  Get the events.
	 *  @return The events
	 */
	public List<ARegistryResponseEvent> getEvents()
	{
		return events;
	}
	
	/**
	 *  Add an event.
	 *  @param event The event.
	 */
	public void addEvent(ARegistryResponseEvent event)
	{
		if(events==null)
			events = new ArrayList<ARegistryResponseEvent>();
		events.add(event);
	}
	
	/**
	 * Returns the number of elements added to this event.
	 */
	public int size()
	{
		return events==null? 0: events.size();
	}
}
