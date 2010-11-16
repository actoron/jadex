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

public class ShadowedRoundedRectangleFigure extends AbstractGpmnFigure
{
	
	public static final int DEFAULT_CORNER_ANGLE = 8;
	
	
	/** The corner angle used for this figure */
	protected int cornerAngle;
 
	/**
	 * Default Constructor
	 */
	public ShadowedRoundedRectangleFigure()
	{
		this(DEFAULT_CORNER_ANGLE, DEFAULT_MIN_SIZE);
	}
	
	/**
	 * Constructor with corner angle specified
	 */
	public ShadowedRoundedRectangleFigure(int cornerAngle,  Dimension defSize)
	{
		super(defSize);
		
		this.cornerAngle = cornerAngle;
		
		setBorder(new RoundedSchemeBorder(cornerAngle)
		{
			//@Override
			//public void paint(IFigure fig, Graphics graphics, Insets insets)
			//{
			//	super.paint(fig, graphics, insets);
			//}
		});
	}
	
	public int getCornerAngle()
	{
		return cornerAngle;
	}
	
	@Override
	protected void fillShape(Graphics graphics)
	{
		graphics.fillRoundRectangle(super.getInnerPaintBounds(), cornerAngle + 1, cornerAngle + 1);
	}

	@Override
	protected void outlineShape(Graphics graphics)
	{
		graphics.drawRoundRectangle(super.getOutlineBounds(), cornerAngle, cornerAngle);
	}

}
