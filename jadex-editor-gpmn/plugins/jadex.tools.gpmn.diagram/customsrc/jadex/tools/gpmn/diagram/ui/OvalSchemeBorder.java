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
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * This scheme border draws a border under an oval shape.
 */
public class OvalSchemeBorder extends RoundedSchemeBorder
{

	public OvalSchemeBorder()
	{
		super(0);
	}

	@Override
	protected void fillShadow(IFigure fig, Graphics graphics, Insets insets)
	{
		Rectangle rect = fig.getBounds().getCopy().translate(2, 2).resize(-4, -4);

		graphics.fillOval(rect/*.getCopy()*/.crop(insets));
	}
}
