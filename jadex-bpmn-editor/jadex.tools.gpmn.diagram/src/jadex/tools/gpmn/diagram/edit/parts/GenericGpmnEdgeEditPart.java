/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.edit.parts;

import jadex.tools.gpmn.diagram.edit.policies.GenericGpmnEdgeItemSemanticEditPolicy;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.PolylineDecoration;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ITreeBranchEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.draw2d.ui.figures.PolylineConnectionEx;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @generated
 */
public class GenericGpmnEdgeEditPart extends ConnectionNodeEditPart implements
		ITreeBranchEditPart
{

	/**
	 * @generated
	 */
	public static final int VISUAL_ID = 4005;

	/**
	 * @generated
	 */
	public GenericGpmnEdgeEditPart(View view)
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
				new GenericGpmnEdgeItemSemanticEditPolicy());
	}

	/**
	 * @generated
	 */
	protected boolean addFixedChild(EditPart childEditPart)
	{
		if (childEditPart instanceof GenericGpmnEdgeNameEditPart)
		{
			((GenericGpmnEdgeNameEditPart) childEditPart)
					.setLabel(getPrimaryShape()
							.getFigureSubGoalEdgeOrderFigure());
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
	protected boolean removeFixedChild(EditPart childEditPart)
	{
		if (childEditPart instanceof GenericGpmnEdgeNameEditPart)
		{
			return true;
		}
		return false;
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
	 * Creates figure for this edit part.
	 * 
	 * Body of this method does not depend on settings in generation model
	 * so you may safely remove <i>generated</i> tag and modify it.
	 * 
	 * @generated
	 */

	protected Connection createConnectionFigure()
	{
		return new SubGoalEdgeFigure();
	}

	/**
	 * @generated
	 */
	public SubGoalEdgeFigure getPrimaryShape()
	{
		return (SubGoalEdgeFigure) getFigure();
	}

	/**
	 * @generated
	 */
	public class SubGoalEdgeFigure extends PolylineConnectionEx
	{

		/**
		 * @generated
		 */
		private WrappingLabel fFigureSubGoalEdgeOrderFigure;

		/**
		 * @generated
		 */
		public SubGoalEdgeFigure()
		{
			this.setLineWidth(1);
			this.setForegroundColor(ColorConstants.black);

			createContents();
			setTargetDecoration(createTargetDecoration());
		}

		/**
		 * @generated
		 */
		private void createContents()
		{

			fFigureSubGoalEdgeOrderFigure = new WrappingLabel();
			fFigureSubGoalEdgeOrderFigure.setText("");

			this.add(fFigureSubGoalEdgeOrderFigure);

		}

		/**
		 * @generated
		 */
		private RotatableDecoration createTargetDecoration()
		{
			PolylineDecoration df = new PolylineDecoration();
			df.setLineWidth(1);
			return df;
		}

		/**
		 * @generated
		 */
		public WrappingLabel getFigureSubGoalEdgeOrderFigure()
		{
			return fFigureSubGoalEdgeOrderFigure;
		}

	}

}
