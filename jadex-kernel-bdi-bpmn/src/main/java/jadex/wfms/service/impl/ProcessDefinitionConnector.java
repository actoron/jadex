package jadex.wfms.service.impl;

import java.util.HashSet;
import java.util.Set;

import jadex.bpmn.model.MBpmnModel;
import jadex.commons.concurrent.IResultListener;
import jadex.gpmn.model.MGpmnModel;
import jadex.service.execution.IExecutionService;
import jadex.wfms.IWfms;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IProcessDefinitionService;
import jadex.wfms.service.IModelRepositoryService;

public class ProcessDefinitionConnector implements IProcessDefinitionService
{
	/** The WFMS */
	private IWfms wfms;
	
	public ProcessDefinitionConnector(IWfms wfms)
	{
		this.wfms = wfms;
	}
	
	/**
	 *  Start the service.
	 */
	public void start()
	{
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public void shutdown(IResultListener listener)
	{
	}
	
	/**
	 * Adds a BPMN model to the repository
	 * 
	 * @param client the client
	 * @param name name of the model
	 * @param path path to the model
	 */
	public void addBpmnModel(IClient client, String path)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.ADD_BPMN_PROCESS_MODEL))
			return;
		BasicModelRepositoryService mr = (BasicModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		mr.addProcessModel(path);
	}
	
	/**
	 * Gets a BPMN model.
	 * @param name name of the model
	 * @return the model
	 */
	public MBpmnModel getBpmnModel(IClient client, String name)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.REQUEST_BPMN_PROCESS_MODEL))
			return null;
		IModelRepositoryService mr = (IModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		
		return (MBpmnModel)mr.getProcessModel(name);
	}
	
	/**
	 * Gets the names of all available BPMN-models
	 * 
	 * @param client the client
	 * @return the names of all available BPMN-models
	 */
	public Set getBpmnModelNames(IClient client)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.REQUEST_BPMN_MODEL_NAMES))
			return null;
		IModelRepositoryService rs = (IModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		return new HashSet(rs.getModelNames());
	}
	
	/**
	 * Adds a GPMN model to the repository
	 * @param client the client
	 * @param name name of the model
	 * @param path path to the model
	 */
	public void addGpmnModel(IClient client, String path)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.ADD_GPMN_PROCESS_MODEL))
			return;
		BasicModelRepositoryService mr = (BasicModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		mr.addProcessModel(path);
	}
	
	/**
	 * Gets a GPMN model.
	 * @param name name of the model
	 * @return the model
	 */
	public MGpmnModel getGpmnModel(IClient client, String name)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.REQUEST_GPMN_PROCESS_MODEL))
			return null;
		IModelRepositoryService mr = (IModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		
		// todo:
		return null;
//		return mr.getGpmnModel(name);
	}
	
	/**
	 * Gets the names of all available GPMN-models
	 * 
	 * @param client the client
	 * @return the names of all available GPMN-models
	 */
	public Set getGpmnModelNames(IClient client)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.REQUEST_GPMN_MODEL_NAMES))
			return null;
		IModelRepositoryService rs = (IModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		return new HashSet(rs.getModelNames());
	}
}
