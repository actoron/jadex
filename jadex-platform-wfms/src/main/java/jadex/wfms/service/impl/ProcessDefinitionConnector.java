package jadex.wfms.service.impl;

import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.wfms.client.IClient;
import jadex.wfms.listeners.IProcessRepositoryListener;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IAuthenticationListener;
import jadex.wfms.service.IExecutionService;
import jadex.wfms.service.IModelRepositoryService;
import jadex.wfms.service.IProcessDefinitionService;

import java.net.URL;
import java.security.AccessControlException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ProcessDefinitionConnector extends BasicService implements IProcessDefinitionService
{
	/** The WFMS */
	private IServiceProvider provider;
	
	/** Model repository listeners */
	private Map repositoryListeners;
	
	public ProcessDefinitionConnector(IServiceProvider provider)
	{
		super(provider.getId(), IProcessDefinitionService.class, null);
		//super(BasicService.createServiceIdentifier(provider.getId(), ProcessDefinitionConnector.class));

		this.provider = provider;
		this.repositoryListeners = new HashMap();
	}
	
	/**
	 * Adds a process model resource to the repository
	 * @param client the client
	 * @param url url to the model resource
	 */
	public synchronized void addProcessResource(IClient client, URL url)
	{
		if (!((IAAAService) SServiceProvider.getService(provider, IAAAService.class).get(new ThreadSuspendable())).accessAction(client, IAAAService.PD_ADD_PROCESS_MODEL))
			throw new AccessControlException("Not allowed: "+client);
		IModelRepositoryService mr = (IModelRepositoryService) SServiceProvider.getService(provider, IModelRepositoryService.class).get(new ThreadSuspendable());
		mr.addProcessResource(url);
	}
	
	/**
	 * Removes a process model resource from the repository
	 * @param client the client
	 * @param url url of the model resource
	 */
	public synchronized void removeProcessResource(IClient client, URL url)
	{
		if (!((IAAAService) SServiceProvider.getService(provider, IAAAService.class).get(new ThreadSuspendable())).accessAction(client, IAAAService.PD_REMOVE_PROCESS_MODEL))
			throw new AccessControlException("Not allowed: "+client);
		IModelRepositoryService mr = (IModelRepositoryService) SServiceProvider.getService(provider, IModelRepositoryService.class).get(new ThreadSuspendable());
		mr.removeProcessResource(url);
	}
	
	/**
	 * Gets a process model.
	 * @param name name of the model
	 * @return the model
	 */
	public synchronized IFuture getProcessModel(IClient client, String name)
	{
		if (!((IAAAService) SServiceProvider.getService(provider, IAAAService.class).get(new ThreadSuspendable())).accessAction(client, IAAAService.PD_REQUEST_PROCESS_MODEL))
			throw new AccessControlException("Not allowed: "+client);
		IModelRepositoryService mr = (IModelRepositoryService) SServiceProvider.getService(provider, IModelRepositoryService.class).get(new ThreadSuspendable());
		
		return mr.getProcessModel(name);
	}
	
	/**
	 * Loads a process model not listed in the model repository.
	 * @param path path of the model
	 * @return the model
	 */
	public synchronized IFuture loadProcessModel(IClient client, String path, String[] imports)
	{
		if (!((IAAAService) SServiceProvider.getService(provider, IAAAService.class).get(new ThreadSuspendable())).accessAction(client, IAAAService.PD_REQUEST_PROCESS_MODEL))
			throw new AccessControlException("Not allowed: "+client);
		IExecutionService es = (IExecutionService) SServiceProvider.getService(provider, IExecutionService.class).get(new ThreadSuspendable());
		
		
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
		if (!((IAAAService) SServiceProvider.getService(provider, IAAAService.class).get(new ThreadSuspendable())).accessAction(client, IAAAService.PD_REQUEST_MODEL_NAMES))
			throw new AccessControlException("Not allowed: "+client);
		IModelRepositoryService rs = (IModelRepositoryService) SServiceProvider.getService(provider, IModelRepositoryService.class).get(new ThreadSuspendable());
		return new HashSet(rs.getModelNames());
	}
	
	/**
	 * Returns a potentially incomplete set of loadable model paths
	 * 
	 * @return set of model paths
	 */
	public Set getLoadableModelPaths(IClient client)
	{
		if (!((IAAAService) SServiceProvider.getService(provider, IAAAService.class).get(new ThreadSuspendable())).accessAction(client, IAAAService.PD_REQUEST_MODEL_PATHS))
			throw new AccessControlException("Not allowed: "+client);
		IModelRepositoryService rs = (IModelRepositoryService) SServiceProvider.getService(provider, IModelRepositoryService.class).get(new ThreadSuspendable());
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
		IAAAService as = ((IAAAService) SServiceProvider.getService(provider, IAAAService.class).get(new ThreadSuspendable()));
		if (!as.accessAction(client, IAAAService.PD_ADD_REPOSITORY_LISTENER))
			throw new AccessControlException("Not allowed: "+client);
		IModelRepositoryService rs = (IModelRepositoryService) SServiceProvider.getService(provider, IModelRepositoryService.class).get(new ThreadSuspendable());
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
						 ((IModelRepositoryService) SServiceProvider.getService(provider, IModelRepositoryService.class).get(new ThreadSuspendable())).removeProcessRepositoryListener(l);
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
		if (!((IAAAService) SServiceProvider.getService(provider, IAAAService.class).get(new ThreadSuspendable())).accessAction(client, IAAAService.PD_REMOVE_REPOSITORY_LISTENER))
			throw new AccessControlException("Not allowed: "+client);
		IModelRepositoryService rs = (IModelRepositoryService) SServiceProvider.getService(provider, IModelRepositoryService.class).get(new ThreadSuspendable());
		rs.removeProcessRepositoryListener(listener);
		
		((Set) repositoryListeners.get(client)).remove(listener);
	}
}
