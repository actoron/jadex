package jadex.bridge;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.service.BasicServiceContainer;
import jadex.service.IResultSelector;
import jadex.service.ISearchManager;
import jadex.service.IServiceContainer;
import jadex.service.IVisitDecider;

/**
 *  Service container for active components. This is a delegation provider.
 */
public class ComponentServiceContainer implements IServiceContainer
{
	//-------- attributes --------
	
	/** The component adapter. */
	protected IComponentAdapter adapter;
	
	/** The original service container. */
	protected IServiceContainer container;
	
	//-------- constructors --------

	/**
	 *  Create a new service container.
	 */
	public ComponentServiceContainer(IComponentAdapter adapter)
	{
		this(adapter, new BasicServiceContainer(adapter.getComponentIdentifier()));
	}
	
	/**
	 *  Create a new service container.
	 */
	public ComponentServiceContainer(IComponentAdapter adapter, IServiceContainer container)
	{
		this.adapter = adapter;
		this.container = container;
	}
	
	//-------- interface methods --------
	
	/**
	 *  Get service or services according to the search spec.
	 *  @param manager The search manager.
	 *  @param decider The visit decider.
	 *  @param selector The selector.
	 *  @return The corresponding services.
	 */
	public IFuture	getServices(final ISearchManager manager, final IVisitDecider decider, final IResultSelector selector)
	{
		final Future ret = new Future();
		
		container.getServices(manager, decider, selector).addResultListener(new DelegationResultListener(ret));
		
		return ret;
	}
	
	/**
	 *  Get the parent service container.
	 *  @return The parent container.
	 */
	public IFuture	getParent()
	{
		final Future ret = new Future();
		
		ret.setResult(adapter.getParent());
		
		return ret;
	}
	
	/**
	 *  Get the children container.
	 *  @return The children container.
	 */
	public IFuture	getChildren()
	{
		final Future ret = new Future();
		
		adapter.getChildren().addResultListener(new DelegationResultListener(ret));
		
		return ret;
	}
	
	/**
	 *  Get the globally unique id of the provider.
	 *  @return The id of this provider.
	 */
	public Object getId()
	{
		// Called in and externally, but returns constant.
		return container.getId();
	}
	
	//-------- methods --------
	
	/**
	 *  Add a service to the platform.
	 *  Does NOT start the service automatically.
	 *  If under the same name and type a service was contained,
	 *  the old one is removed and shutdowned.
	 *  @param id The name.
	 *  @param service The service.
	 */
	public void addService(Class type, Object service)
	{
		// Only called internally.
		container.addService(type, service);
	}

	/**
	 *  Removes a service from the platform (shutdowns also the service).
	 *  @param id The name.
	 *  @param service The service.
	 */
	public void removeService(Class type, Object service)
	{
		// Only called internally.
		container.removeService(type, service);
	}
	
	//-------- internal methods --------
	
	/**
	 *  Start the service.
	 */
	public IFuture start()
	{
		// Only called internally.
		return container.start();
	}
	
	/**
	 *  Shutdown the service.
	 */
	public IFuture shutdown()
	{
		// Only called internally.
		return container.shutdown();
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "ComponentServiceContainer(name="+getId()+")";
	}
}
