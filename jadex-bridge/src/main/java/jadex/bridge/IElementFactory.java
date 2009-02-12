package jadex.bridge;

import javax.swing.Icon;

/**
 * 
 */
public interface IElementFactory
{
	/**
	 *  Load an agent model.
	 *  @param model The model.
	 *  @return The loaded model.
	 */
	public IAgentModel loadModel(String model);

	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param model The model.
	 *  @return True, if model can be loaded.
	 */
	public boolean isLoadable(String model);
	
	/**
	 *  Test if a model is startable (e.g. an agent).
	 *  @param model The model.
	 *  @return True, if startable (and loadable).
	 */
	public boolean isStartable(String model);

	/**
	 *  Get the names of ADF file types supported by this factory.
	 */
	public String[] getFileTypes();

	/**
	 *  Get a default icon for a file type.
	 */
	public Icon getFileTypeIcon(String type);

	/**
	 *  Get the file type of a model.
	 */
	public String getFileType(String model);
}
