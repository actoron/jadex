package jadex.adapter.base.envsupport.observer.graphics.drawable;

import jadex.adapter.base.envsupport.observer.graphics.ViewportJ2D;
import jadex.javaparser.IParsedExpression;

import java.awt.Color;
import java.awt.geom.Ellipse2D;


public class Ellipse extends RegularPolygon
{
	private final static int OPENGL_NUM_VERTICES = 36;
	
	/**
	 * Generates a new Circle.
	 * 
	 * @param position position or position-binding
	 * @param rotation rotation or rotation-binding
	 * @param size size or size-binding
	 * @param absFlags flags for setting position, size and rotation as absolutes
	 * @param c the drawable's color
	 * @param vertices number of vertices (corners)
	 */
	public Ellipse(Object position, Object rotation, Object size, int absFlags, Color c, IParsedExpression drawcondition)
	{
		super(position, rotation, size, absFlags, c, OPENGL_NUM_VERTICES, drawcondition);
	}
	
	public void init(ViewportJ2D vp)
	{
		Ellipse2D.Double e = new Ellipse2D.Double();
		e.x = -0.5;
		e.y = -0.5;
		e.width = 1.0;
		e.height = 1.0;
		shape_ = e;
	}
}
