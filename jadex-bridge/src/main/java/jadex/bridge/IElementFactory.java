package jadex.bridge;

import jadex.service.IService;

import javax.swing.Icon;

/**
 * 
 */
public interface IElementFactory extends IService
{
	//-------- constants --------

	/** The element type application. */
	public static final String ELEMENT_TYPE_APPLICATION = "application";
	
	/** The element type agent. */
	public static final String ELEMENT_TYPE_AGENT = "agent";
	
	/** The element type process. */
	public static final String ELEMENT_TYPE_PROCESS = "process";

	//-------- methods --------
	
	/**
	 *  Load a  model.
	 *  @param model The model.
	 *  @return The loaded model.
	 */
	public ILoadableElementModel loadModel(String model);

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
	 *  @param model The model.
	 */
	public String getFileType(String model);
	
	/**
	 *  Get the element type.
	 *  @return The element type (e.g. an agent, application or process).
	 */
	public String getElementType();
}
