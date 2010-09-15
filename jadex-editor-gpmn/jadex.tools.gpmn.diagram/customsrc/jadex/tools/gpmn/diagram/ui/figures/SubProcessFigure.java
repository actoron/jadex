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

import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;
import jadex.tools.gpmn.diagram.ui.ShadowedRoundedRectangleFigure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.PrecisionRectangle;

/**
 * 
 */
public abstract class SubProcessFigure extends ShadowedRoundedRectangleFigure {

	/**
	 * Corner angle to use for Goals with RoundedRectangle
	 */
	public static final int PROCESS_CORNER_ANGLE = 60;
 
	/** Flag to indicate that this goal is linked against a process diagram */
	private boolean isLinked = false;
	
	/** Flag to indicate that this subprocess is internal */
	private boolean internal = false;
	
	/**
	 * Default Constructor
	 */
	public SubProcessFigure()
	{
		super(PROCESS_CORNER_ANGLE, GpmnShapesDefaultSizes
				.getDefaultSize(GpmnElementTypes.SubProcess_2002));
	}

	@Override
	public void paintFigure(Graphics graphics)
	{
		super.paintFigure(graphics);

		// paint static type title in figure
		{
			PrecisionRectangle bounds = GpmnShapePainter.getTopTitleMarkerBounds(getInnerPaintBounds());
			if (isInternal())
				bounds.preciseY = bounds.preciseY - graphics.getFontMetrics().getAscent() / 2.0;
			GpmnShapePainter.paintCenteredString(graphics, bounds, "SubProcess");
		}
		
		if (isInternal())
		{
			PrecisionRectangle bounds = GpmnShapePainter.getTopTitleMarkerBounds(getInnerPaintBounds());
			bounds.preciseY = bounds.preciseY + graphics.getFontMetrics().getAscent() / 2.0;
			GpmnShapePainter.paintCenteredString(graphics, bounds, "(internal)");
		}

		// paint background image
		/*GpmnShapePainter.paintTypeImageInFigure(graphics, super
				.getInnerPaintBounds(), this, GpmnShapePainter
				.getBackgroundImage("SimpleGoalBackground.png"));*/
		
		// add some special markers to shape
		if (isLinked)
		{
			GpmnShapePainter.paintSubProcessMarkerInsideFigure(graphics,
					bounds, this);
		}
		else
		{
			GpmnShapePainter.paintUnsetSubProcessMarkerInsideFigure(graphics,
					bounds, this);
		}
		
	}
    
	// -------- getter / setter ---------
	
	/**
	 * @return the isLinked
	 */
	public boolean isLinked()
	{
		return isLinked;
	}

	/**
	 * @param isLinked
	 *            the isLinked to set
	 */
	public void setLinked(boolean isLinked)
	{
		this.isLinked = isLinked;
		revalidate();
		repaint();
	}

	/**
	 *  Gets the internal flag.
	 *  @return The internal flag.
	 */
	public boolean isInternal()
	{
		return internal;
	}

	/**
	 *  Sets the internal flag.
	 *  @param internal The internal to set flag.
	 */
	public void setInternal(boolean internal)
	{
		this.internal = internal;
		revalidate();
		repaint();
	}

	
}
