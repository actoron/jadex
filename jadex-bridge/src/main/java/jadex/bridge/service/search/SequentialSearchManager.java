package jadex.bridge.service.search;

import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceProvider;
import jadex.commons.SUtil;
import jadex.commons.future.FutureTerminatedException;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.TerminableIntermediateDelegationFuture;
import jadex.commons.future.TerminableIntermediateDelegationResultListener;
import jadex.commons.future.TerminableIntermediateFuture;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Sequentially searches up and/or down the provider tree. 
 *  If both up and down are activated, the visit decider needs a mechanism
 *  to avoid nodes being checked twice to avoid infinite loops.
 */
public class SequentialSearchManager implements ISearchManager
{
	//-------- constants --------
	
	/** Key for current list of children. */
	protected static final String	CURRENT_CHILDREN	= "current-children";
	
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
	public SequentialSearchManager()
	{
		this(true, true);
	}
	
	/**
	 *  Create a new search manager.
	 */
	public SequentialSearchManager(boolean up, boolean down)
	{
		this(up, down, false);
	}
	
	/**
	 *  Create a new search manager.
	 */
	public SequentialSearchManager(boolean up, boolean down, boolean forcedsearch)
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
	public ITerminableIntermediateFuture<IService> searchServices(final IServiceProvider provider, IVisitDecider decider, 
		final IResultSelector selector, Map<Class<?>, Collection<IService>> services)
	{
		final TerminableIntermediateFuture<IService> ret = new TerminableIntermediateFuture<IService>();
		final TerminableIntermediateDelegationFuture<IService> del = new TerminableIntermediateDelegationFuture<IService>();
		ret.addResultListener(new TerminableIntermediateDelegationResultListener<IService>(del, ret)
		{
			boolean tried = false;
			
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
//				System.out.println("search end fin: "+ret.hashCode() + " "  + tried);
//				if (selector instanceof TypeResultSelector)
//				{
//					System.out.println(((TypeResultSelector) selector).getType());
//				}
				opencalls.remove(ret);
				super.finished();
			}
			
			public void exceptionOccurred(Exception exception)
			{
//				System.out.println("search end ex: "+ret.hashCode() + " "  + tried);
//				if (selector instanceof TypeResultSelector)
//				{
//					if (((TypeResultSelector) selector).getType().getName().indexOf("IClockService") != -1)
//					{
//						Thread.dumpStack();
//					}
//					System.out.println(((TypeResultSelector) selector).getType());
//				}
				opencalls.remove(ret);
				// If the future was terminated it is changed to finished to avoid
				// getting exceptions outside as termination is used also internally
				// to cut off the search when enough results have been found.
				if(exception instanceof FutureTerminatedException &&
				   ret.getIntermediateResults().size()>0 && !tried)
				{
					tried = true;
					finished();
				}
				else
				{
					super.exceptionOccurred(exception);
				}
			}
		});

//		System.out.println("search: "+selector+" "+provider.getId()+" "+ret.hashCode());
//		System.out.println("search: "+ret.hashCode());
	
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
		
		Map<Object, Object>	todo	= new LinkedHashMap<Object, Object>(); // Nodes of which children still to be processed (id->provider).
		SearchContext	context	= new SearchContext(decider, selector, todo);
//		final List res = new ArrayList();
		
//		List<String>	search	= new ArrayList<String>();
//		StringWriter	sw	= new StringWriter();
//		new RuntimeException().printStackTrace(new PrintWriter(sw));
//		search.add("search: "+this+"/"+provider+"/"+decider+"/"+selector+"\n"+sw);
//		SEARCH.set(search);

		processNode(provider, null, provider, context, ret, up, 0, false);//, res);
		
//		SEARCH.set(null);

//		ret.addResultListener(new DefaultResultListener()
//		{
//			public void resultAvailable(Object source, Object result)
//			{
//				System.out.println("DEBUG searchServices: "+selector+" "+res);
//			}
//		});
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

	//-------- helper methods --------

