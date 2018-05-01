package jadex.tools.web;

import java.util.Collection;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.commons.Tuple3;
import jadex.commons.future.IFuture;

@Service(system=true)
public interface IStatusService
{
	/**
	 *  Get the established connections.
	 *  @return A list of connections specified by
	 *  	1: platform id,
	 *  	2: protocol name,
	 *  	3: ready flag (false=connecting, true=connected).
	 */
	// No intermediate for easier REST?
	public IFuture<Collection<Tuple3<IComponentIdentifier,String,Boolean>>>	getConnectedPlatforms();
}
