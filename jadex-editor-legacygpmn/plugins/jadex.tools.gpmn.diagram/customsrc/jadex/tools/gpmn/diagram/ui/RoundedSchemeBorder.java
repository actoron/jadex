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

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

/**
 * Scheme border for rounded rectangle shapes.
 */
public class RoundedSchemeBorder extends AbstractBorder {

	
    public static final Insets INSETS = new Insets(0, 0, 4, 4);
    
    /** The corner angle used for this border */
    protected int cornerAngle;

	/**
	 * Default Constructor
	 * 
	 * @param cornerAngle
	 *            the same corner angle used for the primary shape on which this
	 *            border is applied.
	 */
	public RoundedSchemeBorder(int cornerAngle)
	{
		super();
		this.cornerAngle = cornerAngle;
	}

	public void paint(IFigure fig, Graphics graphics, Insets insets) {
    	int alpha = 70;
//        int alpha = GpmnDiagramEditorPlugin.getInstance().getPreferenceStore().
//            getInt(DiagramPreferenceInitializer.PREF_SHOW_SHADOWS_TRANSPARENCY);
        if (alpha <= 0) {
            return;
        }
        Color c = graphics.getBackgroundColor();
        int oriAlpha = graphics.getAlpha();
        
        graphics.setAlpha(alpha);
        graphics.setBackgroundColor(ColorConstants.black);
        fillShadow(fig, graphics, insets);
        
        graphics.setAlpha(oriAlpha);
        graphics.setBackgroundColor(c);
        return;

    }
    
	protected void fillShadow(IFigure fig, Graphics graphics, Insets insets)
	{
		Rectangle rect = null;
		rect = fig.getBounds().getCopy().translate(3, 3).resize(-4, -4);
		
		graphics.fillRoundRectangle(rect.crop(insets), cornerAngle-1, cornerAngle-1);
	}

	public Insets getInsets(IFigure figure)
	{
		return INSETS;
	}
    
}
