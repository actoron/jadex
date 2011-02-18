package jadex.tools.gpmn.diagram.tools;

import jadex.tools.gpmn.ActivationPlan;
import jadex.tools.gpmn.diagram.edit.parts.ActivationPlanEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SubProcessEditPart;
import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.tools.ConnectionDragCreationTool;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.diagram.core.edithelpers.CreateElementRequestAdapter;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewAndElementRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequestFactory;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewAndElementRequest.ViewAndElementDescriptor;
import org.eclipse.gmf.runtime.diagram.ui.type.DiagramNotationType;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.emf.type.core.IHintedType;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.Location;
import org.eclipse.gmf.runtime.notation.Node;

public class ActivationEdgeCreationTool extends ConnectionDragCreationTool
{
	@Override
	protected Command getCommand()
	{
		if (getSourceRequest() instanceof CreateConnectionRequest)
		{
			final CreateConnectionRequest req = (CreateConnectionRequest) getSourceRequest();
			
			EditPart src = null;
			if (isInState(STATE_CONNECTION_STARTED))
				src = req.getSourceEditPart();
			else
				src = getTargetEditPart();
			
			if (src instanceof GoalEditPart && 
				(req.getTargetEditPart() == null || req.getTargetEditPart() instanceof ShapeEditPart))
				return createGoalConnectionCommand((GoalEditPart) src, (ShapeEditPart) req.getTargetEditPart());
			
			if (src instanceof ActivationPlanEditPart)
				return createActivationPlanConnectionCommand((ActivationPlanEditPart) src, req.getTargetEditPart());
		}
		return null;
	}
	
