package jadex.tools.gpmn.diagram.tools;

import jadex.tools.gpmn.AbstractEdge;
import jadex.tools.gpmn.AbstractNode;
import jadex.tools.gpmn.ActivationEdge;
import jadex.tools.gpmn.ActivationPlan;
import jadex.tools.gpmn.Goal;
import jadex.tools.gpmn.PlanEdge;
import jadex.tools.gpmn.diagram.edit.parts.ActivationPlanEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GoalEditPart;
import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewAndElementRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequestFactory;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewRequest.ConnectionViewDescriptor;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewAndElementRequest.ViewAndElementDescriptor;
import org.eclipse.gmf.runtime.diagram.ui.type.DiagramNotationType;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.type.core.IHintedType;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.Location;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.View;

public final class SGpmnUtilities
{
	
	/**
	 *  Splits an activation plan and reattaches the copy to the provided goal, disconnection the old plan edge.
	 */
	public static final ActivationPlanEditPart splitActivationPlan(Node sourceGoalNode, Node sourcePlanNode, DiagramEditPart diagramEditPart)
	{
		return splitActivationPlan(sourceGoalNode, sourcePlanNode, null, null, diagramEditPart);
	}
	
	/**
	 *  Splits an activation plan and reattaches the copy to the provided goal, disconnection the old plan edge.
	 */
	public static final ActivationPlanEditPart splitActivationPlan(Node sourceGoalNode, Node sourcePlanNode, EditPart oldTarget, EditPart newTarget, DiagramEditPart diagramEditPart)
	{
		boolean planVisible = sourcePlanNode.isVisible();
		boolean goalVisible = sourceGoalNode.isVisible();
		sourceGoalNode.setVisible(true);
		sourcePlanNode.setVisible(true);
		diagramEditPart.refresh();
		GoalEditPart sourceGoal = (GoalEditPart) resolveEditPart(diagramEditPart, sourceGoalNode);
		
		final Edge oldPlanEdge = SGpmnUtilities.findEdge(sourceGoalNode, sourcePlanNode);
		//((GpmnDiagram) diagramEditPart.getNotationView());
		EcoreUtil.remove(oldPlanEdge.getElement());
		/*Display.getCurrent().asyncExec(new Runnable()
		{
			public void run()
			{
				ViewUtil.destroy(oldPlanEdge);
			}
		});*/
		
		ActivationPlanEditPart targetPlan = createRetargetedActivationPlanCopy(sourcePlanNode, oldTarget, newTarget, sourceGoalNode, diagramEditPart);
		Node targetPlanNode = (Node) targetPlan.getNotationView();
		
		CreateConnectionViewRequest req = CreateViewRequestFactory.getCreateConnectionRequest(GpmnElementTypes.PlanEdge_4002, diagramEditPart.getDiagramPreferencesHint());
		CreateConnectionViewAndElementRequest.getCreateCommand(req, sourceGoal, targetPlan).execute();
		targetPlan.refresh();
		//DestroyElementRequest dReq = new DestroyElementRequest(SGpmnUtilities.findEdge(sourceGoalNode, sourcePlanNode), false);
		//(new ICommandProxy(new DestroyElementCommand(dReq))).execute();
		//(new ICommandProxy(GpmnElementTypes.GpmnDiagram_1000.getEditCommand(dReq))).execute();
		
		Edge[] edges = (Edge[]) sourcePlanNode.getSourceEdges().toArray(new Edge[0]);
		for (int i = 0; i < edges.length; ++i)
			if (edges[i].getType().equals(DiagramNotationType.NOTE_ATTACHMENT.getSemanticHint()) && ((Edge) edges[i].getTarget()).getSource().equals(sourceGoalNode))
			{
				edges[i].setSource(targetPlanNode);
				targetPlan.refresh();
			}
		
		ActivationPlanEditPart sourcePlan = (ActivationPlanEditPart) resolveEditPart(diagramEditPart, sourcePlanNode);
		List<Node> peers = new ArrayList<Node>();
		for (Object conn : typeFilterViewList(GpmnElementTypes.PlanEdge_4002, sourcePlanNode.getTargetEdges()))
			peers.add((Node) ((Edge) conn).getTarget());
		for (Object conn : typeFilterViewList(GpmnElementTypes.ActivationEdge_4001, sourcePlanNode.getSourceEdges()))
			peers.add((Node) ((Edge) conn).getTarget());
		PrecisionPoint p = getCenter((Node[]) peers.toArray(new Node[0]));
		((Location) ((Node) sourcePlan.getNotationView()).getLayoutConstraint()).setX((int)Math.round(p.preciseX));
		((Location) ((Node) sourcePlan.getNotationView()).getLayoutConstraint()).setY((int)Math.round(p.preciseY));
		
		targetPlanNode.setVisible(planVisible);
		sourcePlanNode.setVisible(planVisible);
		sourceGoalNode.setVisible(goalVisible);
		diagramEditPart.refresh();
		
		return targetPlan;
	}
	
