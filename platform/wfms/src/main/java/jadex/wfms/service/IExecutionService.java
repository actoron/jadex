package jadex.wfms.service;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.commons.future.IFuture;
import jadex.wfms.service.listeners.IProcessListener;

import java.util.Map;

/**
 * 
 */
public interface IExecutionService
{
	/**
	 *  Load a process model.
	 *  @param info The process resource information.
	 *  @return The process model.
	 */
	public IFuture<IModelInfo> loadModel(ProcessResourceInfo info);
	
	/**
	 *  Create a new process.
	 * /
	public IProcess createProcess(String name);*/
	
	/**
	 *  Start a process instance.
	 *  
	 *  @param info The process resource information.
	 *  @param id ID of the process instance
	 *  @param arguments arguments for the process
	 *  @return assigned process instance ID
	 */
	public IFuture<IComponentIdentifier> startProcess(ProcessResourceInfo info, Object id, Map arguments);

	/**
	 *  Stop a process instance.
	 */
//	public void stopProcess(IProcess id);

	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param info The process resource information.
	 *  @return True, if model can be loaded.
	 */
	public IFuture<Boolean> isLoadable(ProcessResourceInfo info);
	
	/**
	 * Adds a process listener to the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture<Void> addProcessListener(IComponentIdentifier client, IProcessListener listener);
	
	/**
	 * Removes a process listener from the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture<Void> removeProcessListener(IComponentIdentifier client, IProcessListener listener);
}
