package jadex.backup.resource;

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
	 *  Scan for changes at the given remote resource
	 *  that need to be synchronized with the local resource.
	 *  @param remote	The remote resource to synchronize to.
	 *  @return Events of detected remote changes.
	 */
	public ITerminableIntermediateFuture<BackupEvent>	scanForChanges(IResourceService remote);
	
	/**
	 *  Update or revert the local file with the remote version.
	 *  Checks if local and remote file are unchanged with respect to the previous file infos.
	 *  @param remote The remote resource.
	 *  @param localfi The local file info.
	 *  @param remotefi The remote file info.
	 *  @return Status events while the file is being downloaded.
	 *  @throws Exception, e.g. when local or remote file have changed after the last scan.
	 */
	public ITerminableIntermediateFuture<BackupEvent> updateFromRemote(IResourceService remote, FileInfo localfi, FileInfo remotefi);
	
	/**
	 *  Ignore the remote change and set the local file state as being newer.
	 *  As a result, the remote file will be reverted the next time the remote resource synchronizes to this resource. 
	 *  Checks if local and remote file are unchanged with respect to the previous file infos.
	 *  @param remote The remote resource.
	 *  @param localfi The local file info.
	 *  @param remotefi The remote file info.
	 *  @return Status events of the override operation.
	 *  @throws Exception when local or remote file have changed after the last scan.
	 */
	public ITerminableIntermediateFuture<BackupEvent> overrideRemoteChange(IResourceService remote, FileInfo localfi, FileInfo remotefi);
	
	/**
	 *  Copy the local file before downloading the remote version.
	 *  If the remote version would be saved as copy, the change would be detected again on next scan.
	 *  Therefore the remote version is used as new local version whereas
	 *  the old local version is given a new name of the form 'name.copy.ext'.
	 *  @param remote The remote resource.
	 *  @param localfi The local file info.
	 *  @param remotefi The remote file info.
	 *  @return Status events while the file is being downloaded.
	 *  @throws Exception, e.g. when local or remote file have changed after the last scan.
	 */
	public ITerminableIntermediateFuture<BackupEvent> updateAsCopy(IResourceService remote, FileInfo localfi, FileInfo remotefi);
}
