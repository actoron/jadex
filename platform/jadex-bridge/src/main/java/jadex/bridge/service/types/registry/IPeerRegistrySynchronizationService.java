package jadex.bridge.service.types.registry;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  Service for peers to be distinguished from superpeers.
 */
@Service(system=true)
public interface IPeerRegistrySynchronizationService 
{
	/**
	 *  Get the superpeer.
	 *  @param force If true searches superpeer anew.
	 *  @return The superpeer.
	 */
	public IFuture<IComponentIdentifier> getSuperpeer(boolean force);
}
