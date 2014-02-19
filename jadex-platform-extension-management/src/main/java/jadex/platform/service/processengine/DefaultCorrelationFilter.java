package jadex.platform.service.processengine;

import jadex.commons.IFilter;
import jadex.rules.eca.IEvent;

/**
 *  Default correlation filter.
 */
public class DefaultCorrelationFilter implements IFilter<IEvent>
{
	/** The set of already consumed events. */
	protected IEventIdExtractor extractor;
	
	/** The extracted id. */
	protected Object id;
	
	/**
	 *  Create a new correlation filter.
	 */
	public DefaultCorrelationFilter()
	{
	}
	
	/**
	 *  Create a new correlation filter.
	 */
	public DefaultCorrelationFilter(IEvent event, IEventIdExtractor extractor)
	{
		this.extractor = extractor;
		this.id = extractor.extractId(event);
	}
	
	/**
	 *  Test if event should be consumed.
	 */
	public boolean filter(IEvent event)
	{
		return id.equals(extractor.extractId(event));
	}
}
