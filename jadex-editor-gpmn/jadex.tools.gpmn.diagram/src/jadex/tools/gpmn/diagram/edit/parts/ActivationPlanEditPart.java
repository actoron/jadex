/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.edit.parts;

import jadex.tools.gpmn.ActivationPlan;
import jadex.tools.gpmn.GpmnPackage;
import jadex.tools.gpmn.ModeType;
import jadex.tools.gpmn.diagram.edit.policies.ActivationPlanItemSemanticEditPolicy;
import jadex.tools.gpmn.diagram.edit.policies.ConnectionHandleEditPolicyEx;
import jadex.tools.gpmn.diagram.part.GpmnVisualIDRegistry;
import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;
import jadex.tools.gpmn.diagram.tools.ActivationPlanSelectToolEx;
import jadex.tools.gpmn.diagram.tools.SGpmnUtilities;
import jadex.tools.gpmn.diagram.ui.ShadowedRoundedRectangleFigure;
import jadex.tools.gpmn.diagram.ui.SlidableRoundedRectangleAnchor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.SelectionRequest;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.diagram.ui.requests.ToggleConnectionLabelsRequest;
import org.eclipse.gmf.runtime.draw2d.ui.figures.ConstrainedToolbarLayout;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.gef.ui.figures.DefaultSizeNodeFigure;
import org.eclipse.gmf.runtime.gef.ui.figures.NodeFigure;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @generated
 */
public class ActivationPlanEditPart extends ShapeNodeEditPart
{
	
	/**
	 * @generated
	 */
	public static final int VISUAL_ID = 2001;
	
	/**
	 * @generated
	 */
	protected IFigure contentPane;
	
	/**
	 * @generated
	 */
	protected IFigure primaryShape;
	
	/**
	 * @generated
	 */
	public ActivationPlanEditPart(View view)
	{
		super(view);
	}
	
	/**
	 * @generated
	 */
	protected void createDefaultEditPoliciesGen()
	{
		super.createDefaultEditPolicies();
		installEditPolicy(EditPolicyRoles.SEMANTIC_ROLE,
				new ActivationPlanItemSemanticEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, createLayoutEditPolicy());
		// XXX need an SCR to runtime to have another abstract superclass that would let children add reasonable editpolicies
		// removeEditPolicy(org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles.CONNECTION_HANDLES_ROLE);
	}
	
	/**
	 * @generated NOT, use custom edit policies
	 */
	protected void createDefaultEditPolicies()
	{
		this.createDefaultEditPoliciesGen();
		
		// use custom connection handle and tool
		removeEditPolicy(EditPolicyRoles.CONNECTION_HANDLES_ROLE);
		installEditPolicy(EditPolicyRoles.CONNECTION_HANDLES_ROLE,
				new ConnectionHandleEditPolicyEx());
	}
	
