package jadex.adapter.base.envsupport.observer.graphics.drawable;

import jadex.javaparser.IParsedExpression;

import java.awt.Color;


public abstract class ColoredPrimitive extends RotatingPrimitive
{
	/** Color or Color binding of the primitive. */
	protected Object	color_;
	
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
	 * @param c the drawable's color or color binding
	 */
	protected ColoredPrimitive(Object position, Object rotation, Object size, int absFlags, Object c, IParsedExpression drawcondition)
	{
		super(position, rotation, size, absFlags, drawcondition);
		if (c == null)
			c = Color.WHITE;
		setColor(c);
	}

	/**
	 * Gets the color or color binding of the drawable
	 * 
	 * @return color or color binding of the drawable
	 */
	public Object getColor()
	{
		return color_;
	}

	/**
	 * Sets a new color or binding for the drawable
	 * 
	 * @param c new color or binding
	 */
	public void setColor(Object c)
	{
		color_ = c;
	}
}
