package jadex.wfms.service;

import jadex.bridge.IComponentIdentifier;
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
	 *  @param filename The file name.
	 *  @param imports The imports.
	 *  @return The process model.
	 */
	public IFuture loadModel(String filename, String[] imports);
	
	/**
	 *  Create a new process.
	 * /
	public IProcess createProcess(String name);*/
	
	/**
	 *  Start a process instance.
	 *  @param modelName name of the process model
	 *  @param id ID of the process instance
	 *  @param arguments arguments for the process
	 *  @param stepmode enable step mode
	 *  @return assigned process instance ID
	 */
	public IFuture startProcess(String modelname, Object id, Map arguments);

	/**
	 *  Stop a process instance.
	 */
//	public void stopProcess(IProcess id);

	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param modelname The model name.
	 *  @return True, if model can be loaded.
	 */
	public IFuture isLoadable(String modelname);
	
	/**
	 * Adds a process listener to the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture addProcessListener(IComponentIdentifier client, IProcessListener listener);
	
	/**
	 * Removes a process listener from the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture removeProcessListener(IComponentIdentifier client, IProcessListener listener);
}
