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
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gmf.runtime.draw2d.ui.figures.IOvalAnchorableFigure;

public class ShadowedOvalFigure extends AbstractGpmnFigure implements IOvalAnchorableFigure
{

	/**
	 * Default Constructor
	 */
	public ShadowedOvalFigure()
	{
		this(DEFAULT_MIN_SIZE);
	}
	
	/**
	 * Constructor with corner angle specified
	 */
	public ShadowedOvalFigure(Dimension minSize)
	{
		super(minSize);
		
		setBorder(new OvalSchemeBorder()
		{
			//@Override
			//public void paint(IFigure fig, Graphics graphics, Insets insets)
			//{
			//	super.paint(fig, graphics, insets);
			//}
		});
	}

	@Override
	protected void fillShape(Graphics graphics)
	{
		graphics.fillOval(super.getInnerPaintBounds());
	}

	@Override
	protected void outlineShape(Graphics graphics)
	{
		graphics.drawOval(super.getOutlineBounds());
	}
	
	@Override
	public Rectangle getOvalBounds()
	{
		return getOutlineBounds();
	}

}
