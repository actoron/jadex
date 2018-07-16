package jadex.bridge.service.types.registryv2;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Monitoring service for displaying information about a superpeer, e.g. for GUI.
 */
@Service
public interface ISuperpeerStatusService
{
	/**
	 *  Get the clients that are currently registered to super peer.
	 */
	public ISubscriptionIntermediateFuture<IComponentIdentifier>	getRegisteredClients();
}
