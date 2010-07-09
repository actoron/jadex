package jadex.service;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.IResultListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 *  Search for services at the local provider and all parents. 
 */
public class UpwardsSequentialSearchManager implements ISearchManager
{
	/**
	 *  Search for services, starting at the given service provider.
	 *  @param provider	The service provider to start the search at.
	 *  @param decider	The visit decider to select nodes and terminate the search.
	 *  @param selector	The result selector to select matching services and produce the final result. 
	 *  @param services	The local services of the provider (class->list of services).
	 */
	public IFuture	searchServices(IServiceProvider provider, IVisitDecider decider, IResultSelector selector, Map services)
	{
		Future	ret	= new Future();
		Collection	results	= Collections.synchronizedList(new ArrayList());
		LocalSearchManager	lsm	= new LocalSearchManager(results);
		processNode(provider, decider, selector, services, ret, results, lsm);
		return ret;
	}

	protected void processNode(final IServiceProvider provider, final IVisitDecider decider, final IResultSelector selector, final Map services,
		final Future ret, final Collection results, final LocalSearchManager lsm)
	{
		if(provider!=null && decider.searchNode(null, provider, results))
		{
			provider.getServices(lsm, decider, selector).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					provider.getParent().addResultListener(new IResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							processNode((IServiceProvider)result, decider, selector, services, ret, results, lsm);
						}
						
						public void exceptionOccurred(Object source, Exception exception)
						{
							ret.setException(exception);
						}
					});
				}
				
				public void exceptionOccurred(Object source, Exception exception)
				{
					ret.setException(exception);
				}
			});			
		}
		else
		{
			ret.setResult(selector.getResult(results));
		}
	}
}