	/**
	 *  Copies an activation plan except for id and plan edges where one activation edge is re-targeted.
	 *  @param sourcePlanNode Source activation plan.
	 *  @param oldTarget The old activation edge target.
	 *  @param newTarget The new activation edge target.
	 *  @param refNode Optional reference node, may be null.
	 *  @param diagramEditPart The DiagramEditPart.
	 *  @return new activation plan
	 */
	private static final ActivationPlanEditPart createRetargetedActivationPlanCopy(Node sourcePlanNode, EditPart oldTarget, EditPart newTarget, Node refNode, DiagramEditPart diagramEditPart)
	{
		// Copy activation plan
		ActivationPlan sourcePlan = (ActivationPlan) sourcePlanNode.getElement();
		
		ActivationPlanEditPart newPlanPart = copyActivationPlan(sourcePlanNode, diagramEditPart);
		
		List<Node> nodes = new ArrayList<Node>();
		for (Object conn : typeFilterViewList(GpmnElementTypes.ActivationEdge_4001, sourcePlanNode.getSourceEdges()))
		{
			Edge edge = (Edge) conn;
			EditPart part = SGpmnUtilities.resolveEditPart(diagramEditPart, edge.getTarget());
			
			if (part instanceof GoalEditPart)
			{
				if (part.equals(oldTarget))
					if (newTarget == null)
						continue;
					else
						part = newTarget;
				nodes.add((Node)((IGraphicalEditPart) part).getNotationView());
				CreateConnectionViewRequest req = CreateViewRequestFactory.getCreateConnectionRequest(GpmnElementTypes.ActivationEdge_4001, diagramEditPart.getDiagramPreferencesHint());
				CreateConnectionViewAndElementRequest.getCreateCommand(req, newPlanPart, part).execute();
				newPlanPart.refresh();
				ActivationEdge actEdge = (ActivationEdge) ((Edge) ((ConnectionViewDescriptor) req.getNewObject()).getAdapter(Edge.class)).getElement();
				actEdge.setOrder(((ActivationEdge) edge.getElement()).getOrder()); 
			}
		}
		if (refNode != null)
			nodes.add(refNode);
		
		PrecisionPoint p = getCenter(nodes.toArray(new Node[0]));
		((Location) ((Node) newPlanPart.getNotationView()).getLayoutConstraint()).setX((int)Math.round(p.preciseX));
		((Location) ((Node) newPlanPart.getNotationView()).getLayoutConstraint()).setY((int)Math.round(p.preciseY));
		
		diagramEditPart.getViewer().reveal(newPlanPart);
		return newPlanPart;
	}
	
	/**
	 *  Copies an activation plan except for id and edges.
	 *  @param source Source activation plan.
	 *  @param diagramEditPart The DiagramEditPart.
	 *  @return new activation plan
	 */
	private static final ActivationPlanEditPart copyActivationPlan(Node source, DiagramEditPart diagramEditPart)
	{
		if (source == null || diagramEditPart == null)
			return null;
		
		CreateViewRequest apReq = CreateViewRequestFactory.getCreateShapeRequest(GpmnElementTypes.ActivationPlan_2001, diagramEditPart.getDiagramPreferencesHint());
		diagramEditPart.getCommand(apReq).execute();
		diagramEditPart.refresh();
		ActivationPlanEditPart target = (ActivationPlanEditPart) resolveEditPart(diagramEditPart, ((ViewAndElementDescriptor) ((List) apReq.getNewObject()).get(0)).getAdapter(Node.class));
		
		ActivationPlan sourcePlan = (ActivationPlan) source.getElement();
		ActivationPlan targetPlan = (ActivationPlan) target.getNotationView().getElement();
		if (sourcePlan.isSetContextcondition())
			targetPlan.setContextcondition(sourcePlan.getContextcondition());
		if (sourcePlan.isSetDescription())
			targetPlan.setDescription(sourcePlan.getDescription());
		if (sourcePlan.isSetMode())
			targetPlan.setMode(sourcePlan.getMode());
		if (sourcePlan.isSetName())
			targetPlan.setName(sourcePlan.getName());
		if (sourcePlan.isSetPrecondition())
			targetPlan.setPrecondition(sourcePlan.getPrecondition());
		if (sourcePlan.isSetPreconditionLanguage())
			targetPlan.setPreconditionLanguage(sourcePlan.getPreconditionLanguage());
		if (sourcePlan.isSetPriority())
			targetPlan.setPriority(sourcePlan.getPriority());
		if (sourcePlan.isSetTargetconditionLanguage())
			targetPlan.setTargetconditionLanguage(sourcePlan.getTargetconditionLanguage());
		
		return target;
	}
	
