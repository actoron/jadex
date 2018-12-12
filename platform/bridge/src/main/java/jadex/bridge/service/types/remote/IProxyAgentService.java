package jadex.bridge.service.types.remote;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  Service for administration of proxy agents.
 */
@Service
public interface IProxyAgentService
{
	/** The connection state. */
	public static enum State
	{
		CONNECTED,
		UNCONNECTED,
		LOCKED
	}
	
	/**
	 *  Get the component identifier of the remote platform.
	 */
	public IFuture<IComponentIdentifier>	getRemoteComponentIdentifier();

	/**
	 *  Set or update the component identifier of the remote platform,
	 *  i.e., top reflect new transport addresses.
	 */
	public IFuture<Void>	setRemoteComponentIdentifier(IComponentIdentifier cid);
	
	/**	
	 *  Get the connection state of the proxy.
	 *  @return The connection state.
	 */
	public IFuture<State> getConnectionState();

	/**
	 *  Refresh the latency value.
	 */
	public IFuture<Void>	refreshLatency();
}
