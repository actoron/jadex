package jadex.extension.envsupport.environment;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class LocalObjectListener implements IObjectListener
{
	private List eventqueue;
	
	public LocalObjectListener()
	{
		eventqueue = Collections.synchronizedList(new LinkedList());
	}
	
	/**
	 * This event gets called when an environment object event is triggered.
	 */
	public void dispatchObjectEvent(ObjectEvent event)
	{
		eventqueue.add(event);
	}
	
	/**
	 *  Returns the next event or null if none is available.
	 *  @return event or null
	 */
	public ObjectEvent getNextEvent()
	{
		synchronized(eventqueue)
		{
			if (eventqueue.isEmpty())
				return null;
			else
				return (ObjectEvent) eventqueue.remove(0);
		}
	}
	
	/**
	 * Waits for a specific event.
	 * 
	 */

}
