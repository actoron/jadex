package jadex.base.service.awareness.discovery.relay;

import jadex.commons.future.IFuture;

/**
 *  A service to be called from the relay transport to
 *  handle disconnects and reconnects.
 */
public interface IRelayAwarenessService
{
	/**
	 *  Let the awareness now that the transport connected to an address.
	 *  @param address	The relay address.
	 */
	public IFuture<Void>	connected(String address);

	/**
	 *  Let the awareness now that the transport was disconnected from an address.
	 *  @param address	The relay address.
	 */
	public IFuture<Void>	disconnected(String address);
}
