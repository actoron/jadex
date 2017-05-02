package jadex.bridge.service.search;

import jadex.bridge.IComponentIdentifier;

public interface IMultiServiceRegistry extends IServiceRegistry
{
	/**
	 *  Get a subregistry.
	 *  @param cid The platform id.
	 *  @return The registry.
	 */
	// read
//	public IServiceRegistry getSubregistry(IComponentIdentifier cid);
	
	/**
	 *  Remove a subregistry.
	 *  @param cid The platform id.
	 */
	// write
	public void removeSubregistry(IComponentIdentifier cid);
}
