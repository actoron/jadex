package jadex.bridge.service.search;

import jadex.bridge.service.IServiceProvider;
import jadex.commons.SUtil;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 *  Searches up and/or down the provider tree in parallel (if results are provided in parallel).
 *  If both up and down are activated, the visit decider needs a mechanism
 *  to avoid nodes being checked twice to avoid infinite loops.
 */
public class ParallelSearchManager implements ISearchManager
{
	//-------- constants --------
	
	/** The local search manager. */
	protected static final LocalSearchManager	LOCAL_SEARCH_MANAGER	= new LocalSearchManager();

	/** The local search manager. */
	protected static final LocalSearchManager	LOCAL_SEARCH_MANAGER_FORCED	= new LocalSearchManager(true);

	
	//-------- attributes --------
	
	/** Flag to activate upwards (parent) searching. */
	protected boolean up;
	
	/** Flag to activate downwards (children) searching. */
	protected boolean down;
	
	/** Force search flag. */
	protected boolean forcedsearch;
	
	/** The local search manager. */
	protected ISearchManager lsm;
	
	//-------- constructors --------
	
	/**
	 *  Create a new search manager.
	 */
	public ParallelSearchManager()
	{
		this(true, true);
	}
	
	/**
	 *  Create a new search manager.
	 */
	public ParallelSearchManager(boolean up, boolean down)
	{
		this(up, down, false);
	}
	
	/**
	 *  Create a new search manager.
	 */
	public ParallelSearchManager(boolean up, boolean down, boolean forcedsearch)
	{
		this.up	= up;
		this.down	= down;
		this.forcedsearch = forcedsearch;
		this.lsm = forcedsearch? LOCAL_SEARCH_MANAGER_FORCED: LOCAL_SEARCH_MANAGER;
	}
	
	//-------- ISearchManager interface --------
	
	/**
	 *  Search for services, starting at the given service provider.
	 *  @param provider	The service provider to start the search at.
	 *  @param decider	The visit decider to select nodes and terminate the search.
	 *  @param selector	The result selector to select matching services and produce the final result. 
	 *  @param services	The local services of the provider (class->list of services).
	 */
	public IIntermediateFuture	searchServices(IServiceProvider provider, IVisitDecider decider, 
		final IResultSelector selector, Map services)
	{
		final IntermediateFuture	ret	= new IntermediateFuture();
		processNode(provider, null, provider, decider, selector, services, up, false, ret).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
//				Collection res = selector.getResult(results);
//				if(res.size()>2 && res.iterator().next().getClass().toString().indexOf("Directory")!=-1)
//					System.out.println("here: "+res.size());
//				ret.setResult(results);
//				ret.setResult(result);
				ret.setFinished();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		return ret;
	}
	
	/**
	 *  Get the cache key.
	 *  Needs to identify this element with respect to its important features so that
	 *  two equal elements should return the same key.
	 */
	public Object getCacheKey()
	{
		return getClass().getName()+up+down;
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
	
	//-------- helper methods --------

	/**
	 *  Process a single node (provider).
	 */
	protected IFuture processNode(final IServiceProvider start, final IServiceProvider source, final IServiceProvider provider, final IVisitDecider decider, 
		final IResultSelector selector, final Map services, final boolean up, boolean ischild, final IntermediateFuture endret)
	{
		final Future ret	= new Future();
		final boolean[]	finished	= new boolean[3];
		
		if(!selector.isFinished(endret.getIntermediateResults()) && provider!=null 
			&& decider.searchNode(start, source, provider, ischild, endret.getIntermediateResults()))
		{
//			if(provider!=null && selector instanceof TypeResultSelector && ((TypeResultSelector)selector).getType().getName().indexOf("Component")!=-1)
//				System.out.println("from: "+(source!=null?source.getId():"null")+" proc: "+provider.getId());
			
			provider.getServices(lsm, decider, selector).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					if(result!=null)
					{
						for(Iterator it=((Collection)result).iterator(); it.hasNext(); )
						{
							Object next = it.next();
							
							// Must recheck if already finished (otherwise duplicate results may occur).
							if(!endret.getIntermediateResults().contains(next)
								&& !selector.isFinished(endret.getIntermediateResults())) 
							{
//								System.out.println("found: "+next);
								endret.addIntermediateResult(next);
							}
						}
					}
					checkAndSetResults(ret, finished, 0);
				}
				
				public void exceptionOccurred(Exception exception)
				{
					checkAndSetResults(ret, finished, 0);
				}
			});

			if(up)
			{
				provider.getParent().addResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						IServiceProvider target = (IServiceProvider)result;
						// Do not go back to where we came from.
						if(!SUtil.equals(source, target))
						{
							processNode(start, provider, target, decider, selector, services, up, false, endret)
								.addResultListener(new IResultListener()
							{
								public void resultAvailable(Object result)
								{
									checkAndSetResults(ret, finished, 1);
								}
								
								public void exceptionOccurred(Exception exception)
								{
									checkAndSetResults(ret, finished, 1);
								}
							});
						}
						else
						{
							checkAndSetResults(ret, finished, 1);
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
						checkAndSetResults(ret, finished, 1);
					}
				});
			}
			else
			{
				checkAndSetResults(ret, finished, 1);
			}
			
			if(down)
			{
				provider.getChildren().addResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						if(result!=null)
						{
							Collection	coll	= (Collection)result;
							// Do not go back to where we came from.
							if(source!=null)
								coll.remove(source);
							IResultListener	crl	= new CounterResultListener(coll.size(), new IResultListener()
							{
								public void resultAvailable(Object result)
								{
									checkAndSetResults(ret, finished, 2);
								}
								
								public void exceptionOccurred(Exception exception)
								{
									checkAndSetResults(ret, finished, 2);
								}
							});
							for(Iterator it=coll.iterator(); it.hasNext(); )
							{
								IServiceProvider target = (IServiceProvider)it.next();
								processNode(start, provider, target, decider, selector, services, false, true, endret)
									.addResultListener(crl);
							}
						}
						else
						{
							checkAndSetResults(ret, finished, 2);
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
						checkAndSetResults(ret, finished, 2);
					}
				});
			}
			else
			{
				checkAndSetResults(ret, finished, 2);
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}

	/**
	 *  Test if all items are finished and set the result.
	 */
	protected void checkAndSetResults(Future ret, boolean[] finished, int num)
	{
		boolean	set;
		synchronized(finished)
		{
			finished[num]	= true;
			set	= finished[0] && finished[1] && finished[2];
		}
		if(set)
			ret.setResult(null);
	}
}
