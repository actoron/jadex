package jadex.wfms.service.definition;

import jadex.bridge.ILoadableComponentModel;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;
import jadex.wfms.client.IClient;
import jadex.wfms.service.repository.BasicModelRepositoryService;
import jadex.wfms.service.repository.IModelRepositoryService;
import jadex.wfms.service.security.IAAAService;

import java.util.HashSet;
import java.util.Set;

public class ProcessDefinitionConnector implements IProcessDefinitionService
{
	/** The WFMS */
	private IServiceContainer container;
	
	public ProcessDefinitionConnector(IServiceContainer container)
	{
		this.container = container;
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
		if (!((IAAAService) container.getService(IAAAService.class)).accessAction(client, IAAAService.ADD_PROCESS_MODEL))
			return;
		BasicModelRepositoryService mr = (BasicModelRepositoryService)container.getService(IModelRepositoryService.class);
		mr.addProcessModel(path);
	}
	
	/**
	 * Gets a process model.
	 * @param name name of the model
	 * @return the model
	 */
	public ILoadableComponentModel getProcessModel(IClient client, String name)
	{
		if (!((IAAAService)container.getService(IAAAService.class)).accessAction(client, IAAAService.REQUEST_PROCESS_MODEL))
			return null;
		IModelRepositoryService mr = (IModelRepositoryService)container.getService(IModelRepositoryService.class);
		
		return mr.getProcessModel(name);
	}
	
	/**
	 * Gets the names of all available process models
	 * 
	 * @param client the client
	 * @return the names of all available process models
	 */
	public Set getProcessModelNames(IClient client)
	{
		if (!((IAAAService)container.getService(IAAAService.class)).accessAction(client, IAAAService.REQUEST_MODEL_NAMES))
			return null;
		IModelRepositoryService rs = (IModelRepositoryService)container.getService(IModelRepositoryService.class);
		return new HashSet(rs.getModelNames());
	}
}