	/**
	 * @generated
	 */
	protected LayoutEditPolicy createLayoutEditPolicy()
	{
		LayoutEditPolicy lep = new LayoutEditPolicy()
		{
			
			protected EditPolicy createChildEditPolicy(EditPart child)
			{
				EditPolicy result = child
						.getEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE);
				if (result == null)
				{
					result = new NonResizableEditPolicy();
				}
				return result;
			}
			
			protected Command getMoveChildrenCommand(Request request)
			{
				return null;
			}
			
			protected Command getCreateCommand(CreateRequest request)
			{
				return null;
			}
		};
		return lep;
	}
	
	/**
	 * @generated
	 */
	protected IFigure createNodeShape()
	{
		ActivationPlanFigure figure = new ActivationPlanFigure();
		return primaryShape = figure;
	}
	
	/**
	 * @generated NOT
	 */
	public DragTracker getDragTracker(Request req)
	{
		if (req instanceof SelectionRequest)
			return new ActivationPlanSelectToolEx(this);
		return super.getDragTracker(req);
	}
	
	/**
	 * @generated
	 */
	public ActivationPlanFigure getPrimaryShape()
	{
		return (ActivationPlanFigure) primaryShape;
	}
	
	/**
	 * @generated
	 */
	protected boolean addFixedChild(EditPart childEditPart)
	{
		if (childEditPart instanceof ActivationPlanNameEditPart)
		{
			((ActivationPlanNameEditPart) childEditPart)
					.setLabel(getPrimaryShape()
							.getFigureActivationPlanNameFigure());
			return true;
		}
		return false;
	}
	
	/**
	 * @generated
	 */
	protected boolean removeFixedChild(EditPart childEditPart)
	{
		if (childEditPart instanceof ActivationPlanNameEditPart)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * @generated
	 */
	protected void addChildVisual(EditPart childEditPart, int index)
	{
		if (addFixedChild(childEditPart))
		{
			return;
		}
		super.addChildVisual(childEditPart, -1);
	}
	
	/**
	 * @generated
	 */
	protected void removeChildVisual(EditPart childEditPart)
	{
		if (removeFixedChild(childEditPart))
		{
			return;
		}
		super.removeChildVisual(childEditPart);
	}
	
	/**
	 * @generated
	 */
	protected IFigure getContentPaneFor(IGraphicalEditPart editPart)
	{
		return getContentPane();
	}
	
	/**
	 * @generated
	 */
	protected NodeFigure createNodePlate()
	{
		DefaultSizeNodeFigure result = new DefaultSizeNodeFigure(40, 40);
		return result;
	}
	
	/**
	 * Creates figure for this edit part.
	 * 
	 * Body of this method does not depend on settings in generation model
	 * so you may safely remove <i>generated</i> tag and modify it.
	 * 
	 * @generated NOT
	 */
	protected NodeFigure createNodeFigure()
	{
		NodeFigure figure = createNodePlate();
		figure.setLayoutManager(new StackLayout());
		IFigure shape = createNodeShape();
		
		// update decorator and label in figure 
		setDecoratorAndLabel((ActivationPlanFigure) shape);
		
		figure.add(shape);
		contentPane = setupContentPane(shape);
		return figure;
	}
	
	/**
	 * Update the figure with proper mode state and label layout
	 * 
	 * @param shape
	 * @generated NOT
	 */
	private void setDecoratorAndLabel(ActivationPlanFigure shape)
	{
		
		ActivationPlan plan = (ActivationPlan) getPrimaryView().getElement();
		shape.setShowPlanModeDecorator(ModeType.SEQUENTIAL.equals(plan
				.getMode()));
		
		WrappingLabel wl = shape.getFigureActivationPlanNameFigure();
		wl.setTextWrap(true);
		wl.setAlignment(PositionConstants.CENTER);
		wl.setTextJustification(PositionConstants.CENTER);
		
		shape.invalidate();
	}
	
	/**
	 * Default implementation treats passed figure as content pane.
	 * Respects layout one may have set for generated figure.
	 * @param nodeShape instance of generated figure class
	 * @generated
	 */
	protected IFigure setupContentPane(IFigure nodeShape)
	{
		if (nodeShape.getLayoutManager() == null)
		{
			ConstrainedToolbarLayout layout = new ConstrainedToolbarLayout();
			layout.setSpacing(5);
			nodeShape.setLayoutManager(layout);
		}
		return nodeShape; // use nodeShape itself as contentPane
	}
	
	/**
	 * @generated
	 */
	public IFigure getContentPane()
	{
		if (contentPane != null)
		{
			return contentPane;
		}
		return super.getContentPane();
	}
	
	/**
	 * @generated
	 */
	protected void setForegroundColor(Color color)
	{
		if (primaryShape != null)
		{
			primaryShape.setForegroundColor(color);
		}
	}
	
	/**
	 * @generated
	 */
	protected void setBackgroundColor(Color color)
	{
		if (primaryShape != null)
		{
			primaryShape.setBackgroundColor(color);
		}
	}
	
	/**
	 * @generated
	 */
	protected void setLineWidth(int width)
	{
		if (primaryShape instanceof Shape)
		{
			((Shape) primaryShape).setLineWidth(width);
		}
	}
	
	/**
	 * @generated
	 */
	protected void setLineType(int style)
	{
		if (primaryShape instanceof Shape)
		{
			((Shape) primaryShape).setLineStyle(style);
		}
	}
	
	/**
	 * @generated
	 */
	public EditPart getPrimaryChildEditPart()
	{
		return getChildBySemanticHint(GpmnVisualIDRegistry
				.getType(ActivationPlanNameEditPart.VISUAL_ID));
	}
	
	/**
	 * @generated
	 */
	public List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/getMARelTypesOnSource()
	{
		List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/types = new ArrayList/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/();
		types.add(GpmnElementTypes.ActivationEdge_4001);
		return types;
	}
	
	/**
	 * @generated
	 */
	public List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/getMARelTypesOnSourceAndTarget(
			IGraphicalEditPart targetEditPart)
	{
		List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/types = new ArrayList/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/();
		if (targetEditPart instanceof SubProcessEditPart)
		{
			types.add(GpmnElementTypes.ActivationEdge_4001);
		}
		if (targetEditPart instanceof GoalEditPart)
		{
			types.add(GpmnElementTypes.ActivationEdge_4001);
		}
		return types;
	}
	
	/**
	 * @generated
	 */
	public List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/getMATypesForTarget(
			IElementType relationshipType)
	{
		List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/types = new ArrayList/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/();
		if (relationshipType == GpmnElementTypes.ActivationEdge_4001)
		{
			types.add(GpmnElementTypes.SubProcess_2002);
		}
		if (relationshipType == GpmnElementTypes.ActivationEdge_4001)
		{
			types.add(GpmnElementTypes.Goal_2004);
		}
		return types;
	}
	
	/**
	 * @generated
	 */
	public List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/getMARelTypesOnTarget()
	{
		List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/types = new ArrayList/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/();
		types.add(GpmnElementTypes.PlanEdge_4002);
		return types;
	}
	
	/**
	 * @generated
	 */
	public List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/getMATypesForSource(
			IElementType relationshipType)
	{
		List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/types = new ArrayList/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/();
		if (relationshipType == GpmnElementTypes.PlanEdge_4002)
		{
			types.add(GpmnElementTypes.Goal_2004);
		}
		return types;
	}
	
	/**
	 * @generated NOT
	 */
	protected void addTargetConnection(ConnectionEditPart connection, int index)
	{
		super.addTargetConnection(connection, index);
		EditPart part = connection.getSource();
		if (part instanceof GoalEditPart)
		{
			GoalEditPart goalPart = (GoalEditPart) part;
			ActivationPlan plan = ((ActivationPlan) getNotationView()
					.getElement());
			goalPart.planAdded(plan);
		}
	}
	
	/**
	 * @generated NOT
	 */
	protected void removeTargetConnection(ConnectionEditPart connection)
	{
		super.removeTargetConnection(connection);
		EditPart part = connection.getSource();
		if (part instanceof GoalEditPart)
		{
			GoalEditPart goalPart = (GoalEditPart) part;
			ActivationPlan plan = ((ActivationPlan) getNotationView()
					.getElement());
			goalPart.planRemoved(plan);
		}
	}
	
	/**
	 * @generated NOT
	 */
	protected void handleNotificationEvent(Notification notification)
	{
		if (notification.getEventType() == Notification.SET
				|| notification.getEventType() == Notification.UNSET)
		{
			if (GpmnPackage.eINSTANCE.getActivationPlan_Mode().equals(
					notification.getFeature()))
			{
				// orderedMode changed
				final boolean showDecoratorAndLabels = ModeType.SEQUENTIAL
						.equals(notification.getNewValue());
				
				getPrimaryShape().setShowPlanModeDecorator(
						showDecoratorAndLabels);
				toggleActivationConnectionLabels(showDecoratorAndLabels);
				List inEdges = getTargetConnections();
				for (Object obj : inEdges)
				{
					if (obj instanceof PlanEdgeEditPart)
					{
						PlanEdgeEditPart edge = (PlanEdgeEditPart) obj;
						GoalEditPart goal = (GoalEditPart) edge.getSource();
						//TODO: Remove if it works
						//goal.refreshModes((ActivationPlan)getNotationView().getElement(), (ModeType) notification.getNewValue(), true);
						goal.planModeChanged((ActivationPlan) getNotationView()
								.getElement(), (ModeType) notification
								.getNewValue());
					}
				}
			}
		}
		
		super.handleNotificationEvent(notification);
	}
	
	/**
	 * @generated NOT
	 */
	public ConnectionAnchor getTargetConnectionAnchor(
			final ConnectionEditPart connEditPart)
	{
		ConnectionAnchor anchor = new SlidableRoundedRectangleAnchor(
				(ShadowedRoundedRectangleFigure) primaryShape);
		return anchor;
	}
	
	/**
	 * @generated NOT
	 */
	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connEditPart)
	{
		return getTargetConnectionAnchor(connEditPart);
	}
	
	/**
	 * Change the show/hide property of all outgoing Edges
	 * 
	 * @param showConnectionLabels
	 *            to show/hide the labels
	 * @generated NOT
	 */
	protected void toggleActivationConnectionLabels(boolean showConnectionLabels)
	{
		CompoundCommand cc = new CompoundCommand(
				"Toggle source connections labels");
		for (Object e : getSourceConnections())
		{
			if (e instanceof ConnectionEditPart)
			{
				final ConnectionEditPart cep = (ConnectionEditPart) e;
				final ToggleConnectionLabelsRequest tclr = new ToggleConnectionLabelsRequest(
						showConnectionLabels);
				
				// toggle connection label
				Display.getCurrent().asyncExec(new Runnable()
				{
					@Override
					public void run()
					{
						cep.performRequest(tclr);
					}
				});
			}
		}
		getDiagramEditDomain().getDiagramCommandStack().execute(cc);
	}
	
	/**
	 * @generated
	 */
	public class ActivationPlanFigure extends
			jadex.tools.gpmn.diagram.ui.figures.ActivationPlanFigure
	{
		
		/**
		 * @generated
		 */
		private WrappingLabel fFigureActivationPlanNameFigure;
		
		/**
		 * @generated
		 */
		public ActivationPlanFigure()
		{
			
			GridLayout layoutThis = new GridLayout();
			layoutThis.numColumns = 1;
			layoutThis.makeColumnsEqualWidth = true;
			this.setLayoutManager(layoutThis);
			
			this.setForegroundColor(ColorConstants.black);
			this.setMinimumSize(new Dimension(getMapMode().DPtoLP(120),
					getMapMode().DPtoLP(80)));
			createContents();
		}
		
		/**
		 * @generated
		 */
		private void createContents()
		{
			
			fFigureActivationPlanNameFigure = new WrappingLabel();
			fFigureActivationPlanNameFigure.setText("Plan");
			
			GridData constraintFFigureActivationPlanNameFigure = new GridData();
			constraintFFigureActivationPlanNameFigure.verticalAlignment = GridData.CENTER;
			constraintFFigureActivationPlanNameFigure.horizontalAlignment = GridData.CENTER;
			constraintFFigureActivationPlanNameFigure.horizontalIndent = 0;
			constraintFFigureActivationPlanNameFigure.horizontalSpan = 1;
			constraintFFigureActivationPlanNameFigure.verticalSpan = 1;
			constraintFFigureActivationPlanNameFigure.grabExcessHorizontalSpace = true;
			constraintFFigureActivationPlanNameFigure.grabExcessVerticalSpace = true;
			this.add(fFigureActivationPlanNameFigure,
					constraintFFigureActivationPlanNameFigure);
			
		}
		
		/**
		 * @generated
		 */
		private boolean myUseLocalCoordinates = false;
		
		/**
		 * @generated
		 */
		protected boolean useLocalCoordinates()
		{
			return myUseLocalCoordinates;
		}
		
		/**
		 * @generated
		 */
		protected void setUseLocalCoordinates(boolean useLocalCoordinates)
		{
			myUseLocalCoordinates = useLocalCoordinates;
		}
		
		/**
		 * @generated
		 */
		public WrappingLabel getFigureActivationPlanNameFigure()
		{
			return fFigureActivationPlanNameFigure;
		}
		
	}
	
}
