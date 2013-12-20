package jadex.bridge.service.search;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceProvider;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.TerminableIntermediateFuture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 *  Search for services at the local provider and all parents. 
 */
public class LocalSearchManager implements ISearchManager
{
	//-------- attributes --------
	
	/** Force search flag. */
	protected boolean forcedsearch;
	
	//-------- constructors --------
	
	/**
	 *  Create a new local search manager.
	 */
	public LocalSearchManager()
	{
		this(false);
	}
	
	/**
	 *  Create a new local search manager.
	 */
	public LocalSearchManager(boolean forcedsearch)
	{
		this.forcedsearch = forcedsearch;
	}
	
	//-------- ISearchManager interface --------
	
	/**
	 *  Search for services, starting at the given service provider.
	 *  @param provider	The service provider to start the search at.
	 *  @param decider	The visit decider to select nodes and terminate the search.
	 *  @param selector	The result selector to select matching services and produce the final result. 
	 *  @param services	The local services of the provider (class->list of services).
	 */
	public ITerminableIntermediateFuture<IService>	searchServices(IServiceProvider provider, IVisitDecider decider, IResultSelector selector, Map<Class<?>, Collection<IService>> services)
	{
		TerminableIntermediateFuture<IService> ret = new TerminableIntermediateFuture<IService>();
		
//		if(selector instanceof TypeResultSelector && ((TypeResultSelector)selector).getType().toString().indexOf("IIntermediateResultService")!=-1)
//		{
//			System.out.println("sefuill ksd");
//		}
		
		// local search is always allowed?!
		// problem: first gsm searches a node, then lsm searches the same node = double visit
//		if(!selector.isFinished(results))// && decider.searchNode(null, provider, results))
		
		selector.selectServices(services)
			.addResultListener(new DelegationResultListener<Collection<IService>>(ret));
		
//		if(selector instanceof TypeResultSelector && results.toString().indexOf("Add")!=-1)
//			System.out.println("lsm: "+provider+" "+results);
		return ret;
	}
	
	/**
	 *  Get the cache key.
	 *  Needs to identify this element with respect to its important features so that
	 *  two equal elements should return the same key.
	 */
	public Object getCacheKey()
	{
		// Do not cache local results.
		return null;
	}

	/**
	 *  Get the forcedsearch.
	 *  @return The forcedsearch.
	 */
	public boolean isForcedSearch()
	{
		return forcedsearch;
	}
	
	/**
	 *  Set the forcedsearch.
	 *  @param forcedsearch The forcedsearch to set.
	 */
	public void setForcedSearch(boolean forcedsearch)
	{
		this.forcedsearch = forcedsearch;
	}

}