	public static final EditPart resolveEditPart(DiagramEditPart diagramEditPart, Object object)
	{
		if (object == null)
			return null;
		
		EditPart ret = (EditPart) diagramEditPart.getViewer().getEditPartRegistry().get(object);
		
		if (ret == null && object instanceof EObject)
			diagramEditPart.findEditPart(null, (EObject) object);
		
		if (ret == null)
		{
			if (object instanceof Edge)
			{
				for (Object edge : diagramEditPart.getConnections())
					if ((edge instanceof IGraphicalEditPart) && (object.equals(((IGraphicalEditPart) edge).getNotationView())))
						ret = (EditPart) edge;
			}
			else if (object instanceof Node)
			{
				for (Object node : diagramEditPart.getChildren())
					if ((node instanceof IGraphicalEditPart) && (object.equals(((IGraphicalEditPart) node).getNotationView())))
						ret = (EditPart) node;
				if (ret == null)
					for (Object node : diagramEditPart.getRoot().getChildren())
						if ((node instanceof IGraphicalEditPart) && (object.equals(((IGraphicalEditPart) node).getNotationView())))
							ret = (EditPart) node;
			}
			else if (object instanceof AbstractEdge)
			{
				for (Object edge : diagramEditPart.getConnections())
					if ((edge instanceof IGraphicalEditPart) && (object.equals(((IGraphicalEditPart) edge).getNotationView().getElement())))
						ret = (EditPart) edge;
			}
			else if (object instanceof AbstractNode)
			{
				for (Object node : diagramEditPart.getChildren())
					if ((node instanceof IGraphicalEditPart) && (object.equals(((IGraphicalEditPart) node).getNotationView().getElement())))
						ret = (EditPart) node;
			}
		}
		return ret;
	}
	
	/**
	 *  Returns an edge defined by its endpoints.
	 *  @param sourceView Source of the edge. 
	 *  @param targetView Target of the edge.
	 */
	public static final Edge findEdge(View sourceView, View targetView)
	{
		Edge[] edges = (Edge[]) targetView.getTargetEdges().toArray(new Edge[0]);
		for (int i = 0; i < edges.length; ++i)
			if (edges[i].getSource().equals(sourceView))
				return edges[i];
		System.err.println("Connection from " + sourceView + " to " + targetView + " not found!");
		return null;
	}
	
	/**
	 *  Destroys an edge defined by its endpoints.
	 *  @param sourceView Source of the edge. 
	 *  @param targetView Target of the edge.
	 */
	public static final void destroyEdge(View sourceView, View targetView)
	{
		Edge edge = findEdge(sourceView, targetView);
		if (edge != null)
			destroy(edge);
		
	}
	
	/**
	 *  Destroys an element.
	 *  @param view view of the element.
	 */
	public static final void destroy(View view)
	{
		if (view.getElement() != null)
			EcoreUtil.remove(view.getElement());
		else
			ViewUtil.destroy(view);
	}
	
	/**
	 *  Tests if two Views have a connection.
	 *  @param sourceView The source view.
	 *  @param targetView The target view.
	 *  @return true, if the views have a connection.
	 */
	public static final boolean hasConnection(View sourceView, View targetView)
	{
		if (sourceView != null && targetView != null)
			for (Object conn : sourceView.getSourceEdges())
				if (((Edge) conn).getTarget().equals(targetView))
					return true;
		return false;
	}
	
	/**
	 *  Returns the number of plan edges going into an activation plan.
	 *  @param plan The plan view.
	 *  @return The number of plan edges.
	 */
	public static final int getPlanEdgeCount(Node plan)
	{
		return plan.getTargetEdges().size();
	}
	
	public static final List<PlanEdge> getPlanEdges(Goal goal)
	{
		List<PlanEdge> allPlanEdges = goal.getGpmnDiagram().getPlanEdges();
		List<PlanEdge> planEdges = new ArrayList<PlanEdge>();
		
		for (PlanEdge planEdge : allPlanEdges)
			if (planEdge.getSource().equals(goal))
				planEdges.add(planEdge);
		
		return planEdges;
	}
	
	public static final Edge unwrapView(CreateConnectionViewRequest req)
	{
		return (Edge) ((ConnectionViewDescriptor) req.getNewObject()).getAdapter(Edge.class);
	}
	
	public static final EditPart unwrap(CreateConnectionViewRequest req)
	{
		EditPart part = req.getSourceEditPart() != null? req.getSourceEditPart() : req.getTargetEditPart();
		DiagramEditPart diagramEditPart = (DiagramEditPart) part.getRoot().getContents();
		return resolveEditPart(diagramEditPart, unwrapView(req));
	}
	
	public static final PrecisionPoint getCenter(Node[] nodes)
	{
		PrecisionPoint ret = new PrecisionPoint();
		for (int i = 0; i < nodes.length; ++i)
		{
			Location loc = (Location) nodes[i].getLayoutConstraint();
			ret.preciseX += loc.getX();
			ret.preciseY += loc.getY();
		}
		
		ret.preciseX /= nodes.length;
		ret.preciseY /= nodes.length;
		
		return ret;
	}
	
	public static final List<View> typeFilterViewList(IElementType type, List viewList)
	{
		List<View> ret = new ArrayList<View>();
		for (Object view : viewList)
			if (((IHintedType) type).getSemanticHint().equals(((View) view).getType()))
				ret.add((View) view);
		return ret;
	}
	
	public static final Node getPlanFromVirtualEdge(Edge vaeEdge)
	{
		return (Node) ((Edge) ((Edge) vaeEdge.getTargetEdges().get(0)).getSource()).getSource();
		//return (Node) ((Edge) vaeEdge.getTargetEdges().get(0)).getSource();
	}
}
