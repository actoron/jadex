package jadex.backup.resource;

import jadex.bridge.service.types.deployment.FileData;
import jadex.commons.future.ITerminableIntermediateFuture;


/**
 *  Service for manipulating a local resource.
 */
public interface ILocalResourceService
{
	/**
	 *  Update the local resource with all
	 *  changes from all available remote resource.
	 *  @return All changed files and directories that are being processed.
	 */
	public ITerminableIntermediateFuture<FileData>	updateAll();

	/**
	 *  Update the local resource with all
	 *  changes from the given remote resource.
	 *  @param remote	The remote resource to synchronize to.
	 *  @return All changed files and directories that are being processed.
	 */
	public ITerminableIntermediateFuture<FileData>	update(IResourceService remote);
}
