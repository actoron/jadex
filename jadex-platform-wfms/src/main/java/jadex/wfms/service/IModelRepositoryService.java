package jadex.wfms.service;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;
import jadex.wfms.client.ProcessResource;
import jadex.wfms.service.listeners.IProcessRepositoryListener;
/**
 * Repository service for accessing process models.
 */
public interface IModelRepositoryService
{
	/**
	 *  Add a process model resource.
	 *  @param url The URL of the model resource.
	 */
	public IFuture addProcessResource(ProcessResource resource);
	
	/**
	 *  Remove a process model resource.
	 *  @param url The URL of the model resource.
	 */
	public IFuture removeProcessResource(String resourceName);
	
	/**
	 * Gets all available models.
	 * @return names of all models
	 */
	public IFuture getModelNames();
	
	/**
	 * Returns a potentially incomplete set of loadable models
	 * 
	 * @return set of model paths
	 */
	public IFuture getLoadableModels();
	
	/**
	 *  Get a process model of a specific name.
	 *  @param name The model name.
	 *  @return The process model.
	 */
	public IFuture getProcessModel(String name);
	
	/**
	 *  Get a process model file name of a specific name.
	 *  @param name The model name.
	 *  @return The process model file name.
	 */
	public IFuture getProcessFileName(String name);
	
	/**
	 *  Get the imports.
	 */
	public IFuture getImports();
	
	/**
	 * Adds a process repository listener.
	 * 
	 * @param listener the listener
	 */
	public IFuture addProcessRepositoryListener(IComponentIdentifier client, IProcessRepositoryListener listener);
	
	/**
	 * Removes a process repository listener.
	 * 
	 * @param listener the listener
	 */
	public IFuture removeProcessRepositoryListener(IComponentIdentifier client, IProcessRepositoryListener listener);
}
