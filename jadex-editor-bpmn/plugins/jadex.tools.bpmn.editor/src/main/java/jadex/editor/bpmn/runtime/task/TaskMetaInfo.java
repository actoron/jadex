package jadex.editor.bpmn.runtime.task;

/**
 *  Meta information about a task.
 *  Should contain a description of what the task is useful for
 *  and which parameters it has.
 */
public class TaskMetaInfo implements IEditorTaskMetaInfo
{
	//-------- attributes --------
	
	/** The description. */
	protected String description;
	
	/** The parameter descriptions. */
	protected IEditorParameterMetaInfo[] parammetainfos;

	/**
	 *  Create a task meta info.
	 */
	public TaskMetaInfo(String description, IEditorParameterMetaInfo[] parammetainfos)
	{
		this.description = description;
		this.parammetainfos = parammetainfos;
	}
	
	//-------- methods --------
	
	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.IEditorTaskMetaInfo#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return description;
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.IEditorTaskMetaInfo#getParameterMetaInfos()
	 */
	@Override
	public IEditorParameterMetaInfo[] getParameterMetaInfos()
	{
		return this.parammetainfos;
	}
	
	
	
}
