package jadex.wfms.listeners;

/**
 * Event triggered on addition and removal of process model in the repository.
 *
 */
public class ProcessRepositoryEvent
{
	/** The process model name that triggered the event */
	private String modelName;
	
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
	public ProcessRepositoryEvent(String modelName)
	{
		this.modelName = modelName;
	}
	
	/**
	 * Returns the name of the model that triggered the event.
	 * @return name of the model
	 */
	public String getModelName()
	{
		return modelName;
	}
	
	/**
	 * Sets the name of the model that triggered the event.
	 * @param modelName name of the model
	 */
	public void setModelName(String modelName)
	{
		this.modelName = modelName;
	}
}
