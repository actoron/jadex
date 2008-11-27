package jadex.bdi.planlib.simsupport.common.graphics.drawable;

import java.awt.Color;

public abstract class RotatingColoredPrimitive extends RotatingPrimitive
{
    /** Color of the object
     */
    protected Color c_;
    
    /** OpenGL color cache.
	 */
	protected float[] oglColor_;
    
    /** Sets the color of the object.
     *
     *  @param c new color
     */
    public synchronized void setColor(Color c)
    {
        c_ = c;
        oglColor_ = new float[4];
		oglColor_[0] = c_.getRed() / 255.0f;
		oglColor_[1] = c_.getGreen() / 255.0f;
		oglColor_[2] = c_.getBlue() / 255.0f;
		oglColor_[3] = c_.getAlpha() / 255.0f;
    }
}
