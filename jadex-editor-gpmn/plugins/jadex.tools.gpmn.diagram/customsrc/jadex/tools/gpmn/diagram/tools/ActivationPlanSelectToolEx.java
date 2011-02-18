package jadex.tools.gpmn.diagram.tools;

import jadex.tools.gpmn.diagram.edit.parts.ActivationEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.ActivationPlanEditPart;
import jadex.tools.gpmn.diagram.edit.parts.PlanEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.VirtualActivationEdgeEditPart;
import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;
import jadex.tools.gpmn.diagram.ui.figures.ActivationPlanFigure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewAndElementRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequestFactory;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewRequest.ConnectionViewDescriptor;
import org.eclipse.gmf.runtime.diagram.ui.tools.DragEditPartsTrackerEx;
import org.eclipse.gmf.runtime.diagram.ui.type.DiagramNotationType;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.View;

public class ActivationPlanSelectToolEx extends DragEditPartsTrackerEx
{
	public ActivationPlanSelectToolEx(EditPart owner)
	{
		super(owner);
	}
	
	@Override
	protected boolean handleButtonDown(int button)
	{
		PrecisionPoint loc = new PrecisionPoint(getLocation());
		
		Rectangle bounds = getPlanFigure().getHideBounds();
		getPlanFigure().translateToAbsolute(bounds);
		
		// TODO: This is definitely incorrect but SWT.BUTTON1 is the wrong mask.
		if ((button & 1) != 0)
		{
			if (bounds.contains(loc))
			{
				getPlan().getDiagramEditDomain().getDiagramCommandStack().execute(getHideCommand(getPlan()));
				return true;
			}
		}
		
		return super.handleButtonDown(button);
	}
	
	public static Command getHideCommand(final ActivationPlanEditPart planPart)
	{
		if (planPart.getSourceConnections().size() == 0 || planPart.getTargetConnections().size() == 0)
			return null;
		
		final DiagramEditPart diagramEditPart = (DiagramEditPart) planPart.getParent();
		
		final ArrayList<CreateConnectionViewRequest> virtEdges = new ArrayList<CreateConnectionViewRequest>();		
		Command cmd = new ICommandProxy(new AbstractTransactionalCommand((TransactionalEditingDomain) AdapterFactoryEditingDomain.getEditingDomainFor(diagramEditPart.resolveSemanticElement()),
				"Create Virtual Edges.",
				Arrays.asList(new Object[] {WorkspaceSynchronizer.getFile(diagramEditPart.resolveSemanticElement().eResource())}))
		{
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
			{
				
				for (Object actConn : planPart.getSourceConnections())
				{
					ActivationEdgeEditPart actEdge = (ActivationEdgeEditPart) actConn;
					for (Object planConn : planPart.getTargetConnections())
					{
						PlanEdgeEditPart planEdge = (PlanEdgeEditPart) planConn;
						CreateConnectionViewRequest req = CreateViewRequestFactory.getCreateConnectionRequest(GpmnElementTypes.Link_4003, diagramEditPart.getDiagramPreferencesHint());
						CreateConnectionViewAndElementRequest.getCreateCommand(req, planEdge.getSource(), actEdge.getTarget()).execute();
						virtEdges.add(req);
					}
				}
				
				return CommandResult.newOKCommandResult();
			}
			
			@Override
			public boolean canExecute()
			{
				return (planPart.getSourceConnections().size() > 0 && planPart.getTargetConnections().size() > 0);
			}
		});
		
		CompoundCommand cc = new CompoundCommand();
		cc.add(cmd);
		
		final List<VirtualActivationEdgeEditPart> virtParts = new ArrayList<VirtualActivationEdgeEditPart>();
		cmd = new ICommandProxy(new AbstractTransactionalCommand((TransactionalEditingDomain) AdapterFactoryEditingDomain.getEditingDomainFor(diagramEditPart.resolveSemanticElement()),
				"Hide Activation Plan.",
				Arrays.asList(new Object[] {WorkspaceSynchronizer.getFile(diagramEditPart.resolveSemanticElement().eResource())}))
		{
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
			{
				Set<View> virtSet = new HashSet<View>();
				for (CreateConnectionViewRequest request : virtEdges)
					virtSet.add((View) ((ConnectionViewDescriptor) request.getNewObject()).getAdapter(View.class));
				
				for (Object connPart : diagramEditPart.getConnections())
					if (connPart instanceof VirtualActivationEdgeEditPart && virtSet.contains(((VirtualActivationEdgeEditPart) connPart).getPrimaryView()))
						virtParts.add((VirtualActivationEdgeEditPart) connPart);
				
				virtSet = null;
				
				Map<EditPart, ActivationEdgeEditPart> aEdges = new HashMap<EditPart, ActivationEdgeEditPart>();
				for (Object oEdge : planPart.getSourceConnections())
					if (oEdge instanceof ActivationEdgeEditPart)
						aEdges.put(((ActivationEdgeEditPart) oEdge).getTarget(), (ActivationEdgeEditPart) oEdge);
				
				for (VirtualActivationEdgeEditPart vaePart : virtParts)
				{
					CreateConnectionViewRequest req = CreateViewRequestFactory.getCreateConnectionRequest(DiagramNotationType.NOTE_ATTACHMENT, diagramEditPart.getDiagramPreferencesHint());
					CreateConnectionViewAndElementRequest.getCreateCommand(req, aEdges.get(vaePart.getTarget()), vaePart).execute();
				}
				
				planPart.getPrimaryView().setVisible(false);
				
				return CommandResult.newOKCommandResult();
			}
			
			@Override
			public boolean canExecute()
			{
				return (planPart.getSourceConnections().size() > 0 && planPart.getTargetConnections().size() > 0);
			}
		});
		cc.add(cmd);
		
		cmd = new ICommandProxy(new AbstractTransactionalCommand((TransactionalEditingDomain) AdapterFactoryEditingDomain.getEditingDomainFor(diagramEditPart.resolveSemanticElement()),
				"Hide Activation Plan.",
				Arrays.asList(new Object[] {WorkspaceSynchronizer.getFile(diagramEditPart.resolveSemanticElement().eResource())}))
		{
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
			{
				for (VirtualActivationEdgeEditPart vaePart : virtParts)
				{
					((Edge) vaePart.getNotationView().getTargetEdges().get(0)).setVisible(false);
					((EditPart) vaePart.getTargetConnections().get(0)).refresh();
					((EditPart) vaePart.getChildren().get(0)).refresh();
				}
				
				return CommandResult.newOKCommandResult();
			}
			
			@Override
			public boolean canExecute()
			{
				return (planPart.getSourceConnections().size() > 0 && planPart.getTargetConnections().size() > 0);
			}
		});
		cc.add(cmd);
		
		return cc;
	}
	
	private ActivationPlanEditPart getPlan()
	{
		return (ActivationPlanEditPart) getSourceEditPart();
	}
	
	private ActivationPlanFigure getPlanFigure()
	{
		return (ActivationPlanFigure) getPlan().getFigure().getChildren().get(0);
	}
}
