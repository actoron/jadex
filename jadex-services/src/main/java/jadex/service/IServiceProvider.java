package jadex.service;

import jadex.commons.IFuture;

/**
 *  Interface for service providers.
 */
public interface IServiceProvider
{
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
//	public IFuture getService(Class type);
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
//	public IFuture getServices(Class type);
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public IFuture	getServices(ISearchManager manager, IVisitDecider decider, IResultSelector selector);
	
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
