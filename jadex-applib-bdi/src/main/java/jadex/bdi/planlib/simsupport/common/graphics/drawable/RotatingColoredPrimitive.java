package jadex.bdi.planlib.simsupport.common.graphics.drawable;

import java.awt.Color;

public abstract class RotatingColoredPrimitive extends RotatingPrimitive
{
    /** Color of the object
     */
    protected Color c_;
    
    /** Sets the color of the object.
     *
     *  @param c new color
     */
    public synchronized void setColor(Color c)
    {
        c_ = c;
    }
}
