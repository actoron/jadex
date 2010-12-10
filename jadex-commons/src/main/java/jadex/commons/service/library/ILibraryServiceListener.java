package jadex.commons.service.library;

import jadex.commons.IFuture;
import jadex.commons.IRemotable;

import java.net.URL;


/** 
 * Interface for listening to library loading and unloading.
 */
public interface ILibraryServiceListener extends IRemotable
{
	/** 
	 *  Called when a new url has been added.
	 *  @param url the url of the new classpath entry.
	 */
	public IFuture urlAdded(URL url);
	
	/** 
	 *  Called when a url has been removed.
	 *  @param url the url of the removed classpath entry.
	 */
	public IFuture urlRemoved(URL url);
	
}
