/******************************************************************************
 * Copyright (c) 2006, Intalio Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Intalio Inc. - initial API and implementation
 *******************************************************************************/

/**
 * Date             Author              Changes
 * Nov 24, 2006     hmalphettes         Created
 **/
package jadex.tools.gpmn.figures.connectionanchors.impl;

import jadex.tools.gpmn.diagram.edit.parts.MessagingEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SubGoalEdgeEditPart;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.stp.bpmn.figures.connectionanchors.IModelAwareAnchor;
import org.eclipse.stp.bpmn.figures.connectionanchors.IModelAwareAnchorSupport;
import org.eclipse.stp.bpmn.figures.connectionanchors.IModelAwareAnchor.INodeFigureAnchorTerminalUpdatable;

/**
 * <p>
 * Computes the anchor's position according to its usage and the preferences of
 * the diagram (default slideable, position constrained according to the type of
 * connection in the domain model).
 * </p>
 * <p>
 * If the anchor appears to not have that information set, it will behave like a
 * default slideable anchor.
 * </p>
 * <p>
 * There was another alternative: have the edge edit part update the anchor
 * itself.
 * </p>
 * 
 * @author hmalphettes
 * @author <a href="http://www.intalio.com">&copy; Intalio, Inc.</a>
 */
public class GpmnAwareAnchorSupport implements IModelAwareAnchorSupport
{

