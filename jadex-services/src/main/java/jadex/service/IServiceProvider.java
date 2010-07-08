package jadex.service;

import jadex.commons.IFuture;

/**
 *  Interface for service providers.
 */
public interface IServiceProvider
{
	/**
	 *  Get all services of a typ.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public IFuture	getServices(ISearchManager manager, IVisitDecider visit, IResultSelector result);
	
	
	/**
	 *  Get the parent service container.
	 *  @return The parent container.
	 */
	public IFuture	getParent();
	
	/**
	 *  Get the children container.
	 *  @return The children container.
	 */
	public IFuture	getChildren();
	
	/**
	 *  Get the globally unique id of the provider.
	 *  @return The id of this provider.
	 */
	public Object	getId();
}
