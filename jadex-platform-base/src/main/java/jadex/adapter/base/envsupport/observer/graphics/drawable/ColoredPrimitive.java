package jadex.adapter.base.envsupport.observer.graphics.drawable;

import jadex.javaparser.IParsedExpression;

import java.awt.Color;


public abstract class ColoredPrimitive extends RotatingPrimitive
{
	/** Color of the primitive. */
	protected Color		c_;

	/** OpenGL color cache. */
	protected float[]	oglColor_;
	
	/**
	 * Initializes the drawable.
	 */
	protected ColoredPrimitive()
	{
		super();
		setColor(Color.WHITE);
	}

	/**
	 * Initializes the drawable.
	 * 
	 * @param position position or position-binding
	 * @param xrotation xrotation or rotation-binding
	 * @param yrotation yrotation or rotation-binding
	 * @param zrotation zrotation or rotation-binding
	 * @param size size or size-binding
	 * @param absFlags flags for setting position, size and rotation as absolutes
	 * @param c the drawable's color
	 */
	protected ColoredPrimitive(Object position, Object rotation, Object size, int absFlags, Color c, IParsedExpression drawcondition)
	{
		super(position, rotation, size, absFlags, drawcondition);
		if (c == null)
			c = Color.WHITE;
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
