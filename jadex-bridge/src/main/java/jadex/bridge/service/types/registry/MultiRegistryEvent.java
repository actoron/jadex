package jadex.bridge.service.types.registry;

import java.util.ArrayList;
import java.util.List;

/**
 * 
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
}
