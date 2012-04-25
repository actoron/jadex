package jadex.bridge.service.types.deployment;

import jadex.bridge.IInputConnection;
import jadex.bridge.service.IService;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITerminableIntermediateFuture;

/**
 *  Interface for the deployment service.
 */
public interface IDeploymentService //extends IService
{
	/** Default fragment size 10kB. */
	public static final int FRAGMENT_SIZE = 1024*10;
	
//	/**
//	 *  Get a file.
//	 *  @return The file data.
//	 */
//	public IFuture<Tuple2<FileContent,String>> getFile(String path, int fragment, int fileid);

//	/**
//	 *  Put a file.
//	 *  @param file The file data.
//	 *  @param path The target path.
//	 *  @return True, when the file has been copied.
//	 */
//	public IFuture<String> putFile(FileContent filedata, String path, String fileid);
	
	/**
	 *  Put a file.
	 *  @param file The file data.
	 *  @param path The target path.
	 *  @return True, when the file has been copied.
	 */
	public ITerminableIntermediateFuture<Long> uploadFile(IInputConnection con, String path);
	
	/**
	 *  Rename a file.
	 *  @param path The target path.
	 *  @param name The name.
	 *  @return True, if rename was successful.
	 */
	public IFuture<String> renameFile(String path, String name);
	
	/**
	 *  Delete a file.
	 *  @param path The target path.
	 *  @return True, if delete was successful.
	 */
	public IFuture<Void> deleteFile(String path);
	
	/**
	 *  Open a file.
	 *  @param path The filename to open.
	 */
	public IFuture<Void> openFile(String path);
	
	/**
	 *  Get the root devices.
	 *  @return The root device files.
	 */
	public IFuture<FileData[]> getRoots();
}
