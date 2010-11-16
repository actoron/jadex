/**
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id$
 */
package jadex.tools.gpmn.diagram.ui;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

public abstract class AbstractGpmnFigure extends Shape
{

	public static final Dimension DEFAULT_MIN_SIZE = new Dimension(120,80);

	/**
	 * Default Constructor
	 */
	public AbstractGpmnFigure()
	{
		this(DEFAULT_MIN_SIZE);
	}
	
	/**
	 * Constructor with default size specified
	 */
	public AbstractGpmnFigure(Dimension defSize)
	{
		super.setMinimumSize(defSize);
	}
	
//	/**
//	 * Override to respect minimum figure size
//	 */
//	@Override
//	public void setSize(int w, int h) 
//	{
//		Dimension figureMinSize = this.getMinimumSize();
//		int minWidth = w >= figureMinSize.width ? w : figureMinSize.width;
//		int minHeight = h >= figureMinSize.height ? h : figureMinSize.height;
//		super.setSize(minWidth, minHeight);
//	}

	/**
	 * Calculate the shape outline rectangle
	 * @return Rectangle to paint the shape outline in
	 */
	protected Rectangle getOutlineBounds()
	{
		Rectangle f = Rectangle.SINGLETON.getCopy();
		Rectangle r = getBounds().getCopy().crop(getBorder().getInsets(this));
		f.x = r.x + lineWidth / 2;
		f.y = r.y + lineWidth / 2;
		f.width = r.width - lineWidth;
		f.height = r.height - lineWidth;
		return f;
	}
	
	/**
	 * Calculate the inner figure rectangle without the scheme border shape
	 * 
	 * @return inner Rectangle without the BorderShaddow
	 */
	protected Rectangle getInnerPaintBounds()
	{
		Rectangle rect = getBounds().getCopy()
				.crop(getBorder().getInsets(this));
		return rect;
	}

	@Override
	public void paintFigure(Graphics graphics)
	{
		this.paintShadow(graphics);
		super.paintFigure(graphics);
	}
    
	/**
	 * Paint the graphic with a shadow
	 * @param graphics
	 */
	protected void paintShadow(Graphics graphics)
	{
		super.paintBorder(graphics);
	}
	
	
	

	@Override
	protected void paintBorder(Graphics graphics)
	{
		// Nothing to do.
		// The border is painted with the shadow
	}
	
	/**
	 * Paints this Figure and its children. Overridden to have the border paint
	 * before the children of the shape.
	 * 
	 * @param graphics
	 *            The Graphics object used for painting
	 * @see #paintFigure(Graphics)
	 * @see #paintClientArea(Graphics)
	 * @see #paintBorder(Graphics)
	 */
	@Override
	public void paint(Graphics graphics)
	{
		if (getLocalBackgroundColor() != null)
			graphics.setBackgroundColor(getLocalBackgroundColor());
		if (getLocalForegroundColor() != null)
			graphics.setForegroundColor(getLocalForegroundColor());
		if (this.getFont() != null)
			graphics.setFont(this.getFont());

		graphics.pushState();
		try
		{
			paintBorder(graphics);
			paintFigure(graphics);
			graphics.restoreState();
			paintClientArea(graphics);
		}
		finally
		{
			graphics.popState();
		}
	}
}
