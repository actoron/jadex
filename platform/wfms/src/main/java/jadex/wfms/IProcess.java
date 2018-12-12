package jadex.wfms;

import jadex.bridge.modelinfo.IModelInfo;

/**
 *  Interface for process instances.
 */
public interface IProcess
{
	/**
	 *  Get the process id.
	 *  @return The process id.
	 */
	public Object getId();
	
	/**
	 *  Get the process model.
	 *  @return The process model.
	 */
	public IModelInfo getModel();
}
