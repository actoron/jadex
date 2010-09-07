package jadex.tools.gpmn.diagram.edit.policies;

import jadex.tools.gpmn.diagram.edit.parts.ActivationPlanEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.VirtualActivationEdgeEditPart;
import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;
import jadex.tools.gpmn.diagram.tools.SGpmnUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.gef.handles.ConnectionEndpointHandle;
import org.eclipse.gef.tools.ConnectionEndpointTracker;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewAndElementRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequestFactory;
import org.eclipse.gmf.runtime.diagram.ui.requests.RequestConstants;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewAndElementRequest.ViewAndElementDescriptor;
import org.eclipse.gmf.runtime.diagram.ui.type.DiagramNotationType;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.emf.type.core.IHintedType;
import org.eclipse.gmf.runtime.emf.type.core.commands.DestroyElementCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.Location;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.View;

public class VirtualActivationEdgeEndPointsPolicy extends ConnectionEndpointEditPolicy
{
	@Override
	protected List<?> createSelectionHandles()
	{
		//List handles = super.createSelectionHandles();
		
		List<ConnectionEndpointHandle> handles = new ArrayList<ConnectionEndpointHandle>();
		
		handles.add(new ConnectionEndpointHandle(getEdge(), false, ConnectionLocator.SOURCE)
		{
			@Override
			protected DragTracker createDragTracker()
			{
				return new SourcePointTracker(getEdge());
			}
		});
		
		handles.add(new ConnectionEndpointHandle(getEdge(), false, ConnectionLocator.TARGET)
		{
			@Override
			protected DragTracker createDragTracker()
			{
				return new TargetPointTracker(getEdge());
			}
		});
		
		for (Object h : handles)
		{
			ConnectionEndpointHandle ceh = ((ConnectionEndpointHandle) h);
		}
		return handles;
	}
	
	private VirtualActivationEdgeEditPart getEdge()
	{
		return (VirtualActivationEdgeEditPart) getHost();
	}
	
	private static class SourcePointTracker extends ConnectionEndpointTracker
	{
		public SourcePointTracker(ConnectionEditPart cep)
		{
			super(cep);
			setCommandName(RequestConstants.REQ_RECONNECT_SOURCE);
		}
		
