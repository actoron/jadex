package jadex.wfms.service.listeners;

import jadex.wfms.service.ProcessResourceInfo;

/**
 * Event triggered on addition and removal of process model in the repository.
 *
 */
public class ProcessRepositoryEvent
{
	/** The process model resource information that triggered the event */
	private ProcessResourceInfo info;
	
	/**
	 * Creates a new ProcessRepositoryEvent.
	 */
	public ProcessRepositoryEvent()
	{
	}
	
	/**
	 * Creates a new ProcessRepositoryEvent.
	 * @param modelName name of the model
	 */
	public ProcessRepositoryEvent(ProcessResourceInfo info)
	{
		this.info = info;
	}

	/**
	 *  Gets the process resource information.
	 *
	 *  @return The process resource information.
	 */
	public ProcessResourceInfo getProcessInformation()
	{
		return info;
	}

	/**
	 *  Sets the process resource information.
	 *
	 *  @param info The process resource information.
	 */
	public void setProcessInformation(ProcessResourceInfo info)
	{
		this.info = info;
	}
	
	
}
