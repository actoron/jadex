package jadex.base.service.remote;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IRemoteServiceManagementService;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IResultSelector;
import jadex.service.ISearchManager;
import jadex.service.IServiceProvider;
import jadex.service.IVisitDecider;
import jadex.service.SServiceProvider;

import java.util.Collection;

/**
 *  Remote service provider for searching at a remote platform
 *  in the same way as on a the local one.
 */
public class RemoteServiceProvider implements IServiceProvider
{
	//-------- attributes --------
	
	/** The remote component/provider identifier. */
	protected IComponentIdentifier providerid;
	
	/** The parent service provider. */
	protected IServiceProvider parent;
	
	//-------- constructors --------
	
	/**
	 *  Create a new remote service provider.
	 */
	public RemoteServiceProvider(IComponentIdentifier providerid, IServiceProvider parent)
	{
		this.providerid = providerid;
		this.parent = parent;
	}
	
	//-------- methods --------
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public IFuture	getServices(ISearchManager manager, final IVisitDecider decider, final IResultSelector selector, final Collection result)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(parent, IRemoteServiceManagementService.class)
			.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IRemoteServiceManagementService rms = (IRemoteServiceManagementService)result;
				
				// Hack! Use user search manager.
				rms.getServiceProxies(providerid, providerid, SServiceProvider.sequentialmanager, decider, selector)
					.addResultListener(new DelegationResultListener(ret));
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the parent service container.
	 *  @return The parent container.
	 */
	public IFuture	getParent()
	{
		return new Future(parent);
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
		return providerid;
	}
}
