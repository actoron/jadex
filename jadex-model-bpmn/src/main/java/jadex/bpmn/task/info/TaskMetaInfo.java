package jadex.bpmn.task.info;

import java.util.ArrayList;
import java.util.List;

import jadex.commons.transformation.annotations.IncludeFields;

/**
 *  Meta information about a task.
 *  Should contain a description of what the task is useful for
 *  and which parameters it has.
 */
@IncludeFields
public class TaskMetaInfo
{
	//-------- attributes --------
	
	/** The description. */
	protected String description;
	
	/** The parameter descriptions. */
//	protected ParameterMetaInfo[] parammetainfos;
	protected List<ParameterMetaInfo> parameterinfos;
	
	//-------- constructors --------
	
	/**
	 *  Create a task meta info.
	 */
	public TaskMetaInfo()
	{
	}
	
//	/**
//	 *  Create a task meta info.
//	 */
//	public TaskMetaInfo(TaskMetaInfo orig)
//	{
//		this(orig.getDescription(), orig.getParameterMetaInfos());
//	}
	
	/**
	 *  Create a task meta info.
	 */
//	public TaskMetaInfo(String description, ParameterMetaInfo[] parammetainfos)
	public TaskMetaInfo(String description, List<ParameterMetaInfo> parameterinfos)
	{
		this.description = description;
		this.parameterinfos = parameterinfos;
	}
	
	/**
	 *  Create a task meta info.
	 */
	public TaskMetaInfo(String description, ParameterMetaInfo[] parameterinfos)
	{
		this.description = description;
		this.parameterinfos = new ArrayList<ParameterMetaInfo>();
		for(ParameterMetaInfo pmi: parameterinfos)
		{
			this.parameterinfos.add(pmi);
		}
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
	 *  Sets the description.
	 *
	 *  @param description The description.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 *  Get the parameterinfos.
	 *  @return The parameterinfos.
	 */
	public List<ParameterMetaInfo> getParameterInfos()
	{
		return parameterinfos;
	}

	/**
	 *  Set the parameterinfos.
	 *  @param parameterinfos The parameterinfos to set.
	 */
	public void setParameterInfos(List<ParameterMetaInfo> parameterinfos)
	{
		this.parameterinfos = parameterinfos;
	}

//	/**
//	 *  Get the parameters.
//	 *  @return The parameters.
//	 */
//	public ParameterMetaInfo[] getParameterMetaInfos()
//	{
//		return this.parammetainfos;
//	}
//
//	/**
//	 *  Set the parameters.
//	 *
//	 *  @param parammetainfos The parameters.
//	 */
//	public void setParameterMetaInfos(ParameterMetaInfo[] parammetainfos)
//	{
//		this.parammetainfos = parammetainfos;
//	}
}