	protected static Command createGoalConnectionCommand(final GoalEditPart goalsource, final ShapeEditPart target)
	{
		DiagramEditPart dPart = null;
		if (goalsource != null)
			dPart = (DiagramEditPart) goalsource.getParent();
		else
			dPart = (DiagramEditPart) target.getParent();
		final DiagramEditPart diagramEditPart = dPart;
		
		//EditPart part = null;
		/*Iterator sourceedgeit = goalsource.getSourceConnections().iterator();
		while (sourceedgeit.hasNext() && part == null)
		{
			Object conn = sourceedgeit.next();
			if (conn instanceof PlanEdgeEditPart)
			{
				PlanEdgeEditPart edge = (PlanEdgeEditPart) conn;
				if (edge.getTarget() instanceof ActivationPlanEditPart)
					part = edge.getTarget();
			}
		}*/
		
		Node tmpNode = null;
		Iterator sourceedgeit = goalsource.getNotationView().getSourceEdges().iterator();
		while (sourceedgeit.hasNext() && tmpNode == null)
		{
			Edge edge = (Edge) sourceedgeit.next();
			if (edge.getTarget().getType().equals(((IHintedType) GpmnElementTypes.ActivationPlan_2001).getSemanticHint()))
				tmpNode = (Node) edge.getTarget();
		}
		final Node activationPlanNode = tmpNode;
		
		Command cmd = null;
		if (activationPlanNode == null)
		{
			ICommand command = new AbstractTransactionalCommand((TransactionalEditingDomain) AdapterFactoryEditingDomain.getEditingDomainFor(diagramEditPart.resolveSemanticElement()),
					"Add ActivationPlan and connect.",
					Arrays.asList(new Object[] {WorkspaceSynchronizer.getFile(diagramEditPart.resolveSemanticElement().eResource())}))
			{
				@Override
				protected CommandResult doExecuteWithResult(
						IProgressMonitor monitor, IAdaptable info)
						throws ExecutionException
				{
					CreateViewRequest req = CreateViewRequestFactory.getCreateShapeRequest(GpmnElementTypes.ActivationPlan_2001, diagramEditPart.getDiagramPreferencesHint());
					diagramEditPart.getCommand(req).execute();
					diagramEditPart.refresh();
					ActivationPlan plan = (ActivationPlan) (((CreateElementRequestAdapter) ((ViewAndElementDescriptor) ((List) req.getNewObject()).get(0)).getElementAdapter()).resolve());
					
					ActivationPlanEditPart planpart = (ActivationPlanEditPart) diagramEditPart.findEditPart(diagramEditPart, plan);
					Point srcloc = goalsource.getLocation();
					Point tgtloc = target.getLocation();
					((Location) ((Node) planpart.getModel()).getLayoutConstraint()).setX((srcloc.x + tgtloc.x) / 2);
					((Location) ((Node) planpart.getModel()).getLayoutConstraint()).setY((srcloc.y + tgtloc.y) / 2);
					CreateConnectionViewRequest conreq = CreateViewRequestFactory.getCreateConnectionRequest(GpmnElementTypes.PlanEdge_4002, diagramEditPart.getDiagramPreferencesHint());
					CreateConnectionViewAndElementRequest.getCreateCommand(conreq, goalsource, planpart).execute();
					
					conreq = CreateViewRequestFactory.getCreateConnectionRequest(GpmnElementTypes.ActivationEdge_4001, diagramEditPart.getDiagramPreferencesHint());
					CreateConnectionViewAndElementRequest.getCreateCommand(conreq, planpart, target).execute();
					
					return null;
				}
				
				@Override
				public boolean canExecute()
				{
					return goalsource != null && (target instanceof GoalEditPart || target instanceof SubProcessEditPart);
				}
			};
			cmd = new ICommandProxy(command);
		}
		else
		{
			if (activationPlanNode.isVisible())
			{
				CreateConnectionViewRequest req = CreateViewRequestFactory.getCreateConnectionRequest(GpmnElementTypes.ActivationEdge_4001, diagramEditPart.getDiagramPreferencesHint());
				cmd = CreateConnectionViewAndElementRequest.getCreateCommand(req, SGpmnUtilities.resolveEditPart(diagramEditPart, activationPlanNode), target);
			}
			else
			{
				cmd = (new ICommandProxy(new AbstractTransactionalCommand((TransactionalEditingDomain) AdapterFactoryEditingDomain.getEditingDomainFor(diagramEditPart.resolveSemanticElement()),
						"Add ActivationEdge and VirtualEdge.",
						Arrays.asList(new Object[] {WorkspaceSynchronizer.getFile(diagramEditPart.resolveSemanticElement().eResource())}))
				{
					@Override
					protected CommandResult doExecuteWithResult(
							IProgressMonitor monitor, IAdaptable info)
							throws ExecutionException
					{
						Node apNode = null;
						if (SGpmnUtilities.getPlanEdgeCount(activationPlanNode) > 1)
							apNode = (Node) SGpmnUtilities.splitActivationPlan((Node) goalsource.getNotationView(), activationPlanNode, diagramEditPart).getNotationView();
						else
							apNode = activationPlanNode;
						
						apNode.setVisible(true);
						diagramEditPart.refresh();
						
						ActivationPlanEditPart apPart = (ActivationPlanEditPart) SGpmnUtilities.resolveEditPart(diagramEditPart, apNode);
						CreateConnectionViewRequest req = CreateViewRequestFactory.getCreateConnectionRequest(GpmnElementTypes.ActivationEdge_4001, diagramEditPart.getDiagramPreferencesHint());
						CreateConnectionViewAndElementRequest.getCreateCommand(req, apPart, target).execute();
						apPart.refresh();
						EditPart aePart = SGpmnUtilities.unwrap(req);
						
						req = CreateViewRequestFactory.getCreateConnectionRequest(GpmnElementTypes.Link_4003, diagramEditPart.getDiagramPreferencesHint());
						CreateConnectionViewAndElementRequest.getCreateCommand(req, goalsource, target).execute();
						goalsource.refresh();
						EditPart vPart = SGpmnUtilities.unwrap(req);
						req = CreateViewRequestFactory.getCreateConnectionRequest(DiagramNotationType.NOTE_ATTACHMENT, diagramEditPart.getDiagramPreferencesHint());
						CreateConnectionViewAndElementRequest.getCreateCommand(req, aePart, vPart).execute();
						vPart.refresh();
						
						apNode.setVisible(false);
						
						return CommandResult.newOKCommandResult();
					}
					
					@Override
					public boolean canExecute()
					{
						return (!SGpmnUtilities.hasConnection(activationPlanNode, target.getNotationView()));
					}
				}));
				
				CompoundCommand cc = new CompoundCommand();
				cc.add(cmd);
				
				cmd = cc;
			}
		}
		
		return cmd;
	}
	
	protected static Command createActivationPlanConnectionCommand(final ActivationPlanEditPart source, final EditPart target)
	{
		DiagramEditPart dPart = null;
		if (source != null)
			dPart = (DiagramEditPart) source.getParent();
		else
			dPart = (DiagramEditPart) target.getParent();
		final DiagramEditPart diagramEditPart = dPart;
		
		Command cmd = new ICommandProxy(new AbstractTransactionalCommand((TransactionalEditingDomain) AdapterFactoryEditingDomain.getEditingDomainFor(diagramEditPart.resolveSemanticElement()),
				"ActivationPlan connect.",
				Arrays.asList(new Object[] {WorkspaceSynchronizer.getFile(diagramEditPart.resolveSemanticElement().eResource())}))
		{
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
			{
				CreateConnectionViewRequest req = CreateViewRequestFactory.getCreateConnectionRequest(GpmnElementTypes.ActivationEdge_4001, diagramEditPart.getDiagramPreferencesHint());
				CreateConnectionViewAndElementRequest.getCreateCommand(req, source, target).execute();
				return CommandResult.newOKCommandResult();
			}
			
			@Override
			public boolean canExecute()
			{
				CreateConnectionViewRequest req = CreateViewRequestFactory.getCreateConnectionRequest(GpmnElementTypes.ActivationEdge_4001, diagramEditPart.getDiagramPreferencesHint());
				return CreateConnectionViewAndElementRequest.getCreateCommand(req, source, target) != null;
			}
		});
		
		return cmd;
	}
}
