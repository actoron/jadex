/**
 * 
 */
package jadex.editor.bpmn.runtime.task;

/**
 * Marker Interface for Jadex BPMN tasks
 * 
 * @author Claas
 */
public interface IEditorTask
{
	/**
	 * Access the TaskMetaInfo
	 * @return the TaskMetaInfo  
	 */
	public IEditorTaskMetaInfo getMetaInfo();
	/** getDescription() method name */
	public static final String METHOD_IJADEXTASK_GET_TASK_METAINFO = "getMetaInfo";

}
