/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.edit.parts;

import jadex.tools.gpmn.diagram.edit.policies.ContextItemSemanticEditPolicy;
import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;
import jadex.tools.gpmn.figures.GPMNNodeFigure;
import jadex.tools.gpmn.figures.GpmnShapesDefaultSizes;
import jadex.tools.gpmn.figures.connectionanchors.impl.ConnectionAnchorFactory;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Dimension;
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
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.gef.ui.figures.DefaultSizeNodeFigure;
import org.eclipse.gmf.runtime.gef.ui.figures.NodeFigure;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.stp.bpmn.figures.connectionanchors.IConnectionAnchorFactory;
import org.eclipse.swt.graphics.Color;

/**
 * @generated
 */
public class ContextEditPart extends ShapeNodeEditPart
{

	/**
	 * @generated
	 */
	public static final int VISUAL_ID = 2011;

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
	public ContextEditPart(View view)
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
				new ContextItemSemanticEditPolicy());
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
	protected IFigure createNodeShape()
	{
		ContextFigure figure = new ContextFigure();
		return primaryShape = figure;
	}

	/**
	 * @generated
	 */
	public ContextFigure getPrimaryShape()
	{
		return (ContextFigure) primaryShape;
	}

	/**
	 * @return An appropriate connection anchor factory
	 * @generated NOT
	 */
	protected IConnectionAnchorFactory getConnectionAnchorFactory()
	{
		return ConnectionAnchorFactory.INSTANCE;
	}

	/**
	 * @generated NOT
	 */
	protected NodeFigure createNodePlate()
	{
		Dimension minSize = (Dimension) getMapMode().DPtoLP(
				GpmnShapesDefaultSizes.CONTEXT_FIGURE_SIZE);
		return new GPMNNodeFigure(getConnectionAnchorFactory(), minSize);
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
	public List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/getMARelTypesOnSource()
	{
		List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/types = new ArrayList/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/();
		types.add(GpmnElementTypes.Association_4001);
		return types;
	}

	/**
	 * @generated
	 */
	public List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/getMARelTypesOnSourceAndTarget(
			IGraphicalEditPart targetEditPart)
	{
		List/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/types = new ArrayList/*<org.eclipse.gmf.runtime.emf.type.core.IElementType>*/();
		if (targetEditPart instanceof ProcessEditPart)
		{
			types.add(GpmnElementTypes.Association_4001);
		}
		if (targetEditPart instanceof AchieveGoalEditPart)
		{
			types.add(GpmnElementTypes.Association_4001);
		}
		if (targetEditPart instanceof MaintainGoalEditPart)
		{
			types.add(GpmnElementTypes.Association_4001);
		}
		if (targetEditPart instanceof PerformGoalEditPart)
		{
			types.add(GpmnElementTypes.Association_4001);
		}
		if (targetEditPart instanceof QueryGoalEditPart)
		{
			types.add(GpmnElementTypes.Association_4001);
		}
		if (targetEditPart instanceof SequentialGoalEditPart)
		{
			types.add(GpmnElementTypes.Association_4001);
		}
		if (targetEditPart instanceof ParallelGoalEditPart)
		{
			types.add(GpmnElementTypes.Association_4001);
		}
		if (targetEditPart instanceof MessageGoalEditPart)
		{
			types.add(GpmnElementTypes.Association_4001);
		}
		if (targetEditPart instanceof SubProcessGoalEditPart)
		{
			types.add(GpmnElementTypes.Association_4001);
		}
		if (targetEditPart instanceof PlanEditPart)
		{
			types.add(GpmnElementTypes.Association_4001);
		}
		if (targetEditPart instanceof GenericGpmnElementEditPart)
		{
			types.add(GpmnElementTypes.Association_4001);
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
		if (relationshipType == GpmnElementTypes.Association_4001)
		{
			types.add(GpmnElementTypes.Process_2001);
		}
		if (relationshipType == GpmnElementTypes.Association_4001)
		{
			types.add(GpmnElementTypes.AchieveGoal_2002);
		}
		if (relationshipType == GpmnElementTypes.Association_4001)
		{
			types.add(GpmnElementTypes.MaintainGoal_2003);
		}
		if (relationshipType == GpmnElementTypes.Association_4001)
		{
			types.add(GpmnElementTypes.PerformGoal_2004);
		}
		if (relationshipType == GpmnElementTypes.Association_4001)
		{
			types.add(GpmnElementTypes.QueryGoal_2005);
		}
		if (relationshipType == GpmnElementTypes.Association_4001)
		{
			types.add(GpmnElementTypes.SequentialGoal_2006);
		}
		if (relationshipType == GpmnElementTypes.Association_4001)
		{
			types.add(GpmnElementTypes.ParallelGoal_2007);
		}
		if (relationshipType == GpmnElementTypes.Association_4001)
		{
			types.add(GpmnElementTypes.MessageGoal_2008);
		}
		if (relationshipType == GpmnElementTypes.Association_4001)
		{
			types.add(GpmnElementTypes.SubProcessGoal_2009);
		}
		if (relationshipType == GpmnElementTypes.Association_4001)
		{
			types.add(GpmnElementTypes.Plan_2010);
		}
		if (relationshipType == GpmnElementTypes.Association_4001)
		{
			types.add(GpmnElementTypes.GenericGpmnElement_2014);
		}
		return types;
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
	public class ContextFigure extends jadex.tools.gpmn.figures.ContextFigure
	{

		/**
		 * @generated
		 */
		public ContextFigure()
		{

			this.setForegroundColor(ColorConstants.black);
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

	}

}
