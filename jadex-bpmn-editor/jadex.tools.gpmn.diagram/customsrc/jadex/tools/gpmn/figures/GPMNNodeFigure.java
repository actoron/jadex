/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.figures;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gmf.runtime.draw2d.ui.figures.IPolygonAnchorableFigure;
import org.eclipse.stp.bpmn.figures.connectionanchors.DefaultSizeNodeFigureEx;
import org.eclipse.stp.bpmn.figures.connectionanchors.IConnectionAnchorFactory;

/**
 * Manages connection anchors.
 * 
 */
public class GPMNNodeFigure extends DefaultSizeNodeFigureEx implements
		IPolygonAnchorableFigure
{

	/**
	 * Default constructor
	 * 
	 * @param connectionAnchorFactory
	 * @param minSize
	 */
	public GPMNNodeFigure(IConnectionAnchorFactory connectionAnchorFactory,
			Dimension minSize)
	{
		super(minSize, connectionAnchorFactory);
	}

	/**
	 * Currently only calls super
	 * 
	 * @param result
	 *            The rectangle on which the bounds will be set.
	 */
	public void computeAbsoluteHandleBounds(Rectangle result)
	{
		// only compute super bounds, see BPMN ActivityNodeFigure for
		// alternative HandleBounds dependent on LayoutManager
		super.computeAbsoluteHandleBounds(result);
	}

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
