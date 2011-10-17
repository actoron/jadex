package jadex.bridge.service.library;

import jadex.bridge.IResourceIdentifier;
import jadex.commons.IRemotable;
import jadex.commons.future.IFuture;


/** 
 * Interface for listening to library loading and unloading.
 */
public interface ILibraryServiceListener extends IRemotable
{
//	/** 
//	 *  Called when a new url has been added.
//	 *  @param url the url of the new classpath entry.
//	 */
//	public IFuture urlAdded(URL url);
//	
//	/** 
//	 *  Called when a url has been removed.
//	 *  @param url the url of the removed classpath entry.
//	 */
//	public IFuture urlRemoved(URL url);
	
	/** 
	 *  Called when a new rid has been added.
	 *  @param url The rid of the new classpath entry.
	 */
	public IFuture<Void> resourceIdentifierAdded(IResourceIdentifier rid);
	
	/** 
	 *  Called when a rid has been removed.
	 *  @param url The rid of the removed classpath entry.
	 */
	public IFuture<Void> resourceIdentifierRemoved(IResourceIdentifier rid);
	
}
