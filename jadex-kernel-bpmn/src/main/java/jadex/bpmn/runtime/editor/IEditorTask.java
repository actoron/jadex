/**
 * 
 */
package jadex.bpmn.runtime.editor;

/**
 * <h1>This is only a COPY from the BPMN editor</h1>
 * Marker Interface for Jadex BPMN tasks
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
