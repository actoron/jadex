package jadex.bridge;

import java.util.Map;

import jadex.service.IService;
import jadex.service.IServiceContainer;

import javax.swing.Icon;

/**
 * 
 */
public interface IComponentFactory extends IService
{
	//-------- constants --------

//	/** The component type application. */
//	public static final String COMPONENT_TYPE_APPLICATION = "application";
//	
//	/** The component type agent. */
//	public static final String COMPONENT_TYPE_AGENT = "agent";
//	
//	/** The component type process. */
//	public static final String COMPONENT_TYPE_PROCESS = "process";

	//-------- methods --------
	
	/**
	 *  Load a  model.
	 *  @param model The model.
	 *  @return The loaded model.
	 */
	public ILoadableComponentModel loadModel(String model);

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
//	public String getElementType();
	
	/**
	* Create a kernel agent.
	* @param model The agent model file (i.e. the name of the XML file).
	* @param config The name of the configuration (or null for default configuration) 
	* @param arguments The arguments for the agent as name/value pairs.
	* @return An instance of a kernel agent.
	*/
	public IComponentInstance createComponentInstance(IComponentAdapter adapter, ILoadableComponentModel model, String config, Map arguments);
}
