package jadex.bridge.service;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.search.IResultSelector;
import jadex.bridge.service.search.ISearchManager;
import jadex.bridge.service.search.IVisitDecider;
import jadex.commons.IRemotable;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITerminableIntermediateFuture;

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
	public ITerminableIntermediateFuture<IService> getServices(ISearchManager manager, IVisitDecider decider, IResultSelector selector);
	
	/**
	 *  Get the parent service container.
	 *  @return The parent container.
	 */
	public IFuture<IServiceProvider>	getParent();
	
	/**
	 *  Get the children container.
	 *  @return The children container.
	 */
	public IFuture<Collection<IServiceProvider>>	getChildren();
	
	/**
	 *  Get the globally unique id of the provider.
	 *  @return The id of this provider.
	 */
	public IComponentIdentifier	getId();
	
	/**
	 *  Get the type of the service provider (e.g. enclosing component type).
	 *  @return The type of this provider.
	 */
	public String	getType();
}
