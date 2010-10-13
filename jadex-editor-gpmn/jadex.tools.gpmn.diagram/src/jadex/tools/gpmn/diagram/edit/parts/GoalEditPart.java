/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.edit.parts;

import jadex.tools.gpmn.ActivationPlan;
import jadex.tools.gpmn.Goal;
import jadex.tools.gpmn.GpmnDiagram;
import jadex.tools.gpmn.GpmnPackage;
import jadex.tools.gpmn.PlanEdge;
import jadex.tools.gpmn.diagram.edit.policies.ConnectionHandleEditPolicyEx;
import jadex.tools.gpmn.diagram.edit.policies.GoalItemSemanticEditPolicy;
import jadex.tools.gpmn.diagram.part.GpmnVisualIDRegistry;
import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeListener;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.draw2d.ui.figures.ConstrainedToolbarLayout;
import org.eclipse.gmf.runtime.draw2d.ui.figures.IOvalAnchorableFigure;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.gef.ui.figures.DefaultSizeNodeFigure;
import org.eclipse.gmf.runtime.gef.ui.figures.NodeFigure;
import org.eclipse.gmf.runtime.gef.ui.figures.SlidableOvalAnchor;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @generated
 */
public class GoalEditPart extends ShapeNodeEditPart
{
	
	/**
	 * @generated
	 */
	public static final int VISUAL_ID = 2004;
	
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
	public GoalEditPart(View view)
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
				new GoalItemSemanticEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, createLayoutEditPolicy());
		// XXX need an SCR to runtime to have another abstract superclass that would let children add reasonable editpolicies
		// removeEditPolicy(org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles.CONNECTION_HANDLES_ROLE);
	}
	
	/**
	 * Ability to override EditPolicies.
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
		GoalFigure figure = new GoalFigure();
		return primaryShape = figure;
	}
	
	/**
	 * @generated
	 */
	public GoalFigure getPrimaryShape()
	{
		return (GoalFigure) primaryShape;
	}
	
	/**
	 * @generated NOT
	 */
	public void refreshModes()
	{
		Set modes = new HashSet();
		
		List<PlanEdge> edges = ((Goal) getNotationView().getElement())
				.getPlanEdges();
		for (PlanEdge edge : edges)
			if (edge.getTarget() instanceof ActivationPlan)
				modes.add(((ActivationPlan) edge.getTarget()).getMode());
		/*if (mode.isEmpty())
			((jadex.tools.gpmn.diagram.ui.figures.GoalFigure) primaryShape).setModeType(jadex.tools.gpmn.diagram.ui.figures.GoalFigure.MODE_TYPE_NONE);
			case jadex.tools.gpmn.diagram.ui.figures.GoalFigure.MODE_TYPE_PARALLEL:
				((jadex.tools.gpmn.diagram.ui.figures.GoalFigure) primaryShape).setModeType(jadex.tools.gpmn.diagram.ui.figures.GoalFigure.MODE_TYPE_PARALLEL);
				break;
			case jadex.tools.gpmn.diagram.ui.figures.GoalFigure.MODE_TYPE_SEQUENTIAL:
				((jadex.tools.gpmn.diagram.ui.figures.GoalFigure) primaryShape).setModeType(jadex.tools.gpmn.diagram.ui.figures.GoalFigure.MODE_TYPE_SEQUENTIAL);
				break;
			default:
				((jadex.tools.gpmn.diagram.ui.figures.GoalFigure) primaryShape).setModeType(jadex.tools.gpmn.diagram.ui.figures.GoalFigure.MODE_TYPE_MIXED);
		}*/

		((jadex.tools.gpmn.diagram.ui.figures.GoalFigure) primaryShape)
				.setModeTypes(modes);
		
		primaryShape.invalidate();
		primaryShape.repaint();
		
		//return modes;
	}
	
	/**
	 * Update the figure with proper type and label layout
	 * 
	 * @param shape
	 * @generated NOT
	 */
	private void setGoalTypeAndLabel(GoalFigure shape)
	{
		
		Goal goal = (Goal) getPrimaryView().getElement();
		shape.setGoalType(goal.getGoalType().getLiteral());
		
		WrappingLabel wl = shape.getFigureGoalNameFigure();
		wl.setTextWrap(true);
		wl.setAlignment(PositionConstants.CENTER);
		wl.setTextJustification(PositionConstants.CENTER);
		
		// update name label
		if (goal.getName() == null)
		{
			/*if (!goal.getGoalType().getLiteral().equals(wl.getText()))
			{
				wl.setText(goal.getGoalType().getLiteral());
			}*/
			wl.setText("Unnamed_Goal");
		}
		
		shape.invalidate();
	}
	
	/**
	 * @generated
	 */
	protected boolean addFixedChild(EditPart childEditPart)
	{
		if (childEditPart instanceof GoalNameEditPart)
		{
			((GoalNameEditPart) childEditPart).setLabel(getPrimaryShape()
					.getFigureGoalNameFigure());
			return true;
		}
		return false;
	}
	
	/**
	 * @generated
	 */
	protected boolean removeFixedChild(EditPart childEditPart)
	{
		if (childEditPart instanceof GoalNameEditPart)
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
	 * @generated NOT, configure shape
	 */
	protected NodeFigure createNodeFigure()
	{
		NodeFigure figure = createNodePlate();
		figure.setLayoutManager(new StackLayout());
		IFigure shape = createNodeShape();
		
		// configure shape
		setGoalTypeAndLabel((GoalFigure) shape);
		
		figure.add(shape);
		contentPane = setupContentPane(shape);
		return figure;
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
				.getType(GoalNameEditPart.VISUAL_ID));
	}
	
	/**
	 * @generated
	 */
	public List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/getMARelTypesOnSource()
	{
		List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/types = new ArrayList/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/();
		types.add(GpmnElementTypes.PlanEdge_4002);
		types.add(GpmnElementTypes.SuppressionEdge_4004);
		return types;
	}
	
	/**
	 * @generated
	 */
	public List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/getMARelTypesOnSourceAndTarget(
			IGraphicalEditPart targetEditPart)
	{
		List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/types = new ArrayList/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/();
		if (targetEditPart instanceof ActivationPlanEditPart)
		{
			types.add(GpmnElementTypes.PlanEdge_4002);
		}
		if (targetEditPart instanceof BpmnPlanEditPart)
		{
			types.add(GpmnElementTypes.PlanEdge_4002);
		}
		if (targetEditPart instanceof jadex.tools.gpmn.diagram.edit.parts.GoalEditPart)
		{
			types.add(GpmnElementTypes.SuppressionEdge_4004);
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
		if (relationshipType == GpmnElementTypes.PlanEdge_4002)
		{
			types.add(GpmnElementTypes.ActivationPlan_2001);
		}
		if (relationshipType == GpmnElementTypes.PlanEdge_4002)
		{
			types.add(GpmnElementTypes.BpmnPlan_2003);
		}
		if (relationshipType == GpmnElementTypes.SuppressionEdge_4004)
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
		types.add(GpmnElementTypes.ActivationEdge_4001);
		types.add(GpmnElementTypes.SuppressionEdge_4004);
		return types;
	}
	
	/**
	 * @generated
	 */
	public List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/getMATypesForSource(
			IElementType relationshipType)
	{
		List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/types = new ArrayList/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/();
		if (relationshipType == GpmnElementTypes.ActivationEdge_4001)
		{
			types.add(GpmnElementTypes.ActivationPlan_2001);
		}
		if (relationshipType == GpmnElementTypes.SuppressionEdge_4004)
		{
			types.add(GpmnElementTypes.Goal_2004);
		}
		return types;
	}
	
	/**
	 * @generated NOT
	 */
	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connEditPart)
	{
		ConnectionAnchor anchor = new SlidableOvalAnchor(
				(IOvalAnchorableFigure) primaryShape)
		{
			@Override
			protected Rectangle getBox()
			{
				PrecisionRectangle box = new PrecisionRectangle(
						((IOvalAnchorableFigure) getOwner()).getOvalBounds());
				box.preciseX = box.preciseX - 1.0;
				box.preciseY = box.preciseY - 1.0;
				box.preciseWidth += 2;
				box.preciseHeight += 2;
				getOwner().translateToAbsolute(box);
				return box;
			}
		};
		return anchor;
	}
	
	/**
	 * @generated NOT
	 */
	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connEditPart)
	{
		return getSourceConnectionAnchor(connEditPart);
	}
	
	/**
	 * @generated NOT
	 */
	protected void handleNotificationEvent(Notification notification)
	{
		if (notification.getEventType() == Notification.SET
				|| notification.getEventType() == Notification.UNSET)
		{
			if (GpmnPackage.eINSTANCE.getGoal_GoalType().equals(
					notification.getFeature()))
			{
				getPrimaryShape().setGoalType(notification.getNewStringValue());
			}
			
		}
		
		/*if (notification.getEventType() == Notification.ADD
				|| notification.getEventType() == Notification.REMOVE)
		{
			if (notification.getFeatureID(EReference.class) == (NotationPackage.NODE__SOURCE_EDGES))
				Display.getCurrent().asyncExec(new Runnable()
				{
					@Override
					public void run()
					{
						refreshMode();
					}
				});
		}*/

		super.handleNotificationEvent(notification);
	}
	
	/**
	 * @generated
	 */
	public class GoalFigure extends
			jadex.tools.gpmn.diagram.ui.figures.GoalFigure
	{
		
		/**
		 * @generated
		 */
		private WrappingLabel fFigureGoalNameFigure;
		
		/**
		 * @generated
		 */
		public GoalFigure()
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
			
			fFigureGoalNameFigure = new WrappingLabel();
			fFigureGoalNameFigure.setText("Goal");
			
			GridData constraintFFigureGoalNameFigure = new GridData();
			constraintFFigureGoalNameFigure.verticalAlignment = GridData.CENTER;
			constraintFFigureGoalNameFigure.horizontalAlignment = GridData.CENTER;
			constraintFFigureGoalNameFigure.horizontalIndent = 0;
			constraintFFigureGoalNameFigure.horizontalSpan = 1;
			constraintFFigureGoalNameFigure.verticalSpan = 1;
			constraintFFigureGoalNameFigure.grabExcessHorizontalSpace = true;
			constraintFFigureGoalNameFigure.grabExcessVerticalSpace = true;
			this.add(fFigureGoalNameFigure, constraintFFigureGoalNameFigure);
			
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
		public WrappingLabel getFigureGoalNameFigure()
		{
			return fFigureGoalNameFigure;
		}
		
	}
}
