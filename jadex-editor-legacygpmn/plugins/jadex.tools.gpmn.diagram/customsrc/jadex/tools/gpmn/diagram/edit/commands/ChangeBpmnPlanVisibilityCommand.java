package jadex.tools.gpmn.diagram.edit.commands;

import java.util.Arrays;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.notation.Node;

public class ChangeBpmnPlanVisibilityCommand extends AbstractTransactionalCommand
{
	private Node bpmnPlan;
	private DiagramEditPart diagramEditPart;
	private boolean visibility;
	
	public ChangeBpmnPlanVisibilityCommand(DiagramEditPart diagramEditPart, Node bpmnPlan, boolean visibility)
	{
		super((TransactionalEditingDomain) AdapterFactoryEditingDomain.getEditingDomainFor(diagramEditPart.getDiagramView()),
				"Modify Edge Order.",
				Arrays.asList(new Object[] {WorkspaceSynchronizer.getFile(diagramEditPart.getDiagramView().eResource())}));
		
		this.bpmnPlan = bpmnPlan;
		this.diagramEditPart = diagramEditPart;
		this.visibility = visibility;
	}
	
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException
	{
		bpmnPlan.setVisible(visibility);
		diagramEditPart.refresh();
		
		return CommandResult.newOKCommandResult();
	}
}