	/**
	 *  Process a single node (provider).
	 */
	protected void processNode(final IServiceProvider start, final IServiceProvider source, final IServiceProvider provider,
		final SearchContext context, final TerminableIntermediateFuture<IService> ret, final boolean up, final int callstack, final boolean ischild)//, final List res)
	{
		// If terminated return
		
//		if(context.selector instanceof TypeResultSelector && ((TypeResultSelector)context.selector).getType().getName().indexOf("IComponent")!=-1)
//			System.out.println("processNode: "+context+", "+source+", "+provider);

//		if(provider!=null && provider.getId().toString().startsWith("Lars-PC"))
//			System.out.println("processing node: "+provider.getId());
//		if(context.selector instanceof TypeResultSelector && ((TypeResultSelector)context.selector).getType().toString().indexOf("IComponentMana")!=-1)
//			System.out.println("processing: "+provider.getId());

		
//		final List<String>	search	= SEARCH.get();
		
		// Hack!!! Break call stack when it becomes too large (limit ~150 for android devices?).	
		if(callstack>100)
		{
			new Thread(new Runnable()
			{
				public void run()
				{
//					SEARCH.set(search);
					processNode(start, source, provider, context, ret, up, 0, ischild);//, res);
//					SEARCH.set(null);
				}
			}).start();
			return;
		}
		
//		final List found = new ArrayList();
//		if(res!=null && provider!=null)
//		{
//			res.add(new Tuple(provider.getId(), found));
//		}
				
		// If node is to be searched, continue with this node.
//		if(context.selector instanceof TypeResultSelector && ((TypeResultSelector)context.selector).getType().getName().indexOf("Add")!=-1)
//		{
//			System.out.println("proc node: "+provider+" coming from: "+source);
//			if(provider!=null && provider.toString().indexOf("Calculate0")!=-1)
//			{
//				System.out.println("here");
//			}
//		}
//		if(!context.selector.isFinished(ret.getIntermediateResults()) && provider!=null)
		if(!checkFinished(context.selector, ret) && provider!=null)
		{
			if(down)
			{
				// Child nodes of this node still have to be searched.
				context.todo.put(provider.getId(), new Object[]{provider, source});
			}
			
			if(context.decider.searchNode(start==null? null: start.getId(), 
				source==null? null: source.getId(), provider.getId(), 
				ret.getIntermediateResults()))
			{
//				search.add(provider.getId().toString());
				
				// Use fut.isDone() to reduce stack depth
				final ITerminableIntermediateFuture<IService> future = provider.getServices(lsm, context.decider, context.selector);
				
				if(!future.isDone())
				{
					addOpenCall(ret, future);
					
					future.addResultListener(new IIntermediateResultListener<IService>()
					{
						public void intermediateResultAvailable(IService result)
						{
							ret.addIntermediateResult(result);
						}
						
						public void finished()
						{
//							SEARCH.set(search);
							removeOpenCall(ret, future);
							processParent(start, source, provider, context, ret, up, 0);//, res);
//							SEARCH.set(null);
						}
						
						public void resultAvailable(Collection<IService> res)
						{
							removeOpenCall(ret, future);
							
//							System.out.println("found: "+context+", "+provider.getId()+" "+result);
							if(res!=null)
							{
//								Collection res = (Collection)result;
								for(Iterator<IService> it=res.iterator(); it.hasNext(); )
								{
									IService o = it.next();
//									System.out.println("found: "+provider.getId()+" "+o);
									ret.addIntermediateResult(o);
								}
							}
//							SEARCH.set(search);
							processParent(start, source, provider, context, ret, up, 0);//, res);
//							SEARCH.set(null);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							removeOpenCall(ret, future);
							
//							System.out.println("nothing found: "+provider.getId());
							// do not terminate serach when one node fails
							// what to do with exception?
							resultAvailable(null);
//							ret.setException(exception);
						}
					});
				}
				else
				{
//					if(res!=null)
//						found.addAll(context.results);
					Collection<IService> res = null;
					try
					{
						res = future.get(null);
					}
					catch(Exception e)
					{
						// what to do with exception?
					}
//					System.out.println("found: "+context+", "+provider.getId()+" "+res);
					if(res!=null && res.size()>0)
					{
						for(Iterator<IService> it=res.iterator(); it.hasNext(); )
						{
							IService o = it.next();
//							System.out.println("found: "+provider.getId()+" "+o);
							ret.addIntermediateResult(o);
						}
					}
//					else
//					{
//						if(context.selector instanceof TypeResultSelector && ((TypeResultSelector)context.selector).getType().toString().indexOf("IComponentMana")!=-1)
//							System.out.println("nothing found: "+provider.getId());
//					}
					processParent(start, source, provider, context, ret, up, callstack+1);//, res);
				}
			}
			else if(up)
			{
				// Do not perform local search
				processParent(start, source, provider, context, ret, up, callstack+1);//, res);
			}
			else
			{
				// Continue with child nodes from todo list (if any).
				processChildNodes(start, provider, context, ret, callstack+1);//, res);
			}
		}
		else
		{
			// Continue with child nodes from todo list (if any).
			processChildNodes(start, provider, context, ret, callstack+1);//, res);
		}
	}

