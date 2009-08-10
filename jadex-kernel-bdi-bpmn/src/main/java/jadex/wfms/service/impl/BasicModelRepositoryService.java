package jadex.wfms.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jadex.bpmn.BpmnModelLoader;
import jadex.bpmn.model.MBpmnModel;
import jadex.wfms.service.IModelRepositoryService;

/**
 * Basic Model Repository Service implementation
 *
 */
public class BasicModelRepositoryService implements IModelRepositoryService
{
	/** Map from BPMN model name to model resource */
	private Map bpmnModels;
	
	public BasicModelRepositoryService()
	{
		bpmnModels = new HashMap();
	}
	
	/**
	 * Adds a BPMN model.
	 * @param name name of the model
	 * @param path path to the model
	 */
	public synchronized void addBpmnModel(String name, String path)
	{
		bpmnModels.put(name, path);
	}
	
	/**
	 * Removes a BPMN model.
	 * @param name name of the model
	 */
	public synchronized void removeBpmnModel(String name)
	{
		bpmnModels.remove(name);
	}
	
	/**
	 * Gets a BPMN model.
	 * @param name name of the model
	 * @return the model
	 */
	public synchronized MBpmnModel getBpmnModel(String name)
	{
		try
		{
			return new BpmnModelLoader().loadBpmnModel((String) bpmnModels.get(name), null);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * Gets all available BPMN models.
	 * @return names of all BPMN models
	 */
	public synchronized Set getBpmnModelNames()
	{
		return new HashSet(bpmnModels.keySet());
	}

}
