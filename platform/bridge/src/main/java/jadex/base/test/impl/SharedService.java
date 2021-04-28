package jadex.base.test.impl;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.BasicService;
import jadex.commons.future.IFuture;

/**
 *  Helper class to allow sharing a service across platforms in same VM.
 */
public abstract class SharedService<T>	extends BasicService
{
	/** The factory from which this impl was created. */
	protected SharedServiceFactory<T>	factory;

	/**
	 *  Get the instance.
	 */
	public  SharedService(IComponentIdentifier provider, Class<T> type, SharedServiceFactory<T> factory)
	{
		super(provider, type, null);
		this.factory	= factory;
	}
	
	//-------- accessor methods --------
	
	/**
	 *  Get the shared instance to delegate calls to.
	 */
	public T	getInstance()
	{
		return factory.instance;
	}
	
	//-------- BasicService methods --------
	
	@Override
	public IFuture<Void> startService()
	{
//		System.out.println("Starting shared service wrapper: "+this);
		return super.startService().thenCompose(nix -> factory.startService());
	}
	
	@Override
	public IFuture<Void> shutdownService()
	{
//		System.out.println("Terminating shared service wrapper: "+this);
		return super.shutdownService().thenCompose(nix -> factory.shutdownService());
	}
}
