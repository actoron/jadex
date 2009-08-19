package jadex.wfms.service.impl;

import jadex.wfms.IWfms;
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
	 * @param name name of the model
	 * @param path path to the model
	 */
	public void addBpmnModel(String name, String path)
	{
		BasicModelRepositoryService mr = (BasicModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		mr.addBpmnModel(name, path);
	}

}
