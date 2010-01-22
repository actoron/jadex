package jadex.wfms.bdi.ontology;

import jadex.adapter.base.fipa.IAgentAction;

/**
 * Request to remove a process model from the repository.
 *
 */
public class RequestRemoveProcess implements IAgentAction
{
	/** Name of the process model */
	private String processName;
	
	/**
	 * Creates a new RequestRemoveProcess.
	 */
	public RequestRemoveProcess()
	{
	}
	
	/**
	 * Creates a new RequestRemoveProcess.
	 * @param processName name of the process model
	 */
	public RequestRemoveProcess(String processName)
	{
		this.processName = processName;
	}
	
	/**
	 * Gets the name of the process model.
	 * @return name of the process model
	 */
	public String getProcessName()
	{
		return processName;
	}
	
	/**
	 * Sets the name of the process model.
	 * @param processName name of the process model
	 */
	public void setProcessName(String processName)
	{
		this.processName = processName;
	}
}
