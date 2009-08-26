package jadex.wfms.service;

import java.util.Set;

import jadex.bpmn.model.MBpmnModel;
import jadex.gpmn.model.MGpmnModel;
import jadex.wfms.client.IClient;

/**
 * Administrative service interface
 *
 */
public interface IProcessDefinitionService
{
	/**
	 * Adds a BPMN model to the repository
	 * @param client the client
	 * @param name name of the model
	 * @param path path to the model
	 */
	public void addBpmnModel(IClient client, String name, String path);
	
	/**
	 * Gets a BPMN model.
	 * @param name name of the model
	 * @return the model
	 */
	public MBpmnModel getBpmnModel(IClient client, String name);
	
	/**
	 * Gets the names of all available BPMN-models
	 * 
	 * @param client the client
	 * @return the names of all available BPMN-models
	 */
	public Set getBpmnModelNames(IClient client);
	
	/**
	 * Adds a GPMN model to the repository
	 * @param client the client
	 * @param name name of the model
	 * @param path path to the model
	 */
	public void addGpmnModel(IClient client, String name, String path);
	
	/**
	 * Gets a GPMN model.
	 * @param name name of the model
	 * @return the model
	 */
	public MGpmnModel getGpmnModel(IClient client, String name);
	
	/**
	 * Gets the names of all available GPMN-models
	 * 
	 * @param client the client
	 * @return the names of all available GPMN-models
	 */
	public Set getGpmnModelNames(IClient client);
}
