/**
 * 
 */
package jadex.tools.bpmn.runtime.task;

/**
 * Marker Interface for Jadex BPMN tasks
 * 
 * @author Claas
 */
public interface IJadexTask
{
	/**
	 * Access the TaskMetaInfo
	 * @return the TaskMetaInfo  
	 */
	public ITaskMetaInfo getTaskMetaInfo();
	/** getDescription() method name */
	public static final String METHOD_IJADEXTASK_GET_TASK_METAINFO = "getTaskMetaInfo";

}
