package jadex.editor.bpmn.runtime.task;

import org.eclipse.emf.ecore.EModelElement;

public interface IEditorTaskProvider
{

	/** The method to dispose all resources */
	public abstract void dispose();
	/** The implementing dispose() method name*/
	public static final String METHOD_IJADEXTASKPROVIDER_DISPOSE = "dispose";
	
	/** The method to refresh all resources */
	public abstract void refresh();
	/** The implementing refresh() method name*/
	public static final String METHOD_IJADEXTASKPROVIDER_REFRESH = "refresh";
	
	/** Set selected model element  */
	public abstract void setInput(EModelElement selectedElement);
	/** The implementing setInput() method name*/
	public static final String METHOD_IJADEXTASKPROVIDER_SET_INPUT = "setInput";
	

	/** The method to access the provided task implementations */
	public abstract String[] getAvailableTaskImplementations();
	/** The implementing getAvailableTaskImplementations() method name*/
	public static final String METHOD_IJADEXTASKPROVIDER_GET_AVAILABLE_TASK_IMPLEMENTATIONS = "getAvailableTaskImplementations";
	
	
	/** The implementing getTaskMetaInfoFor(fqClassName) method name*/
	public static final String METHOD_IJADEXTASKPROVIDER_GET_TASK_META_INFO = "getTaskMetaInfoFor";
	/** The method to access the meta info for a task */
	public abstract IEditorTaskMetaInfo getTaskMetaInfo(String fqClassName);

}
