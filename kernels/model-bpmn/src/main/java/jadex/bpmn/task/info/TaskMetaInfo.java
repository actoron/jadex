package jadex.bpmn.task.info;

import java.util.ArrayList;
import java.util.List;

import jadex.bridge.ClassInfo;
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
	
	/** The property descriptions. */
	protected List<PropertyMetaInfo> propertyinfos;
	
	/** The gui class. */
	protected ClassInfo guicl;
	
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
	public TaskMetaInfo(String description, List<ParameterMetaInfo> parameterinfos,
		List<PropertyMetaInfo> propertyinfos, ClassInfo guicl)
	{
		this.description = description;
		this.parameterinfos = parameterinfos;
		this.propertyinfos = propertyinfos;
		this.guicl = guicl;
	}
	
	/**
	 *  Create a task meta info.
	 */
	public TaskMetaInfo(String description, ParameterMetaInfo[] parameterinfos)
	{
		this(description, parameterinfos, null);
	}
	
	/**
	 *  Create a task meta info.
	 */
	public TaskMetaInfo(String description, ParameterMetaInfo[] parameterinfos, PropertyMetaInfo[] propertyinfos)
	{
		this.description = description;
		this.parameterinfos = new ArrayList<ParameterMetaInfo>();
		for(ParameterMetaInfo pmi: parameterinfos)
		{
			this.parameterinfos.add(pmi);
		}
		this.propertyinfos = new ArrayList<PropertyMetaInfo>();
		if(propertyinfos!=null)
		{
			for(PropertyMetaInfo pmi: propertyinfos)
			{
				this.propertyinfos.add(pmi);
			}
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

	/**
	 *  Get the propertyinfos.
	 *  @return The propertyinfos.
	 */
	public List<PropertyMetaInfo> getPropertyInfos()
	{
		return propertyinfos;
	}

	/**
	 *  Set the propertyinfos.
	 *  @param propertyinfos The propertyinfos to set.
	 */
	public void setPropertyInfos(List<PropertyMetaInfo> propertyinfos)
	{
		this.propertyinfos = propertyinfos;
	}

	/**
	 *  Get the guicl.
	 *  @return The guicl.
	 */
	public ClassInfo getGuiClassInfo()
	{
		return guicl;
	}

	/**
	 *  Set the guicl.
	 *  @param guicl The guicl to set.
	 */
	public void setGuiClassInfo(ClassInfo guicl)
	{
		this.guicl = guicl;
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
