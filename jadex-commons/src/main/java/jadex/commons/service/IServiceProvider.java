package jadex.commons.service;

import jadex.commons.IFuture;
import jadex.commons.IIntermediateFuture;
import jadex.commons.IRemotable;

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
	public IIntermediateFuture	getServices(ISearchManager manager, IVisitDecider decider, IResultSelector selector);
	
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
