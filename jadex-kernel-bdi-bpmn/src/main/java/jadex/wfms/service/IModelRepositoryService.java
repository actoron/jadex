package jadex.wfms.service;

import java.util.Set;

import jadex.bpmn.model.MBpmnModel;
/**
 * Repository service for accessing process models.
 *
 */
public interface IModelRepositoryService
{
	/**
	 * Gets a BPMN model.
	 * @param name name of the model
	 * @return the model
	 */
	public MBpmnModel getBpmnModel(String name);
	
	/**
	 * Gets all available BPMN models.
	 * @return names of all BPMN models
	 */
	public Set getBpmnModelNames();
	
	/**
	 * Gets a GPMN model.
	 * @param name name of the model
	 * @return the model
	 */
	public String getGpmnModel(String name);
	
	/**
	 * Gets all available GPMN models.
	 * @return names of all GPMN models
	 */
	public Set getGpmnModelNames();
}
