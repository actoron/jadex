/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.edit.parts;

import jadex.tools.gpmn.diagram.edit.policies.MessagingEdgeItemSemanticEditPolicy;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.Graphics;
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
public class MessagingEdgeEditPart extends ConnectionNodeEditPart implements
		ITreeBranchEditPart
{

	/**
	 * @generated
	 */
	public static final int VISUAL_ID = 4004;

	/**
	 * @generated
	 */
	public MessagingEdgeEditPart(View view)
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
				new MessagingEdgeItemSemanticEditPolicy());
	}

	/**
	 * @generated
	 */
	protected boolean addFixedChild(EditPart childEditPart)
	{
		if (childEditPart instanceof MessagingEdgeNameEditPart)
		{
			((MessagingEdgeNameEditPart) childEditPart)
					.setLabel(getPrimaryShape()
							.getFigureMessagingEdgeNameFigure());
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
		if (childEditPart instanceof MessagingEdgeNameEditPart)
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
		return new MessagingEdgeFigure();
	}

	/**
	 * @generated
	 */
	public MessagingEdgeFigure getPrimaryShape()
	{
		return (MessagingEdgeFigure) getFigure();
	}

	/**
	 * @generated
	 */
	public class MessagingEdgeFigure extends PolylineConnectionEx
	{

		/**
		 * @generated
		 */
		private WrappingLabel fFigureMessagingEdgeNameFigure;

		/**
		 * @generated
		 */
		public MessagingEdgeFigure()
		{
			this.setLineWidth(1);
			this.setLineStyle(Graphics.LINE_DASHDOTDOT);

			createContents();
			setSourceDecoration(createSourceDecoration());
			setTargetDecoration(createTargetDecoration());
		}

		/**
		 * @generated
		 */
		private void createContents()
		{

			fFigureMessagingEdgeNameFigure = new WrappingLabel();
			fFigureMessagingEdgeNameFigure.setText("<..>");

			this.add(fFigureMessagingEdgeNameFigure);

		}

		/**
		 * @generated
		 */
		private RotatableDecoration createSourceDecoration()
		{
			PolylineDecoration df = new PolylineDecoration();
			df.setLineWidth(1);
			return df;
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
		public WrappingLabel getFigureMessagingEdgeNameFigure()
		{
			return fFigureMessagingEdgeNameFigure;
		}

	}

}
