package jadex.kernelbase;

import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.IServiceProvider;
import jadex.commons.future.IFuture;

public interface IBootstrapFactory
{
	/**
	 * 
	 * @param provider
	 * @return
	 */
	public IFuture<Void> startService(IServiceProvider provider, IResourceIdentifier rid);
}
