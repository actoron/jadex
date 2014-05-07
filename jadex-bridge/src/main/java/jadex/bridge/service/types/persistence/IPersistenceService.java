package jadex.bridge.service.types.persistence;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.modelinfo.IPersistInfo;
import jadex.commons.future.IFuture;

/**
 *  Service for basic component persistence features.
 */
public interface IPersistenceService
{
	//-------- persistence methods --------
	
	/**
	 *  Gets the component state.
	 *  
	 *  @param cid The component.
	 *  @return The component state.
	 */
	public IFuture<IPersistInfo> getPersistableState(IComponentIdentifier cid);
	
	/**
	 *  Resurrect a persisted component.
	 */
	public IFuture<Void>	resurrectComponent(IPersistInfo pi);
}
