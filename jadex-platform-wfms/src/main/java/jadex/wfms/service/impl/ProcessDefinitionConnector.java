package jadex.wfms.service.impl;

import jadex.bridge.ILoadableComponentModel;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IExecutionService;
import jadex.wfms.service.IModelRepositoryService;
import jadex.wfms.service.IProcessDefinitionService;

import java.util.HashSet;
import java.util.Set;

public class ProcessDefinitionConnector implements IProcessDefinitionService
{
	/** The WFMS */
	private IServiceContainer wfms;
	
	public ProcessDefinitionConnector(IServiceContainer wfms)
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
	 * Adds a process model to the repository
	 * @param client the client
	 * @param name name of the model
	 * @param path path to the model
	 */
	public void addProcessModel(IClient client, String path)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.ADD_PROCESS_MODEL))
			return;
		BasicModelRepositoryService mr = (BasicModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		mr.addProcessModel(path);
	}
	
	/**
	 * Gets a process model.
	 * @param name name of the model
	 * @return the model
	 */
	public ILoadableComponentModel getProcessModel(IClient client, String name)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.REQUEST_PROCESS_MODEL))
			return null;
		IModelRepositoryService mr = (IModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		
		return mr.getProcessModel(name);
	}
	
	/**
	 * Loads a process model not listed in the model repository.
	 * @param path path of the model
	 * @return the model
	 */
	public ILoadableComponentModel loadProcessModel(IClient client, String path, String[] imports)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.REQUEST_PROCESS_MODEL))
			return null;
		IExecutionService es = (IExecutionService) wfms.getService(IExecutionService.class);
		
		
		return es.loadModel(path, imports);
	}
	
	/**
	 * Gets the names of all available process models
	 * 
	 * @param client the client
	 * @return the names of all available process models
	 */
	public Set getProcessModelNames(IClient client)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.REQUEST_MODEL_NAMES))
			return null;
		IModelRepositoryService rs = (IModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		return new HashSet(rs.getModelNames());
	}
}
