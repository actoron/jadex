package jadex.backup.resource;

import jadex.commons.future.IFuture;

/**
 *  A service for a querying a remote resource.
 */
public interface IResourceService
{
	/**
	 *  Get the global resource id.
	 *  The global resource id is a unique id that is
	 *  used to identify the same resource on different
	 *  hosts, i.e. provided by different service instances.
	 *  
	 *  The id is static, i.e. it does not change during
	 *  the lifetime of the resource.
	 */
	public String	getResourceId();
	
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
	 *  Get information about local files.
	 *  Only provide information if newer than the provided info.
	 *  @param dir	The directory to be queried.
	 */
	public IFuture<FileInfo[]>	getFiles(FileInfo dir);
	
	/**
	 *  Get all changes files and directories since a given time point.
	 *  @param time	The local vector time point.
	 *  @return File infos for changed files and directories.
	 */
	public IFuture<FileInfo[]>	getChanges(int time);
}
