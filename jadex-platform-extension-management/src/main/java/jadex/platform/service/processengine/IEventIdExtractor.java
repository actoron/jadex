package jadex.platform.service.processengine;

import jadex.rules.eca.IEvent;

/**
 *  An event id extractor is responsible for extracting an id from an event.
 */
public interface IEventIdExtractor
{
	/**
	 *  Extract the id from an event.
	 *  @param event The event.
	 *  @return The id.
	 */
	public Object extractId(IEvent event);
}
