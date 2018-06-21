package jadex.bridge.service.types.persistence;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;

/**
 *  The swap service is responsible for
 *  swapping idle components to/from disk
 *  in order to save memory.
 *  
 *  Note, that the required operations swapToDisk/swapFromDisk
 *  are not provided by the service itself, but
 *  by the persistence service.
 */
public interface ISwapService
{
	/**
	 *  Store the component state and transparently remove it from memory.
	 *  Keeps the component available in CMS to allow restoring it on access.
	 *  
	 *  @param cid	The component identifier.
	 */
	public IFuture<Void>	swapToStorage(IComponentIdentifier cid);
	
	/**
	 *  Transparently restore the component state of a previously
	 *  swapped component.
	 *  
	 *  @param cid	The component identifier.
	 */
	public IFuture<Void>	swapFromStorage(IComponentIdentifier cid);
}
