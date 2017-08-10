package jadex.platform.service.awareness.discovery.relay;

import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Properties;

/**
 *  A service to be called from the relay transport to
 *  handle disconnects and reconnects.
 */
@Properties(@NameValue(name="system", value="true"))
public interface IRelayAwarenessService
{
	/**
	 *  Let the awareness know that the transport connected to an address.
	 *  @param address	The relay address.
	 */
	public IFuture<Void>	connected(String address);

	/**
	 *  Let the awareness know that the transport was disconnected from an address.
	 *  @param address	The relay address.
	 */
	public IFuture<Void>	disconnected(String address);
}
