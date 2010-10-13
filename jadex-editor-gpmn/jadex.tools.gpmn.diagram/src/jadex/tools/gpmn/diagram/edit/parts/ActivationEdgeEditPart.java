/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.edit.parts;

import jadex.tools.gpmn.ActivationPlan;
import jadex.tools.gpmn.ModeType;
import jadex.tools.gpmn.diagram.edit.policies.ActivationEdgeItemSemanticEditPolicy;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.PolylineDecoration;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ITreeBranchEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.diagram.ui.requests.ToggleConnectionLabelsRequest;
import org.eclipse.gmf.runtime.draw2d.ui.figures.PolylineConnectionEx;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.swt.widgets.Display;

/**
 * @generated
 */
public class ActivationEdgeEditPart extends ConnectionNodeEditPart implements
		ITreeBranchEditPart
{
	
	/**
	 * @generated
	 */
	public static final int VISUAL_ID = 4001;
	
	/**
	 * @generated
	 */
	public ActivationEdgeEditPart(View view)
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
				new ActivationEdgeItemSemanticEditPolicy());
	}
	
	/**
	 * @generated
	 */
	protected boolean addFixedChild(EditPart childEditPart)
	{
		if (childEditPart instanceof ActivationEdgeOrderEditPart)
		{
			((ActivationEdgeOrderEditPart) childEditPart)
					.setLabel(getPrimaryShape()
							.getFigureActivationEdgeOrderFigure());
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
		if (childEditPart instanceof ActivationEdgeOrderEditPart)
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
		return new ActivationEdgeFigure();
	}
	
	/**
	 * @generated
	 */
	public ActivationEdgeFigure getPrimaryShape()
	{
		return (ActivationEdgeFigure) getFigure();
	}
	
	/**
	 * @generated
	 */
	public class ActivationEdgeFigure extends PolylineConnectionEx
	{
		
		/**
		 * @generated
		 */
		private WrappingLabel fFigureActivationEdgeOrderFigure;
		
		/**
		 * @generated
		 */
		public ActivationEdgeFigure()
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
			
			fFigureActivationEdgeOrderFigure = new WrappingLabel();
			fFigureActivationEdgeOrderFigure.setText("");
			
			this.add(fFigureActivationEdgeOrderFigure);
			
		}
		
		/**
		 * @generated
		 */
		private RotatableDecoration createTargetDecoration()
		{
			PolylineDecoration df = new PolylineDecoration();
			df.setLineWidth(1);
			df.setForegroundColor(ColorConstants.black);
			return df;
		}
		
		/**
		 * @generated
		 */
		public WrappingLabel getFigureActivationEdgeOrderFigure()
		{
			return fFigureActivationEdgeOrderFigure;
		}
		
	}
	
}
