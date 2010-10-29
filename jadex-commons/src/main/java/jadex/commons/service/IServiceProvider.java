package jadex.commons.service;

import jadex.commons.IFuture;
import jadex.commons.IRemotable;

import java.util.Collection;

/**
 *  Interface for service providers.
 */
public interface IServiceProvider extends IRemotable
{	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public IFuture	getServices(ISearchManager manager, IVisitDecider decider, IResultSelector selector, Collection results);
	
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
