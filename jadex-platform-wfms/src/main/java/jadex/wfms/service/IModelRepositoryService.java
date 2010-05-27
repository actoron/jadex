package jadex.wfms.service;

import jadex.bridge.ILoadableComponentModel;
import jadex.wfms.listeners.IProcessRepositoryListener;

import java.net.URL;
import java.util.Collection;
import java.util.Set;
/**
 * Repository service for accessing process models.
 */
public interface IModelRepositoryService
{
	/**
	 *  Add a process model resource.
	 *  @param url The URL of the model resource.
	 */
	public void addProcessResource(URL url);
	
	/**
	 *  Remove a process model resource.
	 *  @param url The URL of the model resource.
	 */
	public void removeProcessResource(URL url);
	
	/**
	 * Gets all available models.
	 * @return names of all models
	 */
	public Collection getModelNames();
	
	/**
	 * Returns a potentially incomplete set of loadable models
	 * 
	 * @return set of model paths
	 */
	public Set getLoadableModels();
	
	/**
	 *  Get a process model of a specific name.
	 *  @param name The model name.
	 *  @return The process model.
	 */
	public ILoadableComponentModel getProcessModel(String name);
	
	/**
	 *  Get the imports.
	 */
	public String[] getImports();
	
	/**
	 * Adds a process repository listener.
	 * 
	 * @param listener the listener
	 */
	public void addProcessRepositoryListener(IProcessRepositoryListener listener);
	
	/**
	 * Removes a process repository listener.
	 * 
	 * @param listener the listener
	 */
	public void removeProcessRepositoryListener(IProcessRepositoryListener listener);
}
