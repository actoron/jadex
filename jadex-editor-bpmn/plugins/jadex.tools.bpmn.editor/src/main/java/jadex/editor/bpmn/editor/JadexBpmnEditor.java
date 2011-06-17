/**
 * 
 */
package jadex.editor.bpmn.editor;

import jadex.editor.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.stp.bpmn.diagram.edit.parts.BpmnDiagramEditPart;
import org.eclipse.stp.bpmn.diagram.part.BpmnDiagramEditor;
import org.eclipse.stp.bpmn.diagram.part.BpmnDiagramEditorPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * This editor extends the BPMN-Diagram editor. With this extension we have full
 * control about the displayed properties and editor sheets. We also can extend
 * the editor with new edit parts and views.
 */
public class JadexBpmnEditor extends BpmnDiagramEditor
{

	/** The contributor id for this editor */
	public static final String ID = "jadex.editor.bpmn.JadexBpmnEditor";

	/**
	 * The Version of this editor and its annotation style
	 */
	public static final double EDITOR_VERSION = 1.0;

	/**
	 * Empty default constructor
	 */
	public JadexBpmnEditor()
	{
	}
	
	/** Access the contributor id */
	public String getContributorId()
	{
		return ID;
	}

	/**
	 * Log a exception into eclipse error log
	 * @param Exception to log
	 * @param int flag from {@link IStatus}
	 */
	public static void log(String message, Exception ex, int iStatus)
	{
		String exMessage = ex != null ? " - " + ex.getMessage() : "";
		BpmnDiagramEditorPlugin.getInstance().getLog()
			.log(new Status(iStatus, ID, iStatus, message + exMessage, ex));
	}

	public void doSave(IProgressMonitor progressMonitor)
	{
		Object editPart = getDiagramEditPart();
		if (editPart instanceof BpmnDiagramEditPart)
		{
			JadexBpmnPropertiesUtil
					.updateEditorVersionInfo((BpmnDiagramEditPart) editPart);
		}

		super.doSave(progressMonitor);
	}

	protected void initializeGraphicalViewer()
	{
		super.initializeGraphicalViewer();

		convertDiagramProperties();
	}

	private void convertDiagramProperties()
	{
		if (!jadexEAnnotationExist())
		{
			// nothing to convert
			return;
		}

		if (checkDiagramConversion())
		{
			final JadexBpmnEditor myEditor = this;

			Display.getCurrent().asyncExec(new Runnable()
			{
				public void run()
				{
					String title = "Jadex properties conversion";
					String message = "The annotated jadex properties needs a conversion to version 2. Please klick OK to start the conversion";
					boolean confirm = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), title, message);
					if(confirm)
					{
						// convert properties
						Object editPart = getDiagramEditPart();
						if(editPart instanceof BpmnDiagramEditPart)
						{
							JadexBpmnPropertiesUtil.convertDiagramProperties((BpmnDiagramEditPart) editPart);
							// save editor input
							myEditor.doSave(getProgressMonitor());
						}
					}
					else
					{
						// open warning and close editor?
						String errorTitle = "Can't open BPMN diagram";
						String errorMessage = "The version of this diagram is not supported by this editor. Please convert the diagram or re-open it with the default STP BPMN editor. You may also want to change the default editor for *.bpmn_diagram files by changing the option in Jadex BPMN preferences";
						MessageDialog.openError(Display.getCurrent().getActiveShell(), errorTitle, errorMessage);
						// close editor
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(myEditor, false);
					}

				}
			});
		}
	}

	/**
	 * 
	 */
	private boolean jadexEAnnotationExist()
	{
		boolean eAnnotationExists = false;

		Object editPart = getDiagramEditPart();
		if(editPart instanceof BpmnDiagramEditPart)
		{
			final EObject eObject = ((IGraphicalEditPart)editPart).resolveSemanticElement();
			if(eObject instanceof EModelElement)
			{
				EAnnotation jadexEAnnotation = JadexBpmnPropertiesUtil.getJadexEAnnotation((EModelElement)eObject,
					JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION, false);
				eAnnotationExists = (jadexEAnnotation != null);
			}
		}
		return eAnnotationExists;
	}

	/**
	 * Checks the diagram version
	 */
	private boolean checkDiagramConversion()
	{
		Object editPart = getDiagramEditPart();

		if(editPart instanceof BpmnDiagramEditPart)
		{
			final EObject eObject = ((IGraphicalEditPart)editPart).resolveSemanticElement();
			if(eObject instanceof EModelElement)
			{
				String diagramVersion = JadexBpmnPropertiesUtil.getJadexEAnnotationDetail((EModelElement)eObject,
					JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION, JadexBpmnPropertiesUtil.JADEX_PROPERTIES_VERSION_DETAIL);
				if(diagramVersion == null || diagramVersion.isEmpty() || (new Double(EDITOR_VERSION).compareTo(Double.valueOf(diagramVersion))) < 0)
				{
					return true;
				}
			}
		}

		return false;
	}

}
