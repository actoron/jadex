package jadex.bridge.service.search;

import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceProvider;
import jadex.commons.SUtil;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureTerminatedException;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.TerminableIntermediateDelegationFuture;
import jadex.commons.future.TerminableIntermediateDelegationResultListener;
import jadex.commons.future.TerminableIntermediateFuture;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
	
	/** The open search calls. */
	protected Map<ITerminableIntermediateFuture<IService>, Set<ITerminableIntermediateFuture<IService>>> opencalls;
	
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
		this.opencalls = new HashMap<ITerminableIntermediateFuture<IService>, Set<ITerminableIntermediateFuture<IService>>>();
	}
	
	//-------- ISearchManager interface --------
	
	/**
	 *  Search for services, starting at the given service provider.
	 *  @param provider	The service provider to start the search at.
	 *  @param decider	The visit decider to select nodes and terminate the search.
	 *  @param selector	The result selector to select matching services and produce the final result. 
	 *  @param services	The local services of the provider (class->list of services).
	 */
	public ITerminableIntermediateFuture<IService>	searchServices(IServiceProvider provider, IVisitDecider decider, 
		final IResultSelector selector, Map<Class<?>, Collection<IService>> services)
	{
		final TerminableIntermediateFuture<IService> ret = new TerminableIntermediateFuture<IService>();
		final TerminableIntermediateDelegationFuture<IService> del = new TerminableIntermediateDelegationFuture<IService>();
		ret.addResultListener(new TerminableIntermediateDelegationResultListener<IService>(del, ret)
		{
			public void customResultAvailable(Collection<IService> result)
			{
//				System.out.println("search end: "+ret.hashCode());
				opencalls.remove(ret);
				super.resultAvailable(result);
			}
			
			public void customIntermediateResultAvailable(IService result) 
			{
				super.customIntermediateResultAvailable(result);
			}
			
			public void finished() 
			{
				opencalls.remove(ret);
				super.finished();
			}
			
			public void exceptionOccurred(Exception exception)
			{
//				System.out.println("search end: "+ret.hashCode());
				opencalls.remove(ret);
				// If the future was terminated it is changed to finished to avoid
				// getting exceptions outside as termination is used also internally
				// to cut off the search when enough results have been found.
				if(exception instanceof FutureTerminatedException)
				{
					if(ret.getIntermediateResults().size()>0)
					{
						finished();
					}
					else
					{
						super.exceptionOccurred(exception);
					}
				}
			}
		});
		
		ret.setTerminationCommand(new ITerminationCommand()
		{
			public void terminated(Exception reason)
			{
				Set<ITerminableIntermediateFuture<IService>> ocs = opencalls.get(ret);
				if(ocs!=null)
				{
					for(ITerminableIntermediateFuture<IService> fut: ocs)
					{
						fut.terminate(reason);
					}
				}
				opencalls.remove(ret);
			}
			
			public boolean checkTermination(Exception reason)
			{
				return true;
			}
		});
		
		processNode(provider, null, provider, decider, selector, services, up, ret)
			.addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
//				Collection res = selector.getResult(results);
//				if(res.size()>2 && res.iterator().next().getClass().toString().indexOf("Directory")!=-1)
//					System.out.println("here: "+res.size());
//				ret.setResult(results);
//				ret.setResult(result);
				ret.setFinishedIfUndone();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// The search is considered as success if at least
				// one service could be found.
				// Otherwise search in a disconnected platform could
				// raise a timeout exception which is propagated here.
				if(ret.getIntermediateResults().size()>0)
				{
					ret.setFinishedIfUndone();
				}
				else
				{
					ret.setExceptionIfUndone(exception);
				}
			}
		});
		return del;
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
	protected IFuture<Void> processNode(final IServiceProvider start, final IServiceProvider source, final IServiceProvider provider, final IVisitDecider decider, 
		final IResultSelector selector, final Map<Class<?>, Collection<IService>> services, final boolean up, final TerminableIntermediateFuture<IService> endret)
	{
		final Future<Void> ret	= new Future<Void>();
		final boolean[]	finished	= new boolean[3];
		
//		if(!selector.isFinished(endret.getIntermediateResults()) && provider!=null 
		if(!checkFinished(selector, endret) && provider!=null 
			&& decider.searchNode(start==null? null: start.getId(), source==null? null: source.getId(), provider==null? null: provider.getId(), endret.getIntermediateResults()))
		{
//			if(provider!=null && selector instanceof TypeResultSelector && ((TypeResultSelector)selector).getType().getName().indexOf("Component")!=-1)
//				System.out.println("from: "+(source!=null?source.getId():"null")+" proc: "+provider.getId());
			
			final ITerminableIntermediateFuture<IService> fut = provider.getServices(lsm, decider, selector);
			addOpenCall(endret, fut);
			fut.addResultListener(new IResultListener<Collection<IService>>()
			{
				public void resultAvailable(Collection<IService> result)
				{
					removeOpenCall(endret, fut);
					if(result!=null)
					{
						for(Iterator<IService> it=((Collection<IService>)result).iterator(); it.hasNext(); )
						{
							IService next = it.next();
							
							// Must recheck if already finished (otherwise duplicate results may occur).
							if(!endret.getIntermediateResults().contains(next)
//								&& !selector.isFinished(endret.getIntermediateResults())) 
								&& !checkFinished(selector, endret)) 
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
					removeOpenCall(endret, fut);
					checkAndSetResults(ret, finished, 0);
				}
			});

			if(up)
			{
				provider.getParent().addResultListener(new IResultListener<IServiceProvider>()
				{
					public void resultAvailable(IServiceProvider target)
					{
//						IServiceProvider target = (IServiceProvider)result;
						// Do not go back to where we came from.
						if(!SUtil.equals(source, target))
						{
							processNode(start, provider, target, decider, selector, services, up, endret)
								.addResultListener(new IResultListener<Void>()
							{
								public void resultAvailable(Void result)
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
				provider.getChildren().addResultListener(new IResultListener<Collection<IServiceProvider>>()
				{
					public void resultAvailable(Collection<IServiceProvider> coll)
					{
						if(coll!=null)
						{
//							Collection	coll	= (Collection)result;
							// Do not go back to where we came from.
							if(source!=null)
								coll.remove(source);
							IResultListener<Void>	crl	= new CounterResultListener<Void>(coll.size(), new IResultListener<Void>()
							{
								public void resultAvailable(Void result)
								{
									checkAndSetResults(ret, finished, 2);
								}
								
								public void exceptionOccurred(Exception exception)
								{
									checkAndSetResults(ret, finished, 2);
								}
							});
							for(Iterator<IServiceProvider> it=coll.iterator(); it.hasNext(); )
							{
								IServiceProvider target = (IServiceProvider)it.next();
								processNode(start, provider, target, decider, selector, services, false, endret)
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
	protected void checkAndSetResults(Future<Void> ret, boolean[] finished, int num)
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
	
	/**
	 * 
	 */
	public void addOpenCall(TerminableIntermediateFuture<IService> ret, ITerminableIntermediateFuture<IService> oc)
	{
		Set<ITerminableIntermediateFuture<IService>> ocs = opencalls.get(ret);
		if(ocs==null)
			ocs = new HashSet<ITerminableIntermediateFuture<IService>>();
		ocs.add(oc);
	}
	
	/**
	 * 
	 */
	public void removeOpenCall(TerminableIntermediateFuture<IService> ret, ITerminableIntermediateFuture<IService> oc)
	{
		Set<ITerminableIntermediateFuture<IService>> ocs = opencalls.get(ret);
		if(ocs!=null)
		{
			ocs.remove(oc);
		}
	}
	
	/**
	 *  Checks if the search is finished and then
	 *  automatically terminates the search future.
	 */
	protected boolean checkFinished(IResultSelector ressel, TerminableIntermediateFuture<IService> fut)
	{
		boolean ret = ressel.isFinished(fut.getIntermediateResults());
		if(ret)
		{
			fut.terminate();
		}
		return ret;	
	}
}
