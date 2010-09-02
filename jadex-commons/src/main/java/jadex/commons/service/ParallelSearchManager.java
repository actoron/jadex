package jadex.commons.service;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SUtil;
import jadex.commons.concurrent.CounterResultListener;
import jadex.commons.concurrent.IResultListener;

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
	protected final LocalSearchManager	LOCAL_SEARCH_MANAGER	= new LocalSearchManager();

	//-------- attributes --------
	
	/** Flag to activate upwards (parent) searching. */
	protected boolean up;
	
	/** Flag to activate downwards (children) searching. */
	protected boolean down;
	
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
		this.up	= up;
		this.down	= down;
	}
	
	//-------- ISearchManager interface --------
	
	/**
	 *  Search for services, starting at the given service provider.
	 *  @param provider	The service provider to start the search at.
	 *  @param decider	The visit decider to select nodes and terminate the search.
	 *  @param selector	The result selector to select matching services and produce the final result. 
	 *  @param services	The local services of the provider (class->list of services).
	 */
	public IFuture	searchServices(IServiceProvider provider, IVisitDecider decider, final IResultSelector selector, Map services, final Collection results)
	{
		final Future	ret	= new Future();
		processNode(null, provider, decider, selector, services, results, up).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				Collection res = (Collection)selector.getResult(results);
				if(res.size()>2 && res.iterator().next().getClass().toString().indexOf("Directory")!=-1)
					System.out.println("here: "+res.size());
				ret.setResult(res);
			}
			
			public void exceptionOccurred(Object source, Exception exception)
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

	//-------- helper methods --------

	/**
	 *  Process a single node (provider).
	 */
	protected IFuture	processNode(final IServiceProvider source, final IServiceProvider provider, final IVisitDecider decider, final IResultSelector selector,
		final Map services, final Collection results, final boolean up)
	{
		final Future	ret	= new Future();
		final boolean[]	finished	= new boolean[3];
		
		if(!selector.isFinished(results) && provider!=null && decider.searchNode(source, provider, results))
		{
//			if(provider!=null)
//				System.out.println("from: "+(source!=null?source.getId():"null")+" proc: "+provider.getId());
			
			provider.getServices(LOCAL_SEARCH_MANAGER, decider, selector, results).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					finished[0]	= true;
					checkAndSetResults(finished, ret);
				}
				
				public void exceptionOccurred(Object source, Exception exception)
				{
					finished[0]	= true;
					checkAndSetResults(finished, ret);
				}
			});

			if(up)
			{
				provider.getParent().addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						IServiceProvider target = (IServiceProvider)result;
						// Do not go back to where we came from.
						if(!SUtil.equals(source, target))
						{
							processNode(provider, target, decider, selector, services, results, up)
								.addResultListener(new IResultListener()
							{
								public void resultAvailable(Object source, Object result)
								{
									finished[1]	= true;
									checkAndSetResults(finished, ret);
								}
								
								public void exceptionOccurred(Object source, Exception exception)
								{
									finished[1]	= true;
									checkAndSetResults(finished, ret);
								}
							});
						}
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						finished[1]	= true;
						checkAndSetResults(finished, ret);
					}
				});
			}
			else
			{
				finished[1]	= true;
				checkAndSetResults(finished, ret);				
			}
			
			if(down)
			{
				provider.getChildren().addResultListener(new IResultListener()
				{
					public void resultAvailable(Object src, Object result)
					{
						if(result!=null)
						{
							Collection	coll	= (Collection)result;
							// Do not go back to where we came from.
							if(source!=null)
								coll.remove(source);
							IResultListener	crl	= new CounterResultListener(coll.size())
							{
								public void finalResultAvailable(Object source, Object result)
								{
									finished[2]	= true;
									checkAndSetResults(finished, ret);
								}
								public void exceptionOccurred(Object source, Exception exception)
								{
									finished[2]	= true;
									checkAndSetResults(finished, ret);
								}
							};
							for(Iterator it=coll.iterator(); it.hasNext(); )
							{
								IServiceProvider target = (IServiceProvider)it.next();
								processNode(provider, target, decider, selector, services, results, false)
									.addResultListener(crl);
							}
						}
						else
						{
							finished[2]	= true;
							checkAndSetResults(finished, ret);				
						}
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						finished[2]	= true;
						checkAndSetResults(finished, ret);
					}
				});
			}
			else
			{
				finished[2]	= true;
				checkAndSetResults(finished, ret);
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	protected static void	checkAndSetResults(boolean[] finished, Future ret)
	{
		synchronized(finished)
		{
			if(finished[0] && finished[1] && finished[2])
			{
				ret.setResult(null);
			}
		}
	}
}
