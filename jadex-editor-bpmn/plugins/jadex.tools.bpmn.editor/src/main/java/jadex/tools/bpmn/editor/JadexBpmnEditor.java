/**
 * 
 */
package jadex.tools.bpmn.editor;


import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.stp.bpmn.diagram.part.BpmnDiagramEditor;
import org.eclipse.stp.bpmn.diagram.part.BpmnDiagramEditorPlugin;

/**
 * This editor extends the BPMN-Diagram editor. With this extension we have full
 * control about the displayed properties and editor sheets. We also can extend
 * the editor with new edit parts and views.
 * 
 * @author Claas Altschaffel
 * 
 */
public class JadexBpmnEditor extends BpmnDiagramEditor
{

	/** The contributor id for this editor */
	public static final String ID = "jadex.tools.bpmn.editor.JadexBpmnEditorID";


	/** Access the contributor id */
	public String getContributorId()
	{
		return ID;
	}

	/**
	 * Log a exception into eclipse error log
	 * 
	 * @param Exception
	 *            to log
	 * @param int flag from {@link IStatus}
	 */
	public static void log(String message, Exception ex, int iStatus)
	{
		String exMessage = ex != null ? " - " + ex.getMessage() : "";
		BpmnDiagramEditorPlugin
				.getInstance()
				.getLog()
				.log(new Status(iStatus, ID, iStatus, message + exMessage , ex));
	}

}
