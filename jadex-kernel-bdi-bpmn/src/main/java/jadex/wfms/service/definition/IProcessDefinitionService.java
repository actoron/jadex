package jadex.wfms.service.definition;

import jadex.bridge.ILoadableComponentModel;
import jadex.service.IService;
import jadex.wfms.client.IClient;

import java.util.Set;

/**
 * Administrative service interface
 *
 */
public interface IProcessDefinitionService extends IService
{
	/**
	 * Adds a process model to the repository
	 * @param client the client
	 * @param name name of the model
	 * @param path path to the model
	 */
	public void addProcessModel(IClient client, String path);
	
	/**
	 * Gets a process model.
	 * @param name name of the model
	 * @return the model
	 */
	public ILoadableComponentModel getProcessModel(IClient client, String name);
	
	/**
	 * Gets the names of all available process models
	 * 
	 * @param client the client
	 * @return the names of all available process models
	 */
	public Set getProcessModelNames(IClient client);
	
	/**
	 * Adds a GPMN model to the repository
	 * @param client the client
	 * @param name name of the model
	 * @param path path to the model
	 */
	//public void addGpmnModel(IClient client, String path);
	
	/**
	 * Gets a GPMN model.
	 * @param name name of the model
	 * @return the model
	 */
	//public MGpmnModel getGpmnModel(IClient client, String name);
	
	/**
	 * Gets the names of all available GPMN-models
	 * 
	 * @param client the client
	 * @return the names of all available GPMN-models
	 */
	//public Set getGpmnModelNames(IClient client);
}