	/**
	 * Method called by the anchor to compute its location.
	 * 
	 * @param reference
	 *            The reference point.
	 * @return The location to use.
	 */
	public Point getLocation(IModelAwareAnchor anchor, Point reference)
	{
		// For this time, return always a default location handler
		//return returnDefaultLocation(anchor, reference);
		
		if (anchor.getConnectionType() == null
				|| anchor.getAnchorPositionningStyle() == IModelAwareAnchor.POSITIONNING_SLIDEABLE)
		{
			return returnDefaultLocation(anchor, reference);
		}
		if (anchor.getConnectionType().startsWith(
				String.valueOf(SubGoalEdgeEditPart.VISUAL_ID)))
		{
			// it is a subgoal edge
			if (anchor.isSourceAnchor() == IModelAwareAnchor.UNKNOWN_ANCHOR)
			{
				// it must be on the upside of the owner's figure.
				// we don't know.
				return returnDefaultLocation(anchor, reference);
			}
			Rectangle thisBox = new Rectangle();
			computeOwnerBounds(anchor, thisBox);
			Point thisCenter = thisBox.getCenter();
			Point res = new Point();
			//if (!anchor.getConnectionType().equals(
			//		String.valueOf(SubGoalEdgeEditPart.VISUAL_ID)))
			//{
			//	// this is for an event handler
			//	// shape on the border of a sub-process.
			//	res.x = thisCenter.x;
			//	res.y = thisBox.y + thisBox.height;
			//}
			//else
			//{
				if (anchor.isSourceAnchor() == IModelAwareAnchor.SOURCE_ANCHOR)
				{
					// it must be on the right of the owner's figure.
					//res.x = thisBox.x + thisBox.width;
					
					// subgoals edges starts from the bottom of a figure
					res.x = thisCenter.x;
					res.y = thisBox.y + thisBox.height;
				}
				else
				{
					// is target anchor
					// it must be on the left-side of the owner's figure.
					//res.x = thisBox.x;
					
					// subgoals edges end in the top of a figure
					res.x = thisCenter.x;
					res.y = thisBox.y;
				}

				if (anchor.getCount() > 0 && anchor.getOrderNumber() != -1)
				{
					// TODO: copy / implement constraints 
					// TODO: distribute on x axis!
//					int constraint = EdgeRectilinearRouter.NO_CONSTRAINT;
//					// we also want to distribute them on the y axis if
//					// it is edges of a gateway.
//					if (anchor.getConnectionOwner() instanceof SequenceEdgeEditPart.EdgeFigure)
//					{
//						constraint = anchor.isSourceAnchor() == IModelAwareAnchor.SOURCE_ANCHOR ? ((SequenceEdgeEditPart.EdgeFigure) anchor
//								.getConnectionOwner())
//								.getSourceGatewayConstraint()
//								: ((SequenceEdgeEditPart.EdgeFigure) anchor
//										.getConnectionOwner())
//										.getTargetGatewayConstraint();
//					}
//					if (constraint == EdgeRectilinearRouter.CONSTRAINT_ON_TOP)
//					{
//						res.y = thisBox.y;
//						res.x = thisCenter.x;
//					}
//					else if (constraint == EdgeRectilinearRouter.CONSTRAINT_BOTTOM)
//					{
//						res.x = thisCenter.x;
//						res.y = thisBox.y + thisBox.height;
//					}
//					else if (constraint == EdgeRectilinearRouter.CONSTRAINT_MIDDLE)
//					{
//						res.y = thisCenter.y;
//					}
//					else
//					{
						// well let's try distributing on the y axis for the
						// rest of them.
						//int height = thisBox.height;
						//res.y = thisBox.y + (height / (anchor.getCount() + 1))
						//		* (anchor.getOrderNumber() + 1);
					
						// well let's try distributing on the y axis for the
						// rest of them.
						int width = thisBox.width;
						res.x = thisBox.x + (width / (anchor.getCount() + 1))
								* (anchor.getOrderNumber() + 1);
//					}
				}
				else
				{
					res.y = thisCenter.y;
					//res.x = thisCenter.x;
				}
			//}
			return res;
		}
		
		
		if (anchor.getConnectionType().startsWith(
				String.valueOf(MessagingEdgeEditPart.VISUAL_ID)))
		{
			// it is a messaging edge
			// it is either on the top, either on the bottom depending on
			// where is the other-side of the connection
			if (anchor.getOwner() == null
					|| anchor.getConnectionOwner() == null
					|| anchor.isSourceAnchor() == IModelAwareAnchor.UNKNOWN_ANCHOR)
			{
				return returnDefaultLocation(anchor, reference);
			}
			Connection conn = anchor.getConnectionOwner();

			Rectangle thisBox = new Rectangle();
			computeOwnerBounds(anchor, thisBox);
			Point thisCenter = thisBox.getCenter();
			Point otherCenter = null;
			if (anchor.isSourceAnchor() == IModelAwareAnchor.SOURCE_ANCHOR)
			{
				if (conn.getTargetAnchor().getOwner() == null)
				{
					// this is usually the case for an XYAnchor
					otherCenter = conn.getTargetAnchor().getReferencePoint();
				}
				else
				{
					otherCenter = conn.getTargetAnchor().getOwner().getBounds()
							.getCenter();
					conn.getTargetAnchor().getOwner().translateToAbsolute(
							otherCenter);
				}
			}
			else
			{
				if (conn.getSourceAnchor().getOwner() == null)
				{
					otherCenter = conn.getSourceAnchor().getReferencePoint();
				}
				else
				{
					otherCenter = conn.getSourceAnchor().getOwner().getBounds()
							.getCenter();
					conn.getSourceAnchor().getOwner().translateToAbsolute(
							otherCenter);
				}
			}
			// System.err.println("isSource=" + (anchor.isSourceAnchor() ==
			// IModelAwareAnchor.SOURCE_ANCHOR) +
			// " thisCenter=" + thisCenter + " otherCenter=" + otherCenter);
			
			//Point res = new Point();
			//if (thisCenter.y > otherCenter.y)
			//{
			//	// at the top.
			//	res.y = thisBox.y;// thisCenter.y - thisBox.height/2;
			//}
			//else
			//{
			//	// at the bottom.
			//	res.y = thisBox.y + thisBox.height; // thisCenter.y +
			//										// thisBox.height/2;
			//}
			//if (anchor.getCount() > 0 && anchor.getOrderNumber() != -1)
			//{
			//	int width = thisBox.width;
			//	res.x = thisBox.x // thisCenter.x - width / 2 +
			//			+ (width / (anchor.getCount() + 1))
			//			* (anchor.getOrderNumber() + 1);
			//
			//}
			//else
			//{
			//	res.x = thisCenter.x;
			//}
			
			Point res = new Point();
			if (thisCenter.x < otherCenter.x)
			{
				// at the left.
				res.x = thisBox.x;// thisCenter.y - thisBox.height/2;
			}
			else
			{
				// at the right.
				res.x = thisBox.x + thisBox.width; // thisCenter.y +
													// thisBox.height/2;
			}
			if (anchor.getCount() > 0 && anchor.getOrderNumber() != -1)
			{
				int height = thisBox.height;
				res.y = thisBox.y
						+ (height / (anchor.getCount() + 1))
						* (anchor.getOrderNumber() + 1);

			}
			else
			{
				res.y = thisCenter.y;
			}

			return res;
		}
		if (reference == null)
		{
			Point thisCenter = anchor.getOwner().getBounds().getCenter();
			anchor.getOwner().translateToAbsolute(thisCenter);
			return thisCenter;
		}
		return anchor.getDefaultLocation(reference);
	}

	/**
	 * Helper method to return the default location.
	 * 
	 * @param anchor
	 *            The anchor
	 * @param reference
	 *            The reference point, might be null.
	 * @return
	 */
	protected Point returnDefaultLocation(IModelAwareAnchor anchor,
			Point reference)
	{
		if (reference == null && anchor.getOwner() != null)
		{
			computeOwnerBounds(anchor, Rectangle.SINGLETON);
			Point thisCenter = Rectangle.SINGLETON.getCenter();
			return thisCenter;
		}
		return anchor.getDefaultLocation(reference);
	}

	/**
	 * Helper method to return the bounds of the owner, or only the ones from
	 * its interesting feature. Translate to absolute.
	 * 
	 * @param anchor
	 * @return the bounds to create the anchor on.
	 */
	protected void computeOwnerBounds(IModelAwareAnchor anchor, Rectangle result)
	{
		INodeFigureAnchorTerminalUpdatable n = anchor.getCastedOwner();
		n.computeAbsoluteHandleBounds(result);
	}

}
