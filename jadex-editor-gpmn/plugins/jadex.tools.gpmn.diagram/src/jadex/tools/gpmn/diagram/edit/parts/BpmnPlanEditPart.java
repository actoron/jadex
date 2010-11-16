/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.edit.parts;

import jadex.tools.gpmn.BpmnPlan;
import jadex.tools.gpmn.GpmnPackage;
import jadex.tools.gpmn.diagram.edit.policies.BpmnPlanItemSemanticEditPolicy;
import jadex.tools.gpmn.diagram.edit.policies.ConnectionHandleEditPolicyEx;
import jadex.tools.gpmn.diagram.part.GpmnVisualIDRegistry;
import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;
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
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.draw2d.ui.figures.ConstrainedToolbarLayout;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.gef.ui.figures.DefaultSizeNodeFigure;
import org.eclipse.gmf.runtime.gef.ui.figures.NodeFigure;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.swt.graphics.Color;

/**
 * @generated
 */
public class BpmnPlanEditPart extends ShapeNodeEditPart
{
	
	/**
	 * @generated
	 */
	public static final int VISUAL_ID = 2003;
	
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
	public BpmnPlanEditPart(View view)
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
				new BpmnPlanItemSemanticEditPolicy());
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
	 * @generated NOT, update linked state and label in figure
	 */
	protected IFigure createNodeShape()
	{
		BpmnPlanFigure figure = new BpmnPlanFigure();
		setLinkRefAndLabel(figure);
		return primaryShape = figure;
	}
	
	/**
	 * @generated
	 */
	public BpmnPlanFigure getPrimaryShape()
	{
		return (BpmnPlanFigure) primaryShape;
	}
	
	/**
	 * Update the figure with proper link state and label layout
	 * 
	 * @param figure
	 * @generated NOT
	 */
	private void setLinkRefAndLabel(BpmnPlanFigure figure)
	{
		
		BpmnPlan plan = (BpmnPlan) getPrimaryView().getElement();
		figure.setLinked(null != plan.getPlanref()
				&& !plan.getPlanref().isEmpty());
		
		WrappingLabel wl = figure.getFigureBpmnPlanNameFigure();
		wl.setTextWrap(true);
		wl.setAlignment(PositionConstants.CENTER);
		wl.setTextJustification(PositionConstants.CENTER);
		
		figure.invalidate();
	}
	
	/**
	 * @generated
	 */
	protected boolean addFixedChild(EditPart childEditPart)
	{
		if (childEditPart instanceof BpmnPlanNameEditPart)
		{
			((BpmnPlanNameEditPart) childEditPart).setLabel(getPrimaryShape()
					.getFigureBpmnPlanNameFigure());
			return true;
		}
		return false;
	}
	
	/**
	 * @generated
	 */
	protected boolean removeFixedChild(EditPart childEditPart)
	{
		if (childEditPart instanceof BpmnPlanNameEditPart)
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
	 * @generated
	 */
	protected NodeFigure createNodeFigure()
	{
		NodeFigure figure = createNodePlate();
		figure.setLayoutManager(new StackLayout());
		IFigure shape = createNodeShape();
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
				.getType(BpmnPlanNameEditPart.VISUAL_ID));
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
	 * Synchronizes the shape with the plan
	 * 
	 * @generated NOT
	 * @see org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart
	 *      #handlePropertyChangeEvent(java.beans.PropertyChangeEvent)
	 */
	protected void handleNotificationEvent(Notification notification)
	{
		if (notification.getEventType() == Notification.SET
				|| notification.getEventType() == Notification.UNSET)
		{
			if (GpmnPackage.eINSTANCE.getBpmnPlan_Planref().equals(
					notification.getFeature()))
			{
				getPrimaryShape().setLinked(isPlanSet());
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
	
	// ---- some helper methods ----
	
	/**
	 * Check linking of BpmnPlan
	 * @generated NOT
	 */
	private boolean isPlanSet()
	{
		BpmnPlan plan = (BpmnPlan) getPrimaryView().getElement();
		return plan.getPlanref() != null && !plan.getPlanref().trim().isEmpty();
	}
	
	// ---- the BpmnPlan shape figure ----
	
	/**
	 * @generated
	 */
	public class BpmnPlanFigure extends
			jadex.tools.gpmn.diagram.ui.figures.BpmnPlanFigure
	{
		
		/**
		 * @generated
		 */
		private WrappingLabel fFigureBpmnPlanNameFigure;
		
		/**
		 * @generated
		 */
		public BpmnPlanFigure()
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
			
			fFigureBpmnPlanNameFigure = new WrappingLabel();
			fFigureBpmnPlanNameFigure.setText("BPMN");
			
			GridData constraintFFigureBpmnPlanNameFigure = new GridData();
			constraintFFigureBpmnPlanNameFigure.verticalAlignment = GridData.CENTER;
			constraintFFigureBpmnPlanNameFigure.horizontalAlignment = GridData.CENTER;
			constraintFFigureBpmnPlanNameFigure.horizontalIndent = 0;
			constraintFFigureBpmnPlanNameFigure.horizontalSpan = 1;
			constraintFFigureBpmnPlanNameFigure.verticalSpan = 1;
			constraintFFigureBpmnPlanNameFigure.grabExcessHorizontalSpace = true;
			constraintFFigureBpmnPlanNameFigure.grabExcessVerticalSpace = true;
			this.add(fFigureBpmnPlanNameFigure,
					constraintFFigureBpmnPlanNameFigure);
			
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
		public WrappingLabel getFigureBpmnPlanNameFigure()
		{
			return fFigureBpmnPlanNameFigure;
		}
		
	}
	
}
