package jadex.wfms.service.execution;

import jadex.bridge.IElementFactory;
import jadex.service.IService;

import java.util.Map;

/**
 * 
 */
public interface IExecutionService extends IService, IElementFactory
{
	/**
	 *  Load a process model.
	 *  @param filename The file name.
	 *  @param imports The imports.
	 *  @return The process model.
	 */
//	public IProcessModel loadModel(String filename, String[] imports);
	
	/**
	 *  Create a new process.
	 * /
	public IProcess createProcess(String name);*/
	
	/**
	 *  Start a process instance.
	 */
	public Object startProcess(String modelname, Object id, Map arguments, boolean stepmode);

	/**
	 *  Stop a process instance.
	 */
//	public void stopProcess(IProcess id);

	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param modelname The model name.
	 *  @return True, if model can be loaded.
	 */
//	public boolean isLoadable(String modelname);
}
