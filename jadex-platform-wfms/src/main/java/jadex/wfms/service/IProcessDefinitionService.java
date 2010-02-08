package jadex.wfms.service;

import jadex.bridge.ILoadableComponentModel;
import jadex.service.IService;
import jadex.wfms.client.IClient;
import jadex.wfms.listeners.IProcessRepositoryListener;

import java.util.Set;

/**
 * Administrative service interface
 *
 */
public interface IProcessDefinitionService
{
	/**
	 * Adds a process model to the repository
	 * @param client the client
	 * @param path path to the model
	 */
	public void addProcessModel(IClient client, String path);
	
	/**
	 * Removes a process model from the repository
	 * @param client the client
	 * @param name name of the model
	 */
	public void removeProcessModel(IClient client, String name);
	
	/**
	 * Gets a process model.
	 * @param name name of the model
	 * @return the model
	 */
	public ILoadableComponentModel getProcessModel(IClient client, String name);
	
	/**
	 * Loads a process model not listed in the model repository.
	 * @param client the client
	 * @param path path of the model
	 * @param imports the imports
	 * @return the model
	 */
	public ILoadableComponentModel loadProcessModel(IClient client, String path, String[] imports);
	
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
