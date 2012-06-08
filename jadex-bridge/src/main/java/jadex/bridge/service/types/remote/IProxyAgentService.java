package jadex.bridge.service.types.remote;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;

/**
 *  Service for administration of proxy agents.
 */
public interface IProxyAgentService
{
	/**
	 *  Get the component identifier of the remote platform.
	 */
	public IFuture<IComponentIdentifier>	getRemoteComponentIdentifier();

	/**
	 *  Set or update the component identifier of the remote platform,
	 *  i.e., top reflect new transport addresses.
	 */
	public IFuture<Void>	setRemoteComponentIdentifier(IComponentIdentifier cid);
}
