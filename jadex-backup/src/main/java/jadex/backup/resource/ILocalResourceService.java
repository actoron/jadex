package jadex.backup.resource;

import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;


/**
 *  Service for manipulating a local resource.
 */
public interface ILocalResourceService
{
	/**
	 *  Get the local resource id.
	 *  The local resource id is a unique id that is
	 *  used to identify an individual instance of a
	 *  distributed resource on a specific host.
	 *  
	 *  The id is static, i.e. it does not change during
	 *  the lifetime of the resource.
	 */
	public String	getLocalId();
	
	/**
	 *  Update the local resource with all
	 *  changes from all available remote resource.
	 *  @return Events as files and directories are being processed.
	 */
	public ITerminableIntermediateFuture<BackupEvent>	updateAll();

	/**
	 *  Update the local resource with all
	 *  changes from the given remote resource.
	 *  @param remote	The remote resource to synchronize to.
	 *  @return Events as files and directories are being processed.
	 */
	public ITerminableIntermediateFuture<BackupEvent>	update(IResourceService remote);
	
	/**
	 * 
	 */
	public ISubscriptionIntermediateFuture<BackupEvent> updateFile(final IResourceService remote, final FileInfo fi);
}
