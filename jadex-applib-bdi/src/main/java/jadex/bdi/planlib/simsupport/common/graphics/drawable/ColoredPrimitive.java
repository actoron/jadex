package jadex.bdi.planlib.simsupport.common.graphics.drawable;

import java.awt.Color;

public abstract class ColoredPrimitive extends RotatingPrimitive
{
	/** Color of the Polygon.
	 */
	protected Color c_;
	
	/** OpenGL color cache.
	 */
	protected float[] oglColor_;
	
	/** Gets the color of the drawable
	 * 
	 *  @return color of the drawable
	 */
	public Color getColor()
	{
		return c_;
	}
	
	/** Sets a new color for the drawable
	 * 
	 *  @param c new color
	 */
	public void setColor(Color c)
	{
		c_ = c;
		oglColor_ = new float[4];
		oglColor_[0] = c_.getRed() / 255.0f;
		oglColor_[1] = c_.getGreen() / 255.0f;
		oglColor_[2] = c_.getBlue() / 255.0f;
		oglColor_[3] = c_.getAlpha() / 255.0f;
	}
}
