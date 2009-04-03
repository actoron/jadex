package jadex.adapter.base.envsupport.observer.graphics.drawable;


import jadex.adapter.base.envsupport.math.IVector2;

import java.awt.Color;


public abstract class ColoredPrimitive extends RotatingPrimitive
{
	/** Color of the primitive. */
	protected Color		c_;

	/** OpenGL color cache. */
	protected float[]	oglColor_;

	/**
	 * Initializes the drawable.
	 * 
	 * @param size initial size
	 * @param shift shift from the centered position using scale(1.0, 1.0)
	 * @param rotating if true, the resulting drawable will rotate depending on
	 *        the velocity
	 * @param c the drawable's color
	 */
	protected ColoredPrimitive(IVector2 size, IVector2 shift, boolean rotating,
			Color c)
	{
		super(size, shift, rotating);
		setColor(c);
	}

	/**
	 * Gets the color of the drawable
	 * 
	 * @return color of the drawable
	 */
	public Color getColor()
	{
		return c_;
	}

	/**
	 * Sets a new color for the drawable
	 * 
	 * @param c new color
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
