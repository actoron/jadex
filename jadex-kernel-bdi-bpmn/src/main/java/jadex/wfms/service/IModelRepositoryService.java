package jadex.wfms.service;

import jadex.service.IService;
import jadex.wfms.IProcessModel;

import java.util.Collection;
/**
 * Repository service for accessing process models.
 */
public interface IModelRepositoryService extends IService
{
	/**
	 * Gets a BPMN model.
	 * @param name name of the model
	 * @return the model
	 * /
	public MBpmnModel getBpmnModel(String name);*/
	
	/**
	 * Gets all available BPMN models.
	 * @return names of all BPMN models
	 * /
	public Set getBpmnModelNames();*/
	
	/**
	 * Gets a GPMN model.
	 * @param name name of the model
	 * @return the model
	 * /
	public String getGpmnModel(String name);*/
	
	/**
	 * Gets a GPMN model path.
	 * @param name name of the model
	 * @return path to the model
	 */
//	public String getGpmnModelPath(String name);
	
	/**
	 * Gets all available GPMN models.
	 * @return names of all GPMN models
	 * /
	public Set getGpmnModelNames();*/

	/**
	 *  Add a process model.
	 *  @param client The client.
	 *  @param name The name of the model.
	 *  @param path The path to the model.
	 */
	public void addProcessModel(String filename);
	
	/**
	 * Gets all available models.
	 * @return names of all models
	 */
	public Collection getModelNames();
	
	/**
	 *  Remove a process model.
	 *  @param client The client.
	 *  @param name The name of the model.
	 *  @param path The path to the model.
	 */
//	public void removeProcessModel(IClient client, String name);
	
	/**
	 *  Get a process model of a specific name.
	 *  @param name The model name.
	 *  @return The process model.
	 */
	public IProcessModel getProcessModel(String name);
	
	/**
	 *  Get the imports.
	 */
	public String[] getImports();
}
