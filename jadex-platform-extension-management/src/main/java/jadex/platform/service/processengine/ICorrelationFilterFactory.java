package jadex.platform.service.processengine;

import jadex.commons.IFilter;
import jadex.rules.eca.IEvent;

/**
 * 
 */
public interface ICorrelationFilterFactory
{
	/**
	 *  Create a correlation filter.
	 *  @param event The initial event.
	 */
	public IFilter<IEvent> createCorrelationFilter(IEvent event);
}
