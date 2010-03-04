/******************************************************************************
 * Copyright (c) 2006-2008, Intalio Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Intalio Inc. - initial API and implementation
 *******************************************************************************/

/** 
 * Date             Author           Changes 
 * 17 May 2008      hmalphettes      Created 
 **/
package jadex.tools.gpmn.figures;

import java.util.Collection;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Shape used during a move or resize. It is able to display overlapping areas.
 * 
 * @author hmalphettes
 */
public class FeedbackShape extends RectangleFigure
{

	public FeedbackShape()
	{
		setLineStyle(Graphics.LINE_DOT);
		setBackgroundColor(ColorConstants.lightBlue);
	}

	/** holds the bounds of the figures overlapping with the current bounds */
	private Collection<Rectangle> _overlappingBounds;

	@Override
	protected void fillShape(Graphics graphics)
	{
		graphics.setAlpha(127);
		graphics.setBackgroundColor(getBackgroundColor());
		super.fillShape(graphics);
		if (_overlappingBounds != null)
		{
			graphics.setBackgroundColor(ColorConstants.red);
			// graphics.setXORMode(false);
			for (Rectangle r : _overlappingBounds)
			{
				graphics.fillRectangle(r);
			}
		}
	}

	/**
	 * @param overlappingBounds
	 *            The collection of overlapping bounds or null
	 */
	public void setOverlappingBounds(Collection<Rectangle> overlappingBounds)
	{
		_overlappingBounds = overlappingBounds;
	}

}
