package jadex.base.service.awareness.discovery.message;

import jadex.bridge.IComponentIdentifier;
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
	public IFuture<Void> announceComponentIdentifier(IComponentIdentifier cid);
}
