package jadex.tools.bpmn.runtime.task;

public interface IJadexTaskProvider
{

	
	
	/** The method to access the provided task implementations */
	public abstract String[] getAvailableTaskImplementations();
	/** The implementing getAvailableTaskImplementations() method name*/
	public static final String METHOD_IJADEXTASKPROVIDER_GET_AVAILABLE_TASK_IMPLEMENTATIONS = "getAvailableTaskImplementations";
	
	
	/** The implementing getTaskMetaInfoFor(fqClassName) method name*/
	public static final String METHOD_IJADEXTASKPROVIDER_GET_TASK_META_INFO = "getTaskMetaInfoFor";
	/** The method to access the meta info for a task */
	public abstract ITaskMetaInfo getTaskMetaInfo(String fqClassName);

}
