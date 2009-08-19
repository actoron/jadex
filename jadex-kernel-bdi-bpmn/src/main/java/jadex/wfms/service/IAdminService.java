package jadex.wfms.service;

/**
 * Administrative service interface
 *
 */
public interface IAdminService
{
	/**
	 * Adds a BPMN model to the repository
	 * @param name name of the model
	 * @param path path to the model
	 */
	public void addBpmnModel(String name, String path);
	
	
}
