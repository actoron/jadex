package jadex.bridge;

import jadex.service.IResultSelector;
import jadex.service.ISearchManager;
import jadex.service.IVisitDecider;

/**
 * 
 */
public class RemoteServiceSearchInvocationInfo
{
	/** The providerid (i.e. the component to start with searching). */
	protected Object providerid;
	
	/** The serach manager. */
	protected ISearchManager manager;
	
	/** The visit decider. */
	protected IVisitDecider decider;
	
	/** The result selector. */
	protected IResultSelector selector;

	/**
	 *  Create a new search request.
	 */
	public RemoteServiceSearchInvocationInfo()
	{
	}

	/**
	 *  Create a new search request.
	 */
	public RemoteServiceSearchInvocationInfo(Object providerid, ISearchManager manager, 
		IVisitDecider decider, IResultSelector selector)
	{
		this.providerid = providerid;
		this.manager = manager;
		this.decider = decider;
		this.selector = selector;
	}

	/**
	 *  Get the providerid.
	 *  @return the providerid.
	 */
	public Object getProviderId()
	{
		return providerid;
	}

	/**
	 *  Set the providerid.
	 *  @param providerid The providerid to set.
	 */
	public void setProviderId(Object providerid)
	{
		this.providerid = providerid;
	}

	/**
	 *  Get the manager.
	 *  @return the manager.
	 */
	public ISearchManager getSearchManager()
	{
		return manager;
	}

	/**
	 *  Set the manager.
	 *  @param manager The manager to set.
	 */
	public void setSearchManager(ISearchManager manager)
	{
		this.manager = manager;
	}

	/**
	 *  Get the decider.
	 *  @return the decider.
	 */
	public IVisitDecider getVisitDecider()
	{
		return decider;
	}

	/**
	 *  Set the decider.
	 *  @param decider The decider to set.
	 */
	public void setVisitDecider(IVisitDecider decider)
	{
		this.decider = decider;
	}

	/**
	 *  Get the selector.
	 *  @return the selector.
	 */
	public IResultSelector getResultSelector()
	{
		return selector;
	}

	/**
	 *  Set the selector.
	 *  @param selector The selector to set.
	 */
	public void setResultSelector(IResultSelector selector)
	{
		this.selector = selector;
	}
}
