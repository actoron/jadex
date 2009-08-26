package jadex.wfms.service.impl;

import jadex.wfms.IWfms;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IAdminService;
import jadex.wfms.service.IModelRepositoryService;

import java.security.AccessControlException;

/**
 * 
 */
public class AdminConnector implements IAdminService
{
	/** The WFMS */
	private IWfms wfms;
	
	public AdminConnector(IWfms wfms)
	{
		this.wfms = wfms;
	}
	
	/**
	 *  Add a process model.
	 *  @param client The client.
	 *  @param name The name of the model.
	 *  @param path The path to the model.
	 */
	public void addProcessModel(IClient client, String name, String path)
	{
		if(!((IAAAService)wfms.getService(IAAAService.class)).accessAction(client, IAAAService.ADD_BPMN_PROCESS_MODEL))
			throw new AccessControlException("Insufficient access rights: "+client);
		
		IModelRepositoryService mr = (IModelRepositoryService)wfms.getService(IModelRepositoryService.class);
		mr.addProcessModel(name, path);
	}
	
	/**
	 *  Remove a process model.
	 *  @param client The client.
	 *  @param name The name of the model.
	 *  @param path The path to the model.
	 */
	public void removeProcessModel(IClient client, String name)
	{
	
	}
	
	/**
	 *  Adds a BPMN model to the repository
	 *  @param client the client
	 *  @param name name of the model
	 *  @param path path to the model
	 */
	public void addBpmnModel(IClient client, String name, String path)
	{
		if(!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.ADD_BPMN_PROCESS_MODEL))
			return;
		BasicModelRepositoryService mr = (BasicModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		mr.addProcessModel(name, path);
	}
	
	/**
	 *  Adds a GPMN model to the repository
	 *  @param client the client
	 *  @param name name of the model
	 *  @param path path to the model
	 */
	public void addGpmnModel(IClient client, String name, String path)
	{
		if(!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.ADD_GPMN_PROCESS_MODEL))
			return;
		BasicModelRepositoryService mr = (BasicModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		mr.addProcessModel(name, path);
	}
}
