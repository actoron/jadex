package jadex.tools.bpmn.runtime.task;

/**
 *  Meta information about a task.
 *  Should contain a description of what the task is useful for
 *  and which parameters it has.
 */
public class TaskMetaInfo
{
	//-------- attributes --------
	
	/** The description. */
	protected String description;
	
	/** The parameter descriptions. */
	protected ParameterMetaInfo[] parammetainfos;
	
	//-------- constructors --------
	
	/**
	 *  Create a task meta info.
	 */
	public TaskMetaInfo(String description, ParameterMetaInfo[] parammetainfos)
	{
		this.description = description;
		this.parammetainfos = parammetainfos;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the description.
	 *  @return The description.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 *  Get the parameters.
	 *  @return The parameters.
	 */
	public ParameterMetaInfo[] getParameterMetaInfos()
	{
		return this.parammetainfos;
	}
	
	
	
}
