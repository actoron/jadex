/**
 * 
 */
package jadex.tools.bpmn.runtime.task;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Claas
 *
 */
public abstract class RuntimeTaskProviderSupport implements IRuntimeTaskProvider
{

	// ---- attributes ----
	
	/**
	 * The provided task implementation classes for this {@link IRuntimeTaskProvider}
	 */
	protected String[] taskImplementations;
	
	/**
	 * Map for provided runtime classes<p>
	 * Map(ClassName, TaskMetaInfo)
	 */
	protected Map<String, TaskMetaInfo> metaInfoMap;
	
	

	// ---- constructor ----
	
	/**
	 * Empty default constructor
	 */
	public RuntimeTaskProviderSupport()
	{
		super();
		taskImplementations = new String[]{""};
		metaInfoMap = new HashMap<String, TaskMetaInfo>();
	}

	/**
	 * @param taskImplementations
	 * @param metaInfoMap
	 */
	public RuntimeTaskProviderSupport(String[] taskImplementations,
			HashMap<String, TaskMetaInfo> metaInfoMap)
	{
		super();
		this.taskImplementations = taskImplementations;
		this.metaInfoMap = metaInfoMap;
	}


	// ---- interface methods ----

	/**
	 * Get the provided task implementations
	 * Per default return an String[] with an empty String
	 * @see jadex.tools.bpmn.runtime.task.IRuntimeTaskProvider#getAvailableTaskImplementations()
	 */
	public String[] getAvailableTaskImplementations()
	{
		return taskImplementations;
	}

	
	/**
	 * Get {@link TaskMetaInfo} for provided task implementation.
	 * @see jadex.tools.bpmn.runtime.task.IRuntimeTaskProvider#getTaskMetaInfoFor(java.lang.String)
	 */
	public TaskMetaInfo getTaskMetaInfoFor(String implementationClass)
	{
		if (metaInfoMap != null)
		{
			TaskMetaInfo info = (TaskMetaInfo) metaInfoMap.get(implementationClass);
			if (info != null)
			{
				return info;
			}
		}
		
		return new TaskMetaInfo("No MetaInfo available", new ParameterMetaInfo[0]);
	}

}
