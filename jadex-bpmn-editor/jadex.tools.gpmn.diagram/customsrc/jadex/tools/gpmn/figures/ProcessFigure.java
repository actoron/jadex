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
 * Jul 12, 2006     hmalphettes         Created
 **/
package jadex.tools.gpmn.figures;

import jadex.tools.gpmn.diagram.ui.RoundedSchemeBorder;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.handles.HandleBounds;

/**
 * Transparent figure
 * 
 * @author hmalphettes
 * @author <a href="http://www.intalio.com">&copy; Intalio, Inc.</a>
 */
public class ProcessFigure extends RoundedRectangle
implements HandleBounds {

    public ProcessFigure() {
        this.setBorder(new RoundedSchemeBorder(8));
    }
    
    private boolean _isLoop;
    private boolean _isTransaction;
    
    public boolean isLoop() {
        return _isLoop;
    }

    public void setIsLoop(boolean isLoop) {
        _isLoop = isLoop;
    }

    public boolean isTransaction() {
        return _isTransaction;
    }

    public void setIsTransaction(boolean isTransaction) {
        _isTransaction = isTransaction;
    }

    /**
     * Returns the Rectangle around which handles are to be placed.  The Rectangle should be
     * in the same coordinate system as the figure itself.
     * @return The rectangle used for handles
     */
    public Rectangle getHandleBounds() 
    {
        Rectangle r = getBounds().getCopy().crop(getBorder().getInsets(this));        
        return r;
    }

    /**
     * @see Shape#fillShape(Graphics)
     */
    protected void fillShape(Graphics graphics) {
        graphics.fillRoundRectangle(getHandleBounds(), corner.width,
                corner.height);
    }

    /**
     * @see Shape#outlineShape(Graphics)
     */
    protected void outlineShape(Graphics graphics) {
        Rectangle f = Rectangle.SINGLETON;
        Rectangle r = getHandleBounds();
        f.x = r.x + lineWidth / 2;
        f.y = r.y + lineWidth / 2;
        f.width = r.width - lineWidth;
        f.height = r.height - lineWidth;
        graphics.drawRoundRectangle(f, corner.width, corner.height);
    }
    
    /**
     * Paints this Figure and its children.
     * Overridden to have the border paint before the children of the shape.
     * 
     * @param graphics The Graphics object used for painting
     * @see #paintFigure(Graphics)
     * @see #paintClientArea(Graphics)
     * @see #paintBorder(Graphics)
     */
    public void paint(Graphics graphics) {
        if (getLocalBackgroundColor() != null)
            graphics.setBackgroundColor(getLocalBackgroundColor());
        if (getLocalForegroundColor() != null)
            graphics.setForegroundColor(getLocalForegroundColor());
        if (font != null)
            graphics.setFont(font);

        graphics.pushState();
        try {
            paintBorder(graphics);
            paintFigure(graphics);
            graphics.restoreState();
            paintClientArea(graphics);
            
        } finally {
            graphics.popState();
        }
    }
}
