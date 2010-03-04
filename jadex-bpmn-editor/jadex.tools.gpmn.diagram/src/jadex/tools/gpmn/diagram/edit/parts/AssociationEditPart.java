/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.edit.parts;

import jadex.tools.gpmn.diagram.edit.policies.AssociationItemSemanticEditPolicy;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PolylineDecoration;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ITreeBranchEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.draw2d.ui.figures.PolylineConnectionEx;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @generated
 */
public class AssociationEditPart extends ConnectionNodeEditPart implements
		ITreeBranchEditPart
{

	/**
	 * @generated
	 */
	public static final int VISUAL_ID = 4001;

	/**
	 * @generated
	 */
	public AssociationEditPart(View view)
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
				new AssociationItemSemanticEditPolicy());
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
		return new AssociationFigure();
	}

	/**
	 * @generated
	 */
	public AssociationFigure getPrimaryShape()
	{
		return (AssociationFigure) getFigure();
	}

	/**
	 * @generated
	 */
	public class AssociationFigure extends PolylineConnectionEx
	{

		/**
		 * @generated
		 */
		private WrappingLabel fFigureAssociationDirection;

		/**
		 * @generated
		 */
		public AssociationFigure()
		{
			this.setLineWidth(1);
			this.setLineStyle(Graphics.LINE_DASH);
			this.setForegroundColor(ColorConstants.gray);

			createContents();
			setSourceDecoration(createSourceDecoration());
			setTargetDecoration(createTargetDecoration());
		}

		/**
		 * @generated
		 */
		private void createContents()
		{

			fFigureAssociationDirection = new WrappingLabel();
			fFigureAssociationDirection.setText("");

			this.add(fFigureAssociationDirection);

		}

		/**
		 * @generated
		 */
		private RotatableDecoration createSourceDecoration()
		{
			PolylineDecoration df = new PolylineDecoration();
			df.setLineWidth(1);
			df.setLineStyle(Graphics.LINE_DOT);
			return df;
		}

		/**
		 * @generated
		 */
		private RotatableDecoration createTargetDecoration()
		{
			PolylineDecoration df = new PolylineDecoration();
			df.setLineWidth(1);
			df.setLineStyle(Graphics.LINE_DOT);
			return df;
		}

		/**
		 * @generated
		 */
		public WrappingLabel getFigureAssociationDirection()
		{
			return fFigureAssociationDirection;
		}

	}

}
