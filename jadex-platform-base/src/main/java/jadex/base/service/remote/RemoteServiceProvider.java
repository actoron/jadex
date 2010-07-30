package jadex.base.service.remote;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.service.IResultSelector;
import jadex.service.ISearchManager;
import jadex.service.IServiceProvider;
import jadex.service.IVisitDecider;

import java.util.Collection;

/**
 * 
 */
public class RemoteServiceProvider implements IServiceProvider
{
	/** The remote component identifier. */
	protected IComponentIdentifier component;
	
	/** A local service provider. */
	protected IServiceProvider provider;
	
	/**
	 * 
	 */
	public RemoteServiceProvider(IComponentIdentifier component, IServiceProvider provider)
	{
		this.component = component;
		this.provider = provider;
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public IFuture	getServices(ISearchManager manager, IVisitDecider decider, IResultSelector selector, Collection result)
	{
		Future ret = new Future();
		
//		SServiceProvider.getService(provider, IRemoteServiceManagementService.class)
//			.addResultListener(new IResultListener()
//		{
//			public void resultAvailable(Object source, Object result)
//			{
//				IRemoteServiceManagementService rms = (IRemoteServiceManagementService)result;
//				
//				rms.getProxies(platform, service)
//			}
//			
//			public void exceptionOccurred(Object source, Exception exception)
//			{
//			}
//		});
		
		return ret;
	}
	
	/**
	 *  Get the parent service container.
	 *  @return The parent container.
	 */
	public IFuture	getParent()
	{
		return new Future(null);
	}
	
	/**
	 *  Get the children container.
	 *  @return The children container.
	 */
	public IFuture	getChildren()
	{
		return new Future(null);
	}
	
	/**
	 *  Get the globally unique id of the provider.
	 *  @return The id of this provider.
	 */
	public Object	getId()
	{
		return component;
	}
}
