/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.edit.parts;

import jadex.tools.gpmn.diagram.edit.policies.VirtualActivationEdgeEndPointsPolicy;
import jadex.tools.gpmn.diagram.tools.VirtualActivationEdgeSelectToolEx;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Connection;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.SelectionRequest;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ITreeBranchEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.ViewComponentEditPolicy;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @generated
 */
public class VirtualActivationEdgeEditPart extends ConnectionNodeEditPart
		implements ITreeBranchEditPart
{
	
	/**
	 * @generated
	 */
	public static final int VISUAL_ID = 4003;
	
	/**
	 * @generated
	 */
	public VirtualActivationEdgeEditPart(View view)
	{
		super(view);
	}
	
	/**
	 * @generated NOT
	 */
	protected void createDefaultEditPolicies()
	{
		super.createDefaultEditPolicies();
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new ViewComponentEditPolicy());
		removeEditPolicy(EditPolicyRoles.SEMANTIC_ROLE);
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new VirtualActivationEdgeEndPointsPolicy());
	}
	
	/**
	 * @generated
	 */
	protected boolean addFixedChild(EditPart childEditPart)
	{
		if (childEditPart instanceof VirtualActivationOrderEditPart)
		{
			((VirtualActivationOrderEditPart) childEditPart)
					.setLabel(getPrimaryShape()
							.getFigureVirtualActivationEdgeOrderFigure());
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
		if (childEditPart instanceof VirtualActivationOrderEditPart)
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
		return new VirtualActivationEdgeFigure();
	}
	
	/**
	 * @generated NOT
	 */
	public DragTracker getDragTracker(Request req)
	{
		if (req instanceof SelectionRequest)
			return new VirtualActivationEdgeSelectToolEx(this);
		return super.getDragTracker(req);
	}
	
	/**
	 * @generated
	 */
	public VirtualActivationEdgeFigure getPrimaryShape()
	{
		return (VirtualActivationEdgeFigure) getFigure();
	}
	
	/**
	 * @generated
	 */
	public class VirtualActivationEdgeFigure extends
			jadex.tools.gpmn.diagram.ui.figures.VirtualActivationEdgeFigure
	{
		
		/**
		 * @generated
		 */
		private WrappingLabel fFigureVirtualActivationEdgeOrderFigure;
		
		/**
		 * @generated
		 */
		public VirtualActivationEdgeFigure()
		{
			
			this.setForegroundColor(ColorConstants.black);
			createContents();
		}
		
		/**
		 * @generated
		 */
		private void createContents()
		{
			
			fFigureVirtualActivationEdgeOrderFigure = new WrappingLabel();
			fFigureVirtualActivationEdgeOrderFigure.setText("");
			
			this.add(fFigureVirtualActivationEdgeOrderFigure);
			
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
		public WrappingLabel getFigureVirtualActivationEdgeOrderFigure()
		{
			return fFigureVirtualActivationEdgeOrderFigure;
		}
		
	}
	
}
