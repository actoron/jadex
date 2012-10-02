package jadex.backup.resource;

import jadex.commons.future.IFuture;

/**
 *  A service for a distributed resource.
 */
public interface IResourceService
{
	/**
	 *  Get the resource id.
	 *  The resource id is a globally unique id that is
	 *  used to identify the same resource on different
	 *  hosts, i.e. provided by different service instances.
	 *  
	 *  The id is static, i.e. it does not change during
	 *  the lifetime of the service.
	 */
	public String	getResourceId();
	
	/**
	 *  Get information about local files.
	 *  Only provide information if newer than the provided info.
	 *  @param dir	The directory to be queried.
	 */
	public IFuture<FileInfo[]>	getFiles(FileInfo dir);
}
