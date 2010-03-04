/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.edit.parts;

import jadex.tools.gpmn.Goal;
import jadex.tools.gpmn.GoalType;
import jadex.tools.gpmn.diagram.edit.policies.SequentialGoalItemSemanticEditPolicy;
import jadex.tools.gpmn.diagram.part.GpmnVisualIDRegistry;
import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.draw2d.ui.figures.ConstrainedToolbarLayout;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.gef.ui.figures.NodeFigure;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.swt.graphics.Color;

/**
 * @generated NOT
 * 		extend GoalEditPartSupport
 */
public class SequentialGoalEditPart extends GoalEditPartSupport
{

	/**
	 * @generated
	 */
	public static final int VISUAL_ID = 2006;

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
	public SequentialGoalEditPart(View view)
	{
		super(view);
	}

	/**
	 * @generated
	 */
	protected void createDefaultEditPolicies()
	{
		super.createDefaultEditPolicies();
		installEditPolicy(EditPolicyRoles.SEMANTIC_ROLE,
				new SequentialGoalItemSemanticEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, createLayoutEditPolicy());
		// XXX need an SCR to runtime to have another abstract superclass that would let children add reasonable editpolicies
		// removeEditPolicy(org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles.CONNECTION_HANDLES_ROLE);
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
	protected IFigure createNodeShapeGen()
	{
		GoalFigure figure = new GoalFigure();
		return primaryShape = figure;
	}

	/**
	 * @generated NOT
	 */
	protected IFigure createNodeShape()
	{
		GoalFigure figure = (GoalFigure) createNodeShapeGen();
		super.createNodeShapeSupport(figure);

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
	 * @generated
	 */
	protected boolean addFixedChild(EditPart childEditPart)
	{
		if (childEditPart instanceof SequentialGoalNameEditPart)
		{
			((SequentialGoalNameEditPart) childEditPart)
					.setLabel(getPrimaryShape().getFigureGoalNameFigure());
			return true;
		}
		return false;
	}

	/**
	 * @generated
	 */
	protected boolean removeFixedChild(EditPart childEditPart)
	{
		if (childEditPart instanceof SequentialGoalNameEditPart)
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
	 * Call {@link GoalEditPartSupport}{@link #createNodePlateSupport()}
	 * 
	 * @generated NOT
	 */
	protected NodeFigure createNodePlate()
	{
		// DefaultSizeNodeFigure result = new DefaultSizeNodeFigure(40, 40);
		// return result;
		return super.createNodePlateSupport();
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
				.getType(SequentialGoalNameEditPart.VISUAL_ID));
	}

	/**
	 * @generated
	 */
	public List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/getMARelTypesOnSource()
	{
		List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/types = new ArrayList/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/();
		types.add(GpmnElementTypes.SubGoalEdge_4002);
		types.add(GpmnElementTypes.PlanEdge_4003);
		return types;
	}

	/**
	 * @generated NOT
	 */
	public List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/getMARelTypesOnSourceAndTarget(
			IGraphicalEditPart targetEditPart)
	{
		return super.getMARelTypesOnSourceAndTarget(targetEditPart);
	}

	/**
	 * @generated NOT
	 */
	public List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/getMATypesForTarget(
			IElementType relationshipType)
	{
		return super.getMATypesForTarget(relationshipType);
	}

	/**
	 * @generated NOT remove PlanEdge here because the method in EditPolicy doesn't work as expected :-(
	 */
	public List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/getMARelTypesOnTarget()
	{
		List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/types = new ArrayList/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/();
		types.add(GpmnElementTypes.Association_4001);
		types.add(GpmnElementTypes.SubGoalEdge_4002);
		types.add(GpmnElementTypes.PlanEdge_4003);
		return types;
	}

	/**
	 * @generated NOT
	 */
	public List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/getMATypesForSource(
			IElementType relationshipType)
	{
		return super.getMATypesForSource(relationshipType);
	}

	/**
	 * @generated
	 */
	protected void handleNotificationEvent(Notification event)
	{
		if (event.getNotifier() == getModel()
				&& EcorePackage.eINSTANCE.getEModelElement_EAnnotations()
						.equals(event.getFeature()))
		{
			handleMajorSemanticChange();
		}
		else
		{
			super.handleNotificationEvent(event);
		}
	}

	/**
	 * @generated
	 */
	public class GoalFigure extends jadex.tools.gpmn.figures.GoalFigure
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

			this
					.setGoalType(getPrimaryView().getElement() instanceof Goal ? ((Goal) getPrimaryView()
							.getElement()).getGoalType().getLiteral()
							: GoalType.META_GOAL.getLiteral());

			this.setForegroundColor(ColorConstants.black);
			createContents();
		}

		/**
		 * @generated
		 */
		private void createContents()
		{

			fFigureGoalNameFigure = new WrappingLabel();
			fFigureGoalNameFigure.setText("Goal");

			this.add(fFigureGoalNameFigure);

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
