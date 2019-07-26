package jadex.bridge.service.types.settings;

public interface IPlatformSettings
{
	/**
	 *  Saves arbitrary state to a persistent directory as JSON.
	 *  Object must be serializable and the ID must be unique.
	 *  
	 *  @param id Unique ID for the saved state.
	 *  @param state The state being saved.
	 *  @return Null, when done.
	 */
	public void saveState(String id, Object state);
	
	/**
	 *  Loads arbitrary state form a persistent directory.
	 *  
	 *  @param id Unique ID for the saved state.
	 *  @return The state or null if none was found or corrupt.
	 */
	public Object loadState(String id);
	
	/**
	 *  Directly saves a file in the settings directory.
	 *  
	 *  @param filename Name of the file.
	 *  @param content The file content.
	 *  @return Null, when done.
	 */
	public void saveFile(String filename, byte[] content);
	
	/**
	 *  Directly loads a file from the settings directory.
	 *  
	 *  @param filename Name of the file.
	 *  @return Content of the file or null if not found.
	 */
	public byte[] loadFile(String filename);
}
