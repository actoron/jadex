package jadex.bridge.service.types.registry;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  Interface allows for making a platform to
 *  a) registry superpeer
 *  b) registry client 
 */
@Service(system=true)
public interface IAutoConfigRegistryService
{
	/**
	 *  Make this platform registry superpeer.
	 */
	public IFuture<Void> makeRegistrySuperpeer();
	
	/**
	 *  Make this platform registry client.
	 */
	public IFuture<Void> makeRegistryClient();
	
	/**
	 *  Activate the config service.
	 */
	public IFuture<Void> activate();
}
