package jadex.bridge.nonfunctional;

import jadex.bridge.service.IService;
import jadex.commons.future.IFuture;

import java.util.List;

/**
 *  Interfaces for non-functional ranking mechanism for services.
 *
 */
public interface IServiceRanker
{
	/**
	 *  Ranks services according to non-functional criteria.
	 *  
	 *  @param unrankedservices Unranked list of services.
	 *  @return Ranked list of services.
	 */
	public IFuture<List<IService>> rank(List<IService> unrankedservices);
}
