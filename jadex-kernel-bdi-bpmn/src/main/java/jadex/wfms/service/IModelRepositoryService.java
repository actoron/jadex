package jadex.wfms.service;

import java.util.Set;

import jadex.bpmn.model.MBpmnModel;
import jadex.gpmn.model.MGpmnModel;
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
	public MGpmnModel getGpmnModel(String name);
	
	/**
	 * Gets a GPMN model path.
	 * @param name name of the model
	 * @return path to the model
	 */
	public String getGpmnModelPath(String name);
	
	/**
	 * Gets all available GPMN models.
	 * @return names of all GPMN models
	 */
	public Set getGpmnModelNames();
}