	/**
	 *  Continue search with the parent of the current node (if any).
	 */
	protected void processParent(final IServiceProvider start, final IServiceProvider source, final IServiceProvider provider,
		final SearchContext context, final TerminableIntermediateFuture<IService> ret, final boolean up, final int callstack)//, final List res)
	{
//		final List<String>	search	= SEARCH.get();
		
		// When searching upwards, continue with parent.
//		if(!context.selector.isFinished(ret.getIntermediateResults()) && up)
		if(!checkFinished(context.selector, ret) && up)
		{
			// Use fut.isDone() to reduce stack depth
			IFuture<IServiceProvider>	future	= provider.getParent();
			if(!future.isDone())
			{
				future.addResultListener(new IResultListener<IServiceProvider>()
				{
					public void resultAvailable(IServiceProvider result)
					{
						// Cut search if parent was already visisted.
						if(SUtil.equals(source, result))
							result = null;
//						SEARCH.set(search);
						processNode(start, provider, (IServiceProvider)result, context, ret, up, 0, false);
//						SEARCH.set(null);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						ret.setException(exception);
					}
				});
			}
			else
			{
				try
				{
					// Cut search if parent was already visisted.
					Object	result	= future.get(null);
					if(SUtil.equals(source, result))
						result = null;
//					SEARCH.set(search);
					processNode(start, provider, (IServiceProvider)result, context, ret, up, callstack+1, false);
//					SEARCH.set(null);
					
				}
				catch(Exception exception)
				{
					ret.setException(exception);
				}
			}
		}

		// Else continue with child nodes from todo list (if any).
		else
		{
			processChildNodes(start, provider, context, ret, callstack+1);//, res);
		}
	}

