package jadex.wfms.service;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;
import jadex.wfms.client.ProcessResource;
import jadex.wfms.service.listeners.IProcessRepositoryListener;

import java.util.List;
/**
 * Repository service for accessing process models.
 */
public interface IModelRepositoryService
{
	/**
	 *  Deploy a process model resource.
	 *  @param url The URL of the model resource.
	 */
	public IFuture<Void> addProcessResource(ProcessResource resource);
	
	/**
	 *  Remove a process model resource.
	 *  @param info The process resource info.
	 */
	public IFuture removeProcessResource(ProcessResourceInfo info);
	
	/**
	 * Gets all available models.
	 * @return names of all models
	 */
	//public IFuture getModelNames();
	
	/**
	 * Returns a potentially incomplete set of loadable models
	 * 
	 * @return set of model paths
	 */
	//public IFuture getLoadableModels();
	
	/**
	 *  Gets all available models.
	 *  
	 *  @return Resource information about all known models
	 */
	public IFuture<List<ProcessResourceInfo>> getModels();
	
	/**
	 *  Get a process model info of a specific name.
	 *  @param rinfo Process resource information.
	 */
	public IFuture<IModelInfo> getProcessModelInfo(ProcessResourceInfo rinfo);
	
	/**
	 *  Get a process model file name of a specific name.
	 *  @param name The model name.
	 *  @return The process model file name.
	 */
	//public IFuture getProcessFileName(String name);
	
	/**
	 *  Get the imports.
	 */
	//public IFuture getImports();
	
	/**
	 * Adds a process repository listener.
	 * 
	 * @param listener the listener
	 */
	public IFuture<Void> addProcessRepositoryListener(IComponentIdentifier client, IProcessRepositoryListener listener);
	
	/**
	 * Removes a process repository listener.
	 * 
	 * @param listener the listener
	 */
	public IFuture<Void> removeProcessRepositoryListener(IComponentIdentifier client, IProcessRepositoryListener listener);
}
