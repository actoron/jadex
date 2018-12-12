package jadex.bridge.service.types.filetransfer;

import jadex.bridge.IInputConnection;
import jadex.bridge.IOutputConnection;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Interface for the file transfer service.
 */
@Service(system=true)
public interface IFileTransferService
{
	/**
	 *  Put a file.
	 *  @param file The file data.
	 *  @param path The target path.
	 *  @return True, when the file has been copied.
	 */
	public ISubscriptionIntermediateFuture<Long> uploadFile(IInputConnection con, String path, String name);
	
	/**
	 *  Download a file.
	 *  @param file The file data.
	 *  @param path The target path.
	 *  @return True, when the file has been copied.
	 */
	public ISubscriptionIntermediateFuture<Long> downloadFile(IOutputConnection con, String name);

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
	
	/**
	 *  List the contents of a directory.
	 *  @param dir The directory, null for current directory.
	 *  @return The contained files.
	 */
	public IFuture<FileData[]> listDirectory(String dir);
}
