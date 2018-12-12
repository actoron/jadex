package jadex.bridge.service.types.library;

import jadex.bridge.IResourceIdentifier;
import jadex.commons.IRemotable;
import jadex.commons.future.IFuture;


/** 
 * Interface for listening to library loading and unloading.
 */
public interface ILibraryServiceListener extends IRemotable
{
	/** 
	 *  Called when a new rid has been added.
	 *  @param url The rid of the new classpath entry.
	 */
	public IFuture<Void> resourceIdentifierAdded(IResourceIdentifier parid, IResourceIdentifier rid, boolean removable);
	
	/** 
	 *  Called when a rid has been removed.
	 *  @param url The rid of the removed classpath entry.
	 */
	public IFuture<Void> resourceIdentifierRemoved(IResourceIdentifier parid, IResourceIdentifier rid);
	
}
