/**
 * 
 */
package jadex.tools.bpmn.editor;

import org.eclipse.stp.bpmn.diagram.part.BpmnDiagramEditor;
import org.eclipse.ui.PlatformUI;

/**
 * This editor extends the BPMN-Diagram editor. 
 * With this extension we have full control about the displayed
 * properties and editor sheets. We also can extend the editor with
 * new sheets and model objects.
 * 
 * @author Claas Altschaffel
 *
 */
public class JadexBpmnEditor extends BpmnDiagramEditor {

	/** The contributor id for this editor */
	public static final String ID = "jadex.tools.bpmn.editor.JadexBpmnEditorID";
	 
    static {
 
        PlatformUI.getWorkbench().getEditorRegistry().
            setDefaultEditor("*.bpmn_diagram", ID); //$NON-NLS-1$
    }
 
    /** Access the contributor id */
    public String getContributorId() {
        return ID;
    }

}
