package jadex.commons.service;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IResultListener;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	}
	
	//-------- ISearchManager interface --------
	
	/**
	 *  Search for services, starting at the given service provider.
	 *  @param provider	The service provider to start the search at.
	 *  @param decider	The visit decider to select nodes and terminate the search.
	 *  @param selector	The result selector to select matching services and produce the final result. 
	 *  @param services	The local services of the provider (class->list of services).
	 */
	public IFuture	searchServices(IServiceProvider provider, IVisitDecider decider, IResultSelector selector, Map services, Collection results)
	{
//		System.out.println("search: "+selector+" "+provider.getId());
		Future	ret	= new Future();
		Map	todo	= new LinkedHashMap(); // Nodes of which children still to be processed (id->provider).
		SearchContext	context	= new SearchContext(decider, selector, results, todo);
		processNode(null, provider, context, ret, up, 0);
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
	protected void processNode(final IServiceProvider source, final IServiceProvider provider,
		final SearchContext context, final Future ret, final boolean up, final int callstack)
	{
		// Hack!!! Break call stack when it becomes too large.
		if(callstack>1000)
		{
			new Thread(new Runnable()
			{
				public void run()
				{
					processNode(source, provider, context, ret, up, 0);
				}
			}).start();
			return;
		}
		
		boolean dochildren = false;
		
		// If node is to be searched, continue with this node.
//		System.out.println("proc node: "+source+" "+provider);
		if(!context.selector.isFinished(context.results) && provider!=null)
		{
			if(down)
			{
				// Child nodes of this node still have to be searched.
				context.todo.put(provider.getId(), new Object[]{provider, source});
			}
			
			if(context.decider.searchNode(source, provider, context.results))
			{
				// Use fut.isDone() to reduce stack depth
				IFuture future = provider.getServices(lsm, context.decider, context.selector, context.results);
				if(!future.isDone())
				{
					future.addResultListener(new IResultListener()
					{
						public void resultAvailable(Object src, Object result)
						{
							processParent(source, provider, context, ret, up, 0);
						}
						
						public void exceptionOccurred(Object source, Exception exception)
						{
							ret.setException(exception);
						}
					});
				}
				else
				{
					processParent(source, provider, context, ret, up, callstack+1);
				}
			}
			else if(up)
			{
				// Do not perform local search
				processParent(source, provider, context, ret, up, callstack+1);
			}
			else
			{
				dochildren = true;
			}
		}
		else
		{
			dochildren = true;
		}
		
		// Else continue with child nodes from todo list (if any).
		if(dochildren)
		{
			processChildNodes(provider, context, ret, callstack+1);
		}
	}

	/**
	 *  Continue search with the parent of the current node (if any).
	 */
	protected void processParent(final IServiceProvider source, final IServiceProvider provider,
		final SearchContext context, final Future ret, final boolean up, final int callstack)
	{
		context.callstack++;

		// When searching upwards, continue with parent.
		if(!context.selector.isFinished(context.results) && up)
		{
			// Use fut.isDone() to reduce stack depth
			IFuture	future	= provider.getParent();
			if(!future.isDone())
			{
				future.addResultListener(new IResultListener()
				{
					public void resultAvailable(Object src, Object result)
					{
						// Cut search if parent was already visisted.
						if(SUtil.equals(source, result))
							result = null;
						processNode(provider, (IServiceProvider)result, context, ret, up, 0);
					}
					
					public void exceptionOccurred(Object source, Exception exception)
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
					processNode(provider, (IServiceProvider)result, context, ret, up, callstack+1);
					
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
			processChildNodes(provider, context, ret, callstack+1);
		}
		
		context.callstack--;
	}

	/**
	 *  Process child nodes from the todo list.
	 */
	protected void processChildNodes(final IServiceProvider provider,
		final SearchContext context, final Future ret, final int callstack)
	{
		context.callstack++;
		
		// Finished, when no more todo nodes.
		if(context.selector.isFinished(context.results) || context.todo.isEmpty())
		{
			ret.setResult(context.selector.getResult(context.results));
		}
		
		// Continue with current list of children (if any)
		else if(context.todo.containsKey(CURRENT_CHILDREN))
		{
			List	ccs	= (List)context.todo.get(CURRENT_CHILDREN);
			IServiceProvider	child	= (IServiceProvider)ccs.remove(0);
			if(ccs.isEmpty())
			{
				context.todo.remove(CURRENT_CHILDREN);
			}
			
			// Set 'up' to false, once traversing children has started.
			processNode(provider, child, context, ret, false, callstack+1);
		}

		// Else pick entry from todo list and continue with its children.
		else
		{
			Object	next	= context.todo.keySet().iterator().next();
			final Object[] prov = (Object[])context.todo.remove(next);
			final IServiceProvider	provi = (IServiceProvider)prov[0];
			final IServiceProvider	src = (IServiceProvider)prov[1];
			IFuture future = provi.getChildren();
			
			// Use fut.isDone() to reduce stack depth
			if(!future.isDone())
			{
				future.addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						addChildren(src, provi, context, ret, (Collection)result, 0);
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						ret.setException(exception);
					}
				});
			}
			else
			{
				try
				{
					Object result = future.get(null);
					addChildren(src, provi, context, ret, (Collection)result, callstack+1);
				}
				catch(Exception exception)
				{
					ret.setException(exception);
				}
			}
		}
		
		context.callstack--;
	}
	
	/**
	 *  Add children to the current search.
	 */
	protected void addChildren(IServiceProvider source, IServiceProvider provider,
		SearchContext context, Future ret, Collection children, int callstack)
	{
		context.callstack++;
		
		if(!context.selector.isFinished(context.results) && children!=null && !children.isEmpty())
		{
			List	ccs	= new LinkedList(children);
			ccs.remove(source);
			if(!ccs.isEmpty())
			{
				context.todo.put(CURRENT_CHILDREN, ccs);
			}
		}
		processChildNodes(provider, context, ret, callstack+1);
		
		context.callstack--;
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
	
	//-------- helper classes --------
	
	/**
	 *  Struct for data that remains constant during search.
	 *  Used to reduce stack memory usage.
	 */
	public static class SearchContext
	{
		public IVisitDecider	decider;
		public IResultSelector	selector;
		public Collection	results;
		public Map	todo;
		public int	callstack;
		
		public SearchContext(IVisitDecider decider, IResultSelector selector, Collection results, Map todo)
		{
			this.decider	= decider;
			this.selector	= selector;
			this.results	= results;
			this.todo	= todo;
		}
	}
}
