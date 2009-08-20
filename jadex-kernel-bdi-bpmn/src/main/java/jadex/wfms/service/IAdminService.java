package jadex.wfms.service;

import jadex.wfms.client.IClient;

/**
 * Administrative service interface
 *
 */
public interface IAdminService
{
	/**
	 * Adds a BPMN model to the repository
	 * @param client the client
	 * @param name name of the model
	 * @param path path to the model
	 */
	public void addBpmnModel(IClient client, String name, String path);
	
	/**
	 * Adds a GPMN model to the repository
	 * @param client the client
	 * @param name name of the model
	 * @param path path to the model
	 */
	public void addGpmnModel(IClient client, String name, String path);
}
