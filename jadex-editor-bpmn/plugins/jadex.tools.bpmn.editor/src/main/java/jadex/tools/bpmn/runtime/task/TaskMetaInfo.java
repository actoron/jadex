package jadex.tools.bpmn.runtime.task;

/**
 *  Meta information about a task.
 *  Should contain a description of what the task is useful for
 *  and which parameters it has.
 */
public class TaskMetaInfo implements ITaskMetaInfo
{
	//-------- attributes --------
	
	/** The description. */
	protected String description;
	
	/** The parameter descriptions. */
	protected IParameterMetaInfo[] parammetainfos;

	/**
	 *  Create a task meta info.
	 */
	public TaskMetaInfo(String description, IParameterMetaInfo[] parammetainfos)
	{
		this.description = description;
		this.parammetainfos = parammetainfos;
	}
	
	//-------- methods --------
	
	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.ITaskMetaInfo#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return description;
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.ITaskMetaInfo#getParameterMetaInfos()
	 */
	@Override
	public IParameterMetaInfo[] getParameterMetaInfos()
	{
		return this.parammetainfos;
	}
	
	
	
}
