package jadex.wfms.service.impl;

import jadex.bridge.ILoadableComponentModel;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IService;
import jadex.service.IServiceContainer;
import jadex.wfms.client.IClient;
import jadex.wfms.listeners.IProcessRepositoryListener;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IAuthenticationListener;
import jadex.wfms.service.IExecutionService;
import jadex.wfms.service.IModelRepositoryService;
import jadex.wfms.service.IProcessDefinitionService;

import java.security.AccessControlException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ProcessDefinitionConnector implements IProcessDefinitionService, IService
{
	/** The WFMS */
	private IServiceContainer wfms;
	
	/** Model repository listeners */
	private Map repositoryListeners;
	
	public ProcessDefinitionConnector(IServiceContainer wfms)
	{
		this.wfms = wfms;
		this.repositoryListeners = new HashMap();
	}
	
	/**
	 *  Start the service.
	 */
	public void startService()
	{
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public void shutdownService(IResultListener listener)
	{
		if(listener!=null)
			listener.resultAvailable(this, null);
	}
	
	/**
	 * Adds a process model to the repository
	 * @param client the client
	 * @param path path to the model
	 */
	public synchronized void addProcessModel(IClient client, String path)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.PD_ADD_PROCESS_MODEL))
			throw new AccessControlException("Not allowed: "+client);
		BasicModelRepositoryService mr = (BasicModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		mr.addProcessModel(path);
	}
	
	/**
	 * Removes a process model from the repository
	 * @param client the client
	 * @param name name of the model
	 */
	public synchronized void removeProcessModel(IClient client, String name)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.PD_REMOVE_PROCESS_MODEL))
			throw new AccessControlException("Not allowed: "+client);
		BasicModelRepositoryService mr = (BasicModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		mr.removeProcessModel(name);
	}
	
	/**
	 * Gets a process model.
	 * @param name name of the model
	 * @return the model
	 */
	public synchronized ILoadableComponentModel getProcessModel(IClient client, String name)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.PD_REQUEST_PROCESS_MODEL))
			throw new AccessControlException("Not allowed: "+client);
		IModelRepositoryService mr = (IModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		
		return mr.getProcessModel(name);
	}
	
	/**
	 * Loads a process model not listed in the model repository.
	 * @param path path of the model
	 * @return the model
	 */
	public synchronized ILoadableComponentModel loadProcessModel(IClient client, String path, String[] imports)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.PD_REQUEST_PROCESS_MODEL))
			throw new AccessControlException("Not allowed: "+client);
		IExecutionService es = (IExecutionService) wfms.getService(IExecutionService.class);
		
		
		return es.loadModel(path, imports);
	}
	
	/**
	 * Gets the names of all available process models
	 * 
	 * @param client the client
	 * @return the names of all available process models
	 */
	public synchronized Set getProcessModelNames(IClient client)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.PD_REQUEST_MODEL_NAMES))
			throw new AccessControlException("Not allowed: "+client);
		IModelRepositoryService rs = (IModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		return new HashSet(rs.getModelNames());
	}
	
	/**
	 * Returns a potentially incomplete set of loadable model paths
	 * 
	 * @return set of model paths
	 */
	public Set getLoadableModelPaths(IClient client)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.PD_REQUEST_MODEL_PATHS))
			throw new AccessControlException("Not allowed: "+client);
		IModelRepositoryService rs = (IModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		return rs.getLoadableModels();
	}
	
	/**
	 * Adds a process repository listener.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public synchronized void addProcessRepositoryListener(IClient client, IProcessRepositoryListener listener)
	{
		IAAAService as = ((IAAAService) wfms.getService(IAAAService.class));
		if (!as.accessAction(client, IAAAService.PD_ADD_REPOSITORY_LISTENER))
			throw new AccessControlException("Not allowed: "+client);
		IModelRepositoryService rs = (IModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		rs.addProcessRepositoryListener(listener);
		
		Set listeners = (Set) repositoryListeners.get(client);
		if(listeners == null)
		{
			listeners = new HashSet();
			repositoryListeners.put(client, listeners);
		}
		listeners.add(listener);
		
		as.addAuthenticationListener(new IAuthenticationListener()
		{
			public void deauthenticated(IClient client)
			{
				 Set listeners = (Set) repositoryListeners.get(client);
				 if (listeners != null)
				 {
					 for (Iterator it = listeners.iterator(); it.hasNext(); )
					 {
						 IProcessRepositoryListener l = (IProcessRepositoryListener) it.next();
						 ((IModelRepositoryService) wfms.getService(IModelRepositoryService.class)).removeProcessRepositoryListener(l);
					 }
				 }
			}
			
			public void authenticated(IClient client)
			{
			}
		});
	}
	
	/**
	 * Removes a process repository listener.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public synchronized void removeProcessRepositoryListener(IClient client, IProcessRepositoryListener listener)
	{
		if (!((IAAAService) wfms.getService(IAAAService.class)).accessAction(client, IAAAService.PD_REMOVE_REPOSITORY_LISTENER))
			throw new AccessControlException("Not allowed: "+client);
		IModelRepositoryService rs = (IModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		rs.removeProcessRepositoryListener(listener);
		
		((Set) repositoryListeners.get(client)).remove(listener);
	}
}