		@Override
		protected Command getCommand()
		{
			DiagramEditPart diagramEditPart = (DiagramEditPart) getConnectionEditPart().getRoot().getContents();
			
			Command cmd = new ICommandProxy(new AbstractTransactionalCommand((TransactionalEditingDomain) AdapterFactoryEditingDomain.getEditingDomainFor(diagramEditPart.resolveSemanticElement()),
					"Reconnect Virtual Edge Source.",
					Arrays.asList(new Object[] {WorkspaceSynchronizer.getFile(diagramEditPart.resolveSemanticElement().eResource())}))
			{
				@Override
				protected CommandResult doExecuteWithResult(
						IProgressMonitor monitor, IAdaptable info)
						throws ExecutionException
				{
					Edge virtEdge = (Edge) ((VirtualActivationEdgeEditPart) getConnectionEditPart()).getNotationView();
					Node sourcePlanNode = (Node) ((Edge) virtEdge.getTargetEdges().get(0)).getSource();
					DiagramEditPart diagramEditPart = (DiagramEditPart) getTargetEditPart().getRoot().getContents();
					GoalEditPart targetEditPart = (GoalEditPart) getTargetEditPart();
					
					if (sourcePlanNode.getTargetEdges().size() == 1)
					{
						if (SGpmnUtilities.typeFilterViewList((IHintedType) GpmnElementTypes.ActivationEdge_4001, sourcePlanNode.getSourceEdges()).size() == 1)
						{
							Edge[] edges = (Edge[]) sourcePlanNode.getSourceEdges().toArray(new Edge[0]);
							for (int i = 0; i < edges.length; ++i)
								SGpmnUtilities.destroy(edges[i]);
							edges = (Edge[]) sourcePlanNode.getTargetEdges().toArray(new Edge[0]);
							for (int i = 0; i < edges.length; ++i)
								SGpmnUtilities.destroy(edges[i]);
							SGpmnUtilities.destroy(sourcePlanNode);
						}
						else
						{
							Edge[] edges = (Edge[]) sourcePlanNode.getSourceEdges().toArray(new Edge[0]);
							for (int i = 0; i < edges.length; ++i)
								if (edges[i].getTarget().equals(virtEdge.getTarget()))
								{
									SGpmnUtilities.destroy(edges[i]);
									break;
								}
							SGpmnUtilities.destroy((View) virtEdge.getTargetEdges().get(0));
						}
					}
					else
					{
						if (SGpmnUtilities.typeFilterViewList((IHintedType) GpmnElementTypes.ActivationEdge_4001, sourcePlanNode.getSourceEdges()).size() != 1)
						{
							ActivationPlanEditPart newPlanPart = SGpmnUtilities.splitActivationPlan(
									(Node) virtEdge.getSource(),
									sourcePlanNode,
									SGpmnUtilities.resolveEditPart(diagramEditPart, virtEdge.getTarget()),
									null,
									diagramEditPart);
							
							Edge[] edges = (Edge[]) sourcePlanNode.getSourceEdges().toArray(new Edge[0]);
							for (int i = 0; i < edges.length; ++i)
								if (edges[i].getType().equals(DiagramNotationType.NOTE_ATTACHMENT.getSemanticHint()) &&
										edges[i].getTarget() instanceof Edge&&
										((Edge) edges[i].getTarget()).getSource().equals(virtEdge.getSource()))
									edges[i].setSource(newPlanPart.getNotationView());
						}
						
						SGpmnUtilities.destroy((View) virtEdge.getTargetEdges().get(0));
						SGpmnUtilities.destroyEdge(virtEdge.getSource(), sourcePlanNode);
						
						//newPlanPart.getNotationView().setVisible(false);
					}
					
					//Locate the first activation plan, if any
					Node targetPlanNode = locateTargetPlanNode();
					if (targetPlanNode == null)
					{
						CreateViewRequest apReq = CreateViewRequestFactory.getCreateShapeRequest(GpmnElementTypes.ActivationPlan_2001, diagramEditPart.getDiagramPreferencesHint());
						diagramEditPart.getCommand(apReq).execute();
						diagramEditPart.refresh();
						ActivationPlanEditPart newPlanPart = (ActivationPlanEditPart) SGpmnUtilities.resolveEditPart(diagramEditPart, ((ViewAndElementDescriptor) ((List) apReq.getNewObject()).get(0)).getAdapter(Node.class));
						diagramEditPart.getViewer().reveal(newPlanPart);
						targetPlanNode = (Node) newPlanPart.getNotationView();
						PrecisionPoint p = SGpmnUtilities.getCenter(new Node[] {(Node) targetEditPart.getNotationView(), (Node) virtEdge.getTarget()});
						((Location) targetPlanNode.getLayoutConstraint()).setX((int)Math.round(p.preciseX));
						((Location) targetPlanNode.getLayoutConstraint()).setY((int)Math.round(p.preciseY));
						
						CreateConnectionViewRequest req = CreateViewRequestFactory.getCreateConnectionRequest(GpmnElementTypes.ActivationEdge_4001, diagramEditPart.getDiagramPreferencesHint());
						CreateConnectionViewAndElementRequest.getCreateCommand(req, newPlanPart, SGpmnUtilities.resolveEditPart(diagramEditPart, virtEdge.getTarget())).execute();
						newPlanPart.refresh();
						req = CreateViewRequestFactory.getCreateConnectionRequest(GpmnElementTypes.PlanEdge_4002, diagramEditPart.getDiagramPreferencesHint());
						CreateConnectionViewAndElementRequest.getCreateCommand(req, targetEditPart, newPlanPart).execute();
						targetEditPart.refresh();
						req = CreateViewRequestFactory.getCreateConnectionRequest(DiagramNotationType.NOTE_ATTACHMENT, diagramEditPart.getDiagramPreferencesHint());
						CreateConnectionViewAndElementRequest.getCreateCommand(req, newPlanPart, SGpmnUtilities.resolveEditPart(diagramEditPart, virtEdge)).execute();
						newPlanPart.refresh();
						
						((Edge) virtEdge.getTargetEdges().get(0)).setSource(targetPlanNode);
						
						targetPlanNode.setVisible(false);
					}
					else
					{
						if (targetPlanNode.getTargetEdges().size() == 1)
						{
							boolean visible = targetPlanNode.isVisible();
							targetPlanNode.setVisible(true);
							diagramEditPart.refresh();
							EditPart targetPlanPart = SGpmnUtilities.resolveEditPart(diagramEditPart, targetPlanNode);
							CreateConnectionViewRequest req = CreateViewRequestFactory.getCreateConnectionRequest(GpmnElementTypes.ActivationEdge_4001, diagramEditPart.getDiagramPreferencesHint());
							CreateConnectionViewAndElementRequest.getCreateCommand(req, targetPlanPart, SGpmnUtilities.resolveEditPart(diagramEditPart, virtEdge.getTarget())).execute();
							if (!visible)
							{
								req = CreateViewRequestFactory.getCreateConnectionRequest(DiagramNotationType.NOTE_ATTACHMENT, diagramEditPart.getDiagramPreferencesHint());
								CreateConnectionViewAndElementRequest.getCreateCommand(req, targetPlanPart, SGpmnUtilities.resolveEditPart(diagramEditPart, virtEdge)).execute();
							}
							else
							{
								SGpmnUtilities.destroy(virtEdge);
								virtEdge = null;
							}
							targetPlanNode.setVisible(visible);
						}
						else
						{
							ActivationPlanEditPart newPlanPart = SGpmnUtilities.splitActivationPlan(
									(Node) targetEditPart.getNotationView(),
									targetPlanNode,
									null,
									null,
									diagramEditPart);
							diagramEditPart.getViewer().reveal(newPlanPart);
							
							CreateConnectionViewRequest req = CreateViewRequestFactory.getCreateConnectionRequest(GpmnElementTypes.ActivationEdge_4001, diagramEditPart.getDiagramPreferencesHint());
							CreateConnectionViewAndElementRequest.getCreateCommand(req, newPlanPart, SGpmnUtilities.resolveEditPart(diagramEditPart, virtEdge.getTarget())).execute();
							
							if (!targetPlanNode.isVisible())
							{
								diagramEditPart.refresh();
								Edge[] sEdges = (Edge[]) targetEditPart.getNotationView().getSourceEdges().toArray(new Edge[0]);
								for (int i = 0; i < sEdges.length; ++i)
								{
									if (sEdges[i].getType().equals(((IHintedType) GpmnElementTypes.Link_4003).getSemanticHint()) &&
										((Edge) sEdges[i].getTargetEdges().get(0)).getSource().equals(targetPlanNode))
									{
										SGpmnUtilities.destroy((Edge) sEdges[i].getTargetEdges().get(0));
										req = CreateViewRequestFactory.getCreateConnectionRequest(DiagramNotationType.NOTE_ATTACHMENT, diagramEditPart.getDiagramPreferencesHint());
										CreateConnectionViewAndElementRequest.getCreateCommand(req, newPlanPart, SGpmnUtilities.resolveEditPart(diagramEditPart, sEdges[i])).execute();
									}
								}
								/*Edge[] nEdges = (Edge[]) targetPlanNode.getSourceEdges().toArray(new Edge[0]);
								for (int i = 0; i < nEdges.length; ++i)
								{
									if (nEdges[i].getType().equals(DiagramNotationType.NOTE_ATTACHMENT.getSemanticHint()) &&
											((Edge) nEdges[i].getTarget()).getSource().equals(targetEditPart.getNotationView()))
										nEdges[i].setSource(newPlanPart.getNotationView());
								}*/
								
								
								req = CreateViewRequestFactory.getCreateConnectionRequest(DiagramNotationType.NOTE_ATTACHMENT, diagramEditPart.getDiagramPreferencesHint());
								CreateConnectionViewAndElementRequest.getCreateCommand(req, newPlanPart, SGpmnUtilities.resolveEditPart(diagramEditPart, virtEdge)).execute();
								//newPlanPart.getNotationView().setVisible(false);
							}
							else
							{
								SGpmnUtilities.destroy(virtEdge);
								virtEdge = null;
							}
						}
					}
					
					if (virtEdge != null)
						virtEdge.setSource(targetEditPart.getNotationView());
					
					
					return CommandResult.newOKCommandResult();
				}
				
				@Override
				public boolean canExecute()
				{
					return (getTargetEditPart() instanceof GoalEditPart && 
							!getConnectionEditPart().getSource().equals(getTargetEditPart()) &&
							!SGpmnUtilities.hasConnection(locateTargetPlanNode(), ((GoalEditPart) getTargetEditPart()).getNotationView()));
				}
				
				private Node locateTargetPlanNode()
				{
					//Locate the first activation plan, if any
					Node targetPlanNode = null;
					GoalEditPart targetPart = (GoalEditPart) getTargetEditPart();
					for (Object conn : targetPart.getNotationView().getSourceEdges())
						if (((Edge) conn).getType().equals(((IHintedType) GpmnElementTypes.PlanEdge_4002).getSemanticHint()) &&
							((Edge) conn).getTarget().getType().equals(((IHintedType) GpmnElementTypes.ActivationPlan_2001).getSemanticHint()))
							targetPlanNode = (Node) ((Edge) conn).getTarget();
					return targetPlanNode;
				}
			});
			
			return cmd;
		}
	}
	
