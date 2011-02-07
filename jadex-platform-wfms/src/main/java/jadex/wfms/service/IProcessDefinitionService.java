package jadex.wfms.service;

import jadex.commons.future.IFuture;
import jadex.wfms.client.IClient;
import jadex.wfms.listeners.IProcessRepositoryListener;

import java.net.URL;
import java.util.Set;

/**
 * Administrative service interface
 *
 */
public interface IProcessDefinitionService
{
	/**
	 * Adds a process model resource to the repository
	 * @param client the client
	 * @param url url to the model resource
	 */
	public void addProcessResource(IClient client, URL url);
	
	/**
	 * Removes a process model resource from the repository
	 * @param client the client
	 * @param url url of the model resource
	 */
	public void removeProcessResource(IClient client, URL url);
	
	/**
	 * Gets a process model.
	 * @param name name of the model
	 * @return the model
	 */
	public IFuture getProcessModel(IClient client, String name);
	
	/**
	 * Loads a process model not listed in the model repository.
	 * @param client the client
	 * @param path path of the model
	 * @param imports the imports
	 * @return the model
	 */
	public IFuture loadProcessModel(IClient client, String path, String[] imports);
	
	/**
	 * Gets the names of all available process models
	 * 
	 * @param client the client
	 * @return the names of all available process models
	 */
	public Set getProcessModelNames(IClient client);
	
	/**
	 * Returns a potentially incomplete set of loadable model paths
	 * 
	 * @return set of model paths
	 */
	public Set getLoadableModelPaths(IClient client);
	
	/**
	 * Adds a process repository listener.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public void addProcessRepositoryListener(IClient client, IProcessRepositoryListener listener);
	
	/**
	 * Removes a process repository listener.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public void removeProcessRepositoryListener(IClient client, IProcessRepositoryListener listener);
}
