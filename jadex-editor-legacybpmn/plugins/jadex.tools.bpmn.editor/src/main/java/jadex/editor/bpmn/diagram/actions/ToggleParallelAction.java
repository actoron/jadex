/**
 * 
 */
package jadex.editor.bpmn.diagram.actions;

import jadex.editor.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;
import jadex.editor.common.model.properties.ModifyEObjectCommand;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.diagram.ui.actions.DiagramAction;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
import org.eclipse.stp.bpmn.SubProcess;
import org.eclipse.stp.bpmn.diagram.edit.parts.SubProcessEditPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Claas
 * 
 */
public class ToggleParallelAction extends DiagramAction
{

	public final static String ID = "jadex.tools.bpmn.commands.toggleParallelActionID"; //$NON-NLS-1$
	public static final String IS_PARALLEL_KEY_ID = "isParallel"; //$NON-NLS-1$

	private boolean setAsParallel;

	/**
	 * @param workbenchPage
	 */
	public ToggleParallelAction(IWorkbenchPage workbenchPage)
	{
		super(workbenchPage);
	}

	/**
	 * @param workbenchpart
	 */
	public ToggleParallelAction(IWorkbenchPart workbenchpart)
	{
		super(workbenchpart);
	}

	@Override
	public void init()
	{
		super.init();
		setId(ID);
		refresh();
	}

	@Override
	public void refresh()
	{
		if (getStructuredSelection().isEmpty()
				|| getStructuredSelection().size() > 1)
		{
			setEnabled(false);
			return;
		}
		if (!(getStructuredSelection().getFirstElement() instanceof SubProcessEditPart))
		{
			setEnabled(false);
			return;
		}
		setEnabled(true);
		SubProcess process = (SubProcess) ((SubProcessEditPart) getStructuredSelection()
				.getFirstElement()).resolveSemanticElement();
		String annotation = EcoreUtil.getAnnotation(process,
				JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION,
				IS_PARALLEL_KEY_ID);
		if (annotation == null || annotation.equals("false")) //$NON-NLS-1$
		{
			setAsParallel = true;
			setText("Set as parallel");
			setToolTipText("Set the SupProcess to parallel execution");
		}
		else
		{
			setAsParallel = false;
			setText("Set as non-parallel");
			setToolTipText("Set the SupProcess to standard execution");
		}
	}

	/**
	 * We override getCommand() instead.
	 * 
	 * @see org.eclipse.gmf.runtime.diagram.ui.actions.DiagramAction#createTargetRequest
	 *      ()
	 * @return null
	 */
	@Override
	protected Request createTargetRequest()
	{
		// Override getCommand instead.
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gmf.runtime.diagram.ui.actions.DiagramAction#isSelectionListener
	 * ()
	 */
	@Override
	protected boolean isSelectionListener()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gmf.runtime.diagram.ui.actions.DiagramAction#getCommand()
	 */
	@Override
	protected Command getCommand()
	{

		final SubProcess model = (SubProcess) ((GraphicalEditPart) (EditPart) getStructuredSelection()
				.getFirstElement()).getPrimaryView().getElement();

		
		ICommand iCommand = new ModifyEObjectCommand(model,
				"refresh property section command")
		{

			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws org.eclipse.core.commands.ExecutionException
			{
				if (setAsParallel)
				{
					EcoreUtil
							.setAnnotation(
									model,
									JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION,
									IS_PARALLEL_KEY_ID, "true"); //$NON-NLS-1$
				}
				else
				{
					EcoreUtil
							.setAnnotation(
									model,
									JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION,
									IS_PARALLEL_KEY_ID, "false"); //$NON-NLS-1$
				}
				((SubProcessEditPart) getStructuredSelection()
						.getFirstElement()).getFigure().repaint();
				return CommandResult.newOKCommandResult();
			}
		};

		return new ICommandProxy(iCommand);
	}

}
