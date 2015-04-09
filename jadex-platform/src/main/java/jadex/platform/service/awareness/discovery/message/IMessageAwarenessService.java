package jadex.platform.service.awareness.discovery.message;

import jadex.bridge.ITransportComponentIdentifier;
import jadex.commons.future.IFuture;

/**
 *  Discovery service called when a message is received. 
 */
public interface IMessageAwarenessService 
{
	/**
	 *  Announce a potentially new component identifier.
	 *  @param cid The component identifier.
	 */
	public IFuture<Void> announceComponentIdentifier(ITransportComponentIdentifier cid);
}
