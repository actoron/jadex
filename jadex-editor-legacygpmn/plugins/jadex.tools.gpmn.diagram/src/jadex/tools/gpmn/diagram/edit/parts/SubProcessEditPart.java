/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.edit.parts;

import jadex.tools.gpmn.GpmnPackage;
import jadex.tools.gpmn.SubProcess;
import jadex.tools.gpmn.diagram.edit.policies.SubProcessItemSemanticEditPolicy;
import jadex.tools.gpmn.diagram.part.GpmnDiagramEditorPlugin;
import jadex.tools.gpmn.diagram.part.GpmnVisualIDRegistry;
import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;
import jadex.tools.gpmn.diagram.ui.ShadowedRoundedRectangleFigure;
import jadex.tools.gpmn.diagram.ui.SlidableRoundedRectangleAnchor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.diagram.ui.figures.BorderItemsAwareFreeFormLayer;
import org.eclipse.gmf.runtime.diagram.ui.services.editpart.EditPartService;
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
public class SubProcessEditPart extends ShapeNodeEditPart
{
	
	/**
	 * @generated
	 */
	public static final int VISUAL_ID = 2002;
	
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
	public SubProcessEditPart(View view)
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
				new SubProcessItemSemanticEditPolicy());
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
		
		//		// use custom connection handle and tool
		//		removeEditPolicy(EditPolicyRoles.CONNECTION_HANDLES_ROLE);
		//		installEditPolicy(EditPolicyRoles.CONNECTION_HANDLES_ROLE,
		//				new ConnectionHandleEditPolicyEx());
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
		SubProcessFigure figure = new SubProcessFigure();
		setLinkRefAndLabel(figure);
		figure.setInternal(((SubProcess) getPrimaryView().getElement())
				.isInternal());
		return primaryShape = figure;
	}
	
	/**
	 * @generated
	 */
	public SubProcessFigure getPrimaryShape()
	{
		return (SubProcessFigure) primaryShape;
	}
	
	/**
	 * Update the figure with proper link state and label layout
	 * 
	 * @param figure
	 * @generated NOT
	 */
	private void setLinkRefAndLabel(SubProcessFigure figure)
	{
		
		SubProcess process = (SubProcess) getPrimaryView().getElement();
		figure.setLinked(null != process.getProcessref()
				&& !process.getProcessref().isEmpty());
		
		WrappingLabel wl = figure.getFigureSubProcessNameFigure();
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
		if (childEditPart instanceof SubProcessNameEditPart)
		{
			((SubProcessNameEditPart) childEditPart).setLabel(getPrimaryShape()
					.getFigureSubProcessNameFigure());
			return true;
		}
		return false;
	}
	
	/**
	 * @generated
	 */
	protected boolean removeFixedChild(EditPart childEditPart)
	{
		if (childEditPart instanceof SubProcessNameEditPart)
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
				.getType(SubProcessNameEditPart.VISUAL_ID));
	}
	
	/**
	 * @generated
	 */
	public List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/getMARelTypesOnTarget()
	{
		List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/types = new ArrayList/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/();
		types.add(GpmnElementTypes.ActivationEdge_4001);
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
		return types;
	}
	
	/**
	 * Synchronizes the shape with the sub process
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
			if (GpmnPackage.eINSTANCE.getSubProcess_Processref().equals(
					notification.getFeature()))
			{
				getPrimaryShape().setLinked(isProcessrefSet());
			}
			else if (GpmnPackage.eINSTANCE.getSubProcess_Internal().equals(
					notification.getFeature()))
			{
				getPrimaryShape().setInternal(
						((SubProcess) getPrimaryView().getElement())
								.isInternal());
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
	
	// ---- helper methods ----
	
	/**
	 * Check linking of SubProcess
	 * @generated NOT
	 */
	private boolean isProcessrefSet()
	{
		SubProcess process = (SubProcess) getPrimaryView().getElement();
		return process.getProcessref() != null
				&& !process.getProcessref().trim().isEmpty();
	}
	
	// --- the shape figure ----
	
	/**
	 * @generated
	 */
	public class SubProcessFigure extends
			jadex.tools.gpmn.diagram.ui.figures.SubProcessFigure
	{
		
		/**
		 * @generated
		 */
		private WrappingLabel fFigureSubProcessNameFigure;
		
		/**
		 * @generated
		 */
		public SubProcessFigure()
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
			
			fFigureSubProcessNameFigure = new WrappingLabel();
			fFigureSubProcessNameFigure.setText("SubProcess");
			
			GridData constraintFFigureSubProcessNameFigure = new GridData();
			constraintFFigureSubProcessNameFigure.verticalAlignment = GridData.CENTER;
			constraintFFigureSubProcessNameFigure.horizontalAlignment = GridData.CENTER;
			constraintFFigureSubProcessNameFigure.horizontalIndent = 0;
			constraintFFigureSubProcessNameFigure.horizontalSpan = 1;
			constraintFFigureSubProcessNameFigure.verticalSpan = 1;
			constraintFFigureSubProcessNameFigure.grabExcessHorizontalSpace = true;
			constraintFFigureSubProcessNameFigure.grabExcessVerticalSpace = true;
			this.add(fFigureSubProcessNameFigure,
					constraintFFigureSubProcessNameFigure);
			
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
		public WrappingLabel getFigureSubProcessNameFigure()
		{
			return fFigureSubProcessNameFigure;
		}
		
	}
	
}
