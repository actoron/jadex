/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.edit.parts;

import jadex.tools.gpmn.diagram.edit.policies.PlanEdgeItemSemanticEditPolicy;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.Graphics;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ITreeBranchEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.draw2d.ui.figures.PolylineConnectionEx;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @generated
 */
public class PlanEdgeEditPart extends ConnectionNodeEditPart implements
		ITreeBranchEditPart
{
	
	/**
	 * @generated
	 */
	public static final int VISUAL_ID = 4002;
	
	/**
	 * @generated
	 */
	public PlanEdgeEditPart(View view)
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
				new PlanEdgeItemSemanticEditPolicy());
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
		return new PlanEdgeFigure();
	}
	
	/**
	 * @generated
	 */
	public PlanEdgeFigure getPrimaryShape()
	{
		return (PlanEdgeFigure) getFigure();
	}
	
	/**
	 * @generated
	 */
	public class PlanEdgeFigure extends PolylineConnectionEx
	{
		
		/**
		 * @generated
		 */
		public PlanEdgeFigure()
		{
			this.setLineWidth(1);
			this.setForegroundColor(ColorConstants.black);
			
		}
		
	}
	
}
