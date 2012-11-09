/**
 * 
 */
package jadex.editor.bpmn.diagram.actions;

import jadex.editor.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.stp.bpmn.diagram.edit.parts.BpmnDiagramEditPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Claas
 *
 */
public class ConvertDiagramPropertiesAction implements IObjectActionDelegate
{

	public final static String ID = "jadex.tools.bpmn.diagram.actions.ConvertDiagramPropertiesActionID";
	

	@SuppressWarnings("unused")
	private IWorkbenchPart targetPart;
	private EditPart diagramEditPart;
	
	
	/**
	 * Empty default constructor
	 */
	public ConvertDiagramPropertiesAction()
	{
		super();
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection)
	{
		diagramEditPart = null;
		if (selection instanceof IStructuredSelection)
		{
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			if (structuredSelection.getFirstElement() instanceof EditPart)
			{
				EditPart ep = (EditPart) structuredSelection
					.getFirstElement();
				
				while (ep.getParent() != null && !(ep instanceof BpmnDiagramEditPart))
				{
					ep = ep.getParent();
				}
				diagramEditPart = ep;
			}
		}
	}

	/**
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) 
	{
		this.targetPart = targetPart;
	}

	@Override
	public void run(IAction action)
	{
		if (diagramEditPart instanceof BpmnDiagramEditPart)
		{
			JadexBpmnPropertiesUtil.convertDiagramProperties(
					(BpmnDiagramEditPart) diagramEditPart);
		}
	}

	
	
}


