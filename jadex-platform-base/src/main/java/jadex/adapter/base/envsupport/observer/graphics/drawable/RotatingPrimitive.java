package jadex.adapter.base.envsupport.observer.graphics.drawable;

import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJ2D;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJOGL;
import jadex.adapter.base.envsupport.observer.gui.SObjectInspector;

import java.awt.Graphics2D;

import javax.media.opengl.GL;


public abstract class RotatingPrimitive extends AbstractVisual2D implements IDrawable
{
	private static final float	PI_2	= (float)(Math.PI / 2.0);
	
	/** The condition deciding if the drawable should be drawn. */
	protected DrawCondition drawcondition;
	
	/**
	 * Initializes the drawable.
	 */
	protected RotatingPrimitive()
	{
		super();
		drawcondition = null;
	}

	/**
	 * Initializes the drawable.
	 * 
	 * @param position position or position-binding
	 * @param rotation rotation or rotation-binding
	 * @param size size or size-binding
	 */
	protected RotatingPrimitive(Object position, Object rotation, Object size, DrawCondition drawcondition)
	{
		super();
		this.drawcondition = drawcondition;
		if (position != null)
			this.position = position;
		if (rotation != null)
			this.rotation = rotation;
		if (size != null)
			this.size = size;
	}

	/**
	 * Sets up the transformation matrix before drawing.
	 * 
	 * @param obj object being drawn
	 * @param g graphics context
	 * @return true, if the setup was successful
	 */
	protected boolean setupMatrix(Object obj, Graphics2D g)
	{
		IVector2 size = SObjectInspector.getVector2(obj, this.size);
		IVector1 rotation = SObjectInspector.getVector1(obj, this.rotation);
		IVector2 position = SObjectInspector.getVector2(obj, this.position);
		
		if ((position == null) || (size == null) || (rotation == null))
		{
			return false;
		}
		
		g.translate(position.getXAsDouble(), position.getYAsDouble());
		g.scale(size.getXAsDouble(), size.getYAsDouble());
		g.rotate(rotation.getAsDouble());
		return true;
	}

	/**
	 * Sets up the transformation matrix before drawing.
	 * 
	 * @param obj object being drawn
	 * @param gl OpenGL context
	 */
	protected boolean setupMatrix(Object obj, GL gl)
	{
		IVector2 size = SObjectInspector.getVector2(obj, this.size);
		IVector1 rotation = SObjectInspector.getVector1asDirection(obj, this.rotation);
		IVector2 position = SObjectInspector.getVector2(obj, this.position);
		
		if ((position == null) || (size == null) || (rotation == null))
		{
			return false;
		}
		
		gl.glTranslatef(position.getXAsFloat(), position.getYAsFloat(), 0.0f);
		gl.glScalef(size.getXAsFloat(), size.getYAsFloat(), 1.0f);
		gl.glRotated(Math.toDegrees(rotation.getAsFloat()), 0.0, 0.0, 1.0);
		return true;
	}
	
	/**
	 * Draws the object to a Java2D viewport
	 * 
	 * @param obj the object being drawn
	 * @param vp the viewport
	 */
	public final void draw(Object obj, ViewportJ2D vp)
	{
		if((drawcondition == null) || drawcondition.testCondition(obj))
			doDraw(obj, vp);
	}

	/**
	 * Draws the object to an OpenGL viewport
	 * 
	 * @param obj the object being drawn
	 * @param vp the viewport
	 */
	public final void draw(Object obj, ViewportJOGL vp)
	{
		if((drawcondition == null) || drawcondition.testCondition(obj))
			doDraw(obj, vp);
	}
	
	/**
	 * Draws the object to a Java2D viewport
	 * 
	 * @param obj the object being drawn
	 * @param vp the viewport
	 */
	public abstract void doDraw(Object obj, ViewportJ2D vp);
	
	/**
	 * Draws the object to an OpenGL viewport
	 * 
	 * @param obj the object being drawn
	 * @param vp the viewport
	 */
	public abstract void doDraw(Object obj, ViewportJOGL vp);
	
	/**
	 * Sets the draw condition.
	 * 
	 * @param drawcondition the draw condition
	 */
	public void setDrawCondition(DrawCondition drawcondition)
	{
		this.drawcondition = drawcondition;
	}
}
