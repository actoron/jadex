/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.edit.parts;

import jadex.tools.gpmn.diagram.edit.policies.SuppressionEdgeItemSemanticEditPolicy;

import jadex.tools.gpmn.diagram.ui.figures.SuppressionEdgeTargetDecoration;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ITreeBranchEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.draw2d.ui.figures.PolylineConnectionEx;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @generated
 */
public class SuppressionEdgeEditPart extends ConnectionNodeEditPart implements
		ITreeBranchEditPart
{
	
	/**
	 * @generated
	 */
	public static final int VISUAL_ID = 4004;
	
	/**
	 * @generated
	 */
	public SuppressionEdgeEditPart(View view)
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
				new SuppressionEdgeItemSemanticEditPolicy());
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
		return new SuppressionEdgeFigure();
	}
	
	/**
	 * @generated
	 */
	public SuppressionEdgeFigure getPrimaryShape()
	{
		return (SuppressionEdgeFigure) getFigure();
	}
	
	/**
	 * @generated
	 */
	public class SuppressionEdgeFigure extends PolylineConnectionEx
	{
		
		/**
		 * @generated
		 */
		public SuppressionEdgeFigure()
		{
			this.setLineWidth(1);
			this.setLineStyle(Graphics.LINE_DASH);
			this.setForegroundColor(ColorConstants.black);
			
			setTargetDecoration(createTargetDecoration());
		}
		
		/**
		 * @generated
		 */
		private RotatableDecoration createTargetDecoration()
		{
			SuppressionEdgeTargetDecoration df = new SuppressionEdgeTargetDecoration();
			
			return df;
		}
		
	}
	
}
