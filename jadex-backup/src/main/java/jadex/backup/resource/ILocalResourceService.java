package jadex.backup.resource;

import jadex.commons.future.ITerminableIntermediateFuture;


/**
 *  Service for manipulating a local resource.
 */
public interface ILocalResourceService
{
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
}
