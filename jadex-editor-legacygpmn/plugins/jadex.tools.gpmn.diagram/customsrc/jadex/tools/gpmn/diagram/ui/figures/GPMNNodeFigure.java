/**
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id$
 */
package jadex.tools.gpmn.diagram.ui.figures;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gmf.runtime.draw2d.ui.figures.IPolygonAnchorableFigure;
import org.eclipse.gmf.runtime.gef.ui.figures.DefaultSizeNodeFigure;

/**
 * Manages connection anchors.
 * 
 */
public class GPMNNodeFigure extends DefaultSizeNodeFigure /*DefaultSizeNodeFigureEx*/ implements
		IPolygonAnchorableFigure
{

	/**
	 * Default constructor
	 * 
	 * @param connectionAnchorFactory
	 * @param defSize
	 */
	public GPMNNodeFigure(/*IConnectionAnchorFactory connectionAnchorFactory,*/
			Dimension defSize)
	{
		super(defSize /*, connectionAnchorFactory*/);
	}

//	/**
//	 * Currently calls super only
//	 * 
//	 * @param result
//	 *            The rectangle on which the bounds will be set.
//	 */
//	public void computeAbsoluteHandleBounds(Rectangle result)
//	{
//		// only compute super bounds, see BPMN ActivityNodeFigure for
//		// alternative HandleBounds dependent on LayoutManager
//		super.computeHandleBounds(result);
//	}

	/**
	 * Currently only calls super
	 * 
	 * @return Rectangle from super call
	 */
	@Override
	public Rectangle getHandleBounds()
	{
		// only compute super bounds, see BPMN ActivityNodeFigure for
		// alternative HandleBounds dependent on LayoutManager
		return super.getHandleBounds();
	}

	/**
	 * Generate the PointList for connection handle polygon. Currently this only
	 * returns the outline bounds of the rectangle.
	 */
	public PointList getPolygonPoints()
	{
		// only return rectangle bounds, see BPMN ActivityNodeFigure for
		// alternative PointList dependent on Layout/LayoutManager/Model
		PointList ptList = new PointList();
		ptList.addPoint(getHandleBounds().getTopLeft());
		ptList.addPoint(getHandleBounds().getTopRight());
		ptList.addPoint(getHandleBounds().getBottomRight());
		ptList.addPoint(getHandleBounds().getBottomLeft());
		ptList.addPoint(getHandleBounds().getTopLeft());
		return ptList;
	}
}
