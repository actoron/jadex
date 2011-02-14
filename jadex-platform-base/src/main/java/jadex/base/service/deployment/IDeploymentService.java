package jadex.base.service.deployment;

import jadex.commons.future.IFuture;
import jadex.commons.service.IService;

/**
 *  Interface for the deployment service.
 */
public interface IDeploymentService extends IService
{
	/**
	 *  Get a file.
	 *  @return The file data.
	 */
	public IFuture getFile(String path);

	/**
	 *  Put a file.
	 *  @param file The file data.
	 *  @param path The target path.
	 *  @return True, when the file has been copied.
	 */
	public IFuture putFile(FileData filedata, String path);
	
	/**
	 *  Rename a file.
	 *  @param path The target path.
	 *  @param name The name.
	 *  @return True, if rename was successful.
	 */
	public IFuture renameFile(String path, String name);
	
	/**
	 *  Delete a file.
	 *  @param path The target path.
	 *  @return True, if delete was successful.
	 */
	public IFuture deleteFile(String path);
	
	/**
	 *  Get the root devices.
	 *  @return The root device files.
	 */
	public IFuture getRoots();
}