	/**
	 *  Process child nodes from the todo list.
	 */
	protected void processChildNodes(final IServiceProvider start, final IServiceProvider provider,
		final SearchContext context, final TerminableIntermediateFuture<IService> ret, final int callstack)//, final List res)
	{
//		final List<String>	search	= SEARCH.get();
		
		// Finished, when no more todo nodes.
//		if(context.selector.isFinished(ret.getIntermediateResults()) || context.todo.isEmpty())
		if(context.todo.isEmpty() || checkFinished(context.selector, ret))
		{
//			ret.setResult(context.selector.getResult(context.results));
//			ret.setResult(context.results);
			ret.setFinishedIfUndone();
//			if(context.selector instanceof TypeResultSelector && ((TypeResultSelector)context.selector).getType().toString().indexOf("IComponentMana")!=-1)
//				System.out.println("found "+ret.getIntermediateResults());
		}
		
		// Continue with current list of children (if any)
		else if(context.todo.containsKey(CURRENT_CHILDREN))
		{
			List<IServiceProvider>	ccs	= (List<IServiceProvider>)context.todo.get(CURRENT_CHILDREN);
			IServiceProvider	child	= ccs.remove(0);
			if(ccs.isEmpty())
			{
				context.todo.remove(CURRENT_CHILDREN);
			}
			
			// Set 'up' to false, once traversing children has started.
			processNode(start, provider, child, context, ret, false, callstack+1, true);
		}

		// Else pick entry from todo list and continue with its children.
		else
		{
			Object	next	= context.todo.keySet().iterator().next();
			final Object[] prov = (Object[])context.todo.remove(next);
			final IServiceProvider	provi = (IServiceProvider)prov[0];
			final IServiceProvider	src = (IServiceProvider)prov[1];
			IFuture<Collection<IServiceProvider>> future = provi.getChildren();
			
			// Use fut.isDone() to reduce stack depth
			if(!future.isDone())
			{
				future.addResultListener(new IResultListener<Collection<IServiceProvider>>()
				{
					public void resultAvailable(Collection<IServiceProvider> result)
					{
//						SEARCH.set(search);
						addChildren(start, src, provi, context, ret, result, 0);//, res);
//						SEARCH.set(null);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						// Continue processing children even if one is terminated ?
//						processChildNodes(start, provi, context, ret, callstack+1);//, res);
						ret.setException(exception);
					}
				});
			}
			else
			{
				try
				{
					Collection<IServiceProvider> result = future.get(null);
					addChildren(start, src, provi, context, ret, result, callstack+1);//, res);
				}
				catch(Exception exception)
				{
					// Continue processing children even if one is terminated
//					System.out.println("exe: "+exception);
//					processChildNodes(start, provi, context, ret, callstack+1);//, res);
					ret.setException(exception);
				}
			}
		}
	}
	
	/**
	 *  Add children to the current search.
	 */
	protected void addChildren(IServiceProvider start, IServiceProvider source, IServiceProvider provider,
		SearchContext context, TerminableIntermediateFuture<IService> ret, Collection<IServiceProvider> children, int callstack)//, List res)
	{
//		if(!context.selector.isFinished(ret.getIntermediateResults()) && children!=null && !children.isEmpty())
		if(!checkFinished(context.selector, ret) && children!=null && !children.isEmpty())
		{
			List<IServiceProvider>	ccs	= new LinkedList<IServiceProvider>(children);
			ccs.remove(source);
			if(!ccs.isEmpty())
			{
				context.todo.put(CURRENT_CHILDREN, ccs);
			}
		}
		processChildNodes(start, provider, context, ret, callstack+1);//, res);
	}


	/**
	 *  Get the up flag.
	 *  @return The up flag.
	 */
	public boolean isUp()
	{
		return up;
	}

	/**
	 *  Set the up flag.
	 *  @param up The up flag to set.
	 */
	public void setUp(boolean up)
	{
		this.up = up;
	}

	/**
	 *  Get the down flag.
	 *  @return the down.
	 */
	public boolean isDown()
	{
		return down;
	}

	/**
	 *  Set the down flag.
	 *  @param down The down to set.
	 */
	public void setDown(boolean down)
	{
		this.down = down;
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
	
	/**
	 *  Add an open call.
	 *  An open call is a search on a node.
	 */
	public void addOpenCall(TerminableIntermediateFuture<IService> ret, ITerminableIntermediateFuture<IService> oc)
	{
		Set<ITerminableIntermediateFuture<IService>> ocs = opencalls.get(ret);
		if(ocs==null)
			ocs = new HashSet<ITerminableIntermediateFuture<IService>>();
		ocs.add(oc);
	}
	
	/**
	 *  Remove an open call.
	 *  An open call is a search on a node.
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

	
	//-------- helper classes --------
	
	/**
	 *  Struct for data that remains constant during search.
	 *  Used to reduce stack memory usage.
	 */
	public static class SearchContext
	{
		public IVisitDecider	decider;
		public IResultSelector	selector;
//		public Collection	results;
		public Map<Object, Object>	todo;
		
		public SearchContext(IVisitDecider decider, IResultSelector selector, Map<Object, Object> todo)
		{
			this.decider	= decider;
			this.selector	= selector;
//			this.results	= results;
			this.todo	= todo;
		}
	}
}
