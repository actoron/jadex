package jadex.wfms.service;

import jadex.bridge.ILoadableComponentModel;
import jadex.service.IService;
import jadex.wfms.listeners.IProcessRepositoryListener;

import java.util.Collection;
/**
 * Repository service for accessing process models.
 */
public interface IModelRepositoryService extends IService
{
	/**
	 *  Add a process model.
	 *  @param filename The file name of the model.
	 */
	public void addProcessModel(String filename);
	
	/**
	 * Gets all available models.
	 * @return names of all models
	 */
	public Collection getModelNames();
	
	/**
	 *  Remove a process model.
	 *  @param name The name of the model.
	 */
	public void removeProcessModel(String name);
	
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
