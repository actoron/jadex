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
	protected final String	CURRENT_CHILDREN	= "current-children";
	
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
	public SequentialSearchManager()
	{
		this(true, true);
	}
	
	/**
	 *  Create a new search manager.
	 */
	public SequentialSearchManager(boolean up, boolean down)
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
	public IFuture	searchServices(IServiceProvider provider, IVisitDecider decider, IResultSelector selector, Map services, Collection results)
	{
//		System.out.println("search: "+selector+" "+provider.getId());
		Future	ret	= new Future();
		Map	todo	= new LinkedHashMap(); // Nodes of which children still to be processed (id->provider).
		processNode(null, provider, decider, selector, services, ret, results, todo, up);
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
	protected void processNode(final IServiceProvider source, final IServiceProvider provider, final IVisitDecider decider, final IResultSelector selector, final Map services,
		final Future ret, final Collection results, final Map todo, final boolean up)
	{
//		if(selector.toString().indexOf("IRemoteService")!=-1)
//			System.out.println("processing: "+provider+" "+selector);
		
		boolean dochildren = false;
		
		// If node is to be searched, continue with this node.
		if(!selector.isFinished(results) && provider!=null)
		{
			if(down)
			{
				// Child nodes of this node still have to be searched.
				todo.put(provider.getId(), new Object[]{provider, source});
			}
			
			if(decider.searchNode(source, provider, results))
			{
				provider.getServices(LOCAL_SEARCH_MANAGER, decider, selector, results).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						// When searching upwards, continue with parent.
						if(!selector.isFinished(results) && up)
						{
							provider.getParent().addResultListener(new IResultListener()
							{
								public void resultAvailable(Object source, Object result)
								{
									// Cut search if parent was already visisted.
									if(SUtil.equals(source, result))
										result = null;
									processNode(provider, (IServiceProvider)result, decider, selector, services, ret, results, todo, up);
								}
								
								public void exceptionOccurred(Object source, Exception exception)
								{
									ret.setException(exception);
								}
							});
						}
	
						// Else continue with child nodes from todo list (if any).
						else
						{
							processChildNodes(null, decider, selector, services, ret, results, todo);
						}
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						ret.setException(exception);
					}
				});
			}
			else if(up)
			{
				// Do not perform local search
				provider.getParent().addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						// Cut search if parent was already visisted.
						if(SUtil.equals(source, result))
							result = null;
						processNode(provider, (IServiceProvider)result, decider, selector, services, ret, results, todo, up);
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						ret.setException(exception);
					}
				});
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
			processChildNodes(null, decider, selector, services, ret, results, todo);
		}
	}

	/**
	 *  Process child nodes from the todo list.
	 */
	protected void processChildNodes(final IServiceProvider source,
			final IVisitDecider decider, final IResultSelector selector,
			final Map services, final Future ret, final Collection results, final Map todo)
	{
		// Finished, when no more todo nodes.
		if(selector.isFinished(results) || todo.isEmpty())
		{
			ret.setResult(selector.getResult(results));
		}
		
		// Continue with current list of children (if any)
		else if(todo.containsKey(CURRENT_CHILDREN))
		{
			List	ccs	= (List)todo.get(CURRENT_CHILDREN);
			IServiceProvider	child	= (IServiceProvider)ccs.remove(0);
			if(ccs.isEmpty())
			{
				todo.remove(CURRENT_CHILDREN);
			}
			
			// Set 'up' to false, once traversing children has started.
			processNode(source, child, decider, selector, services, ret, results, todo, false);
		}

		// Else pick entry from todo list and continue with its children.
		else
		{
			Object	next	= todo.keySet().iterator().next();
			final Object[] prov = (Object[])todo.remove(next);
			final IServiceProvider	provider = (IServiceProvider)prov[0];
			final IServiceProvider	src = (IServiceProvider)prov[1];
			provider.getChildren().addResultListener(new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
//					if(selector.toString().indexOf("IRemoteService")!=-1)
//						System.out.println("children: "+provider+" "+result);
					if(!selector.isFinished(results) && result!=null && !((Collection)result).isEmpty())
					{
						List	ccs	= new LinkedList((Collection)result);
						ccs.remove(src);
						todo.put(CURRENT_CHILDREN, ccs);
					}
					processChildNodes(provider, decider, selector, services, ret, results, todo);
				}
				
				public void exceptionOccurred(Object source, Exception exception)
				{
					ret.setException(exception);
				}
			});
		}
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
	
}
