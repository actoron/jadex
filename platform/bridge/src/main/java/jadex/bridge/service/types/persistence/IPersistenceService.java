package jadex.bridge.service.types.persistence;

import java.util.Collection;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.modelinfo.IPersistInfo;
import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;

/**
 *  Service for basic component persistence features.
 */
public interface IPersistenceService
{
	//-------- recovery methods --------
	
	/**
	 *  Get the component state.
	 *  
	 *  @param cid The component to be saved.
	 *  @param recursive	True, if subcomponents should be saved as well.
	 *  @return The component(s) state.
	 */
	public IFuture<IPersistInfo> snapshot(IComponentIdentifier cid);
	
	/**
	 *  Get the component states.
	 *  
	 *  @param cids The components to be saved.
	 *  @param recursive	True, if subcomponents should be saved as well.
	 *  @return The component state(s).
	 */
	public IFuture<Collection<IPersistInfo>> snapshot(Collection<IComponentIdentifier> cids, boolean recursive);
	
	/**
	 *  Restore a component from a snapshot.
	 *  
	 *  @param pi	The component snapshot.
	 */
	public IFuture<Void>	restore(IPersistInfo pi);
	
	/**
	 *  Restore components from a snapshot.
	 *  
	 *  @param pis	The component snapshots.
	 */
	public IFuture<Void>	restore(Collection<IPersistInfo> pis);
	
	//-------- swap methods --------
	
	/**
	 *  Fetch the component state and transparently remove it from memory.
	 *  Keeps the component available in CMS to allow restoring it on access.
	 *  
	 *  @param cid	The component identifier.
	 *  @return The component state.
	 */
	public IFuture<IPersistInfo>	swapToStorage(IComponentIdentifier cid);
	
	/**
	 *  Transparently restore the component state of a previously
	 *  swapped component.
	 *  
	 *  @param pi	The persist info.
	 */
	public IFuture<Void>	swapFromStorage(IPersistInfo pi);
	
	/**
	 *  Set the idle hook to be called when a component becomes idle.
	 */
	@Excluded
	public IFuture<Void>	addIdleHook(@Reference IIdleHook hook);
}
