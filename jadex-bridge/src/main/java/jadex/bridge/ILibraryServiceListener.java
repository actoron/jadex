package jadex.bridge;


/** 
 * Interface for listening to library loading and unloading.
 */
public interface ILibraryServiceListener
{
	/** 
	 *  Called when a new .jar-file has been added.
	 *  @param path the path to the new .jar-file
	 */
	public void jarAdded(String path);
	
	/** 
	 *  Called when a .jar-file has been removed.
	 *  @param path the path to the removed .jar-file
	 */
	public void jarRemoved(String path);
	
	/** 
	 *  Called when a new class path has been added.
	 *  @param path the new class path
	 */
	public void pathAdded(String path);
	
	/** 
	 *  Called when a class path has been removed.
	 *  @param path the removed class path
	 */
	public void pathRemoved(String path);
}