	private static class TargetPointTracker extends ConnectionEndpointTracker
	{
		public TargetPointTracker(ConnectionEditPart cep)
		{
			super(cep);
			setCommandName(RequestConstants.REQ_RECONNECT_TARGET);
		}
		
		@Override
		protected Command getCommand()
		{
			DiagramEditPart diagramEditPart = (DiagramEditPart) getConnectionEditPart().getRoot().getContents();
			
			Command cmd = new ICommandProxy(new AbstractTransactionalCommand((TransactionalEditingDomain) AdapterFactoryEditingDomain.getEditingDomainFor(diagramEditPart.resolveSemanticElement()),
					"Reconnect Virtual Edge Target.",
					Arrays.asList(new Object[] {WorkspaceSynchronizer.getFile(diagramEditPart.resolveSemanticElement().eResource())}))
			{
				@Override
				protected CommandResult doExecuteWithResult(
						IProgressMonitor monitor, IAdaptable info)
						throws ExecutionException
				{
					Edge virtEdge = (Edge) ((VirtualActivationEdgeEditPart) getConnectionEditPart()).getNotationView();
					Node planNode = (Node) ((Edge) virtEdge.getTargetEdges().get(0)).getSource();
					
					if (planNode.getTargetEdges().size() == 1)
					{
						Edge aEdge = null;
						for (Iterator it = planNode.getSourceEdges().iterator(); aEdge == null && it.hasNext(); )
						{
							Edge edge = (Edge) it.next();
							if (edge.getTarget().equals(virtEdge.getTarget()))
								aEdge = edge;
						}
						
						View target = ((GoalEditPart) getTargetEditPart()).getNotationView();
						
						aEdge.setTarget(target);
						virtEdge.setTarget(target);
					}
					else
					{
						DiagramEditPart diagramEditPart = (DiagramEditPart) getConnectionEditPart().getRoot().getContents();
						
						//Node newPlanNode = (Node) ((ViewAndElementDescriptor) ((List) req.getNewObject()).get(0)).getAdapter(Node.class);
						//ActivationPlan newPlan = (ActivationPlan) newPlanNode.getElement();
						
						ActivationPlanEditPart newPlanPart = SGpmnUtilities.splitActivationPlan(
								(Node) virtEdge.getSource(),
								planNode,
								SGpmnUtilities.resolveEditPart(diagramEditPart, virtEdge.getTarget()),
								getTargetEditPart(),
								diagramEditPart);
						
						//Attach virtual edges to new plan
						Edge[] edges = (Edge[]) planNode.getSourceEdges().toArray(new Edge[0]);
						for (int i = 0; i < edges.length; ++i)
							if (DiagramNotationType.NOTE_ATTACHMENT.getSemanticHint().equals(edges[i].getType()) &&
								((Edge) edges[i].getTarget()).getSource().equals(virtEdge.getSource()))
								edges[i].setSource(newPlanPart.getNotationView());
						
						virtEdge.setTarget(((IGraphicalEditPart) getTargetEditPart()).getNotationView());
						
						newPlanPart.getNotationView().setVisible(false);
					}
					
					return CommandResult.newOKCommandResult();
				}
				
				@Override
				public boolean canExecute()
				{
					return getTargetEditPart() instanceof GoalEditPart && !getConnectionEditPart().getTarget().equals(getTargetEditPart());
				}
			});
			
			return cmd;
		}
	}
}
