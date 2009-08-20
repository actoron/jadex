package jadex.wfms.service.impl;

import jadex.wfms.IWfms;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IAdminService;
import jadex.wfms.service.IModelRepositoryService;

public class AdminConnector implements IAdminService
{
	/** The WFMS */
	private IWfms wfms;
	
	public AdminConnector(IWfms wfms)
	{
		this.wfms = wfms;
	}
	
	/**
	 * Adds a BPMN model to the repository
	 * 
	 * @param client the client
	 * @param name name of the model
	 * @param path path to the model
	 */
	public void addBpmnModel(IClient client, String name, String path)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.ADD_BPMN_PROCESS))
			return;
		BasicModelRepositoryService mr = (BasicModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		mr.addBpmnModel(name, path);
	}
	
	/**
	 * Adds a GPMN model to the repository
	 * @param client the client
	 * @param name name of the model
	 * @param path path to the model
	 */
	public void addGpmnModel(IClient client, String name, String path)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.ADD_GPMN_PROCESS))
			return;
		BasicModelRepositoryService mr = (BasicModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		mr.addGpmnModel(name, path);
	}
}
