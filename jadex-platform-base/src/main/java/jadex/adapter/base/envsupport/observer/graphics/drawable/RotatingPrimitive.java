package jadex.adapter.base.envsupport.observer.graphics.drawable;

import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJ2D;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJOGL;
import jadex.adapter.base.envsupport.observer.gui.SObjectInspector;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

import java.awt.Graphics2D;

import javax.media.opengl.GL;


public abstract class RotatingPrimitive extends AbstractVisual2D implements IDrawable
{
	
	/** The condition deciding if the drawable should be drawn. */
//	protected DrawCondition drawcondition;
	protected IParsedExpression drawcondition;
	
	protected float tRotation;
	
	/**
	 * Initializes the drawable.
	 */
	protected RotatingPrimitive()
	{
		super();
		drawcondition = null;
		tRotation = 0.0f;
	}

	/**
	 * Initializes the drawable.
	 * 
	 * @param position position or position-binding
	 * @param xrotation xrotation or rotation-binding
	 * @param yrotation yrotation or rotation-binding
	 * @param zrotation zrotation or rotation-binding
	 * @param size size or size-binding
	 */
	protected RotatingPrimitive(Object position, Object xrotation, Object yrotation, Object zrotation, Object size, IParsedExpression drawcondition)
	{
		super();
		this.drawcondition = drawcondition;
		if (position != null)
			this.position = position;
		if (xrotation != null)
			this.xRotation = xrotation;
		if (xrotation != null)
			this.xRotation = xrotation;
		if (zrotation != null)
			this.zRotation = zrotation;
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
		IVector1 xrotation = SObjectInspector.getVector1AsDirection(obj, this.xRotation);
		IVector1 yrotation = SObjectInspector.getVector1AsDirection(obj, this.yRotation);
		IVector1 zrotation = SObjectInspector.getVector1AsDirection(obj, this.zRotation);
		IVector2 position = SObjectInspector.getVector2(obj, this.position);
		
		if ((position == null) || (size == null) || (xrotation == null) || (yrotation == null) || (zrotation == null))
		{
			return false;
		}
		
		g.translate(position.getXAsDouble(), position.getYAsDouble());
		g.scale(size.getXAsDouble(), size.getYAsDouble());
		tRotation = (tRotation + 0.1f) % (float)(Math.PI * 2);
		g.scale(Math.cos(xrotation.getAsDouble()), Math.cos(yrotation.getAsDouble()));
		g.rotate(zrotation.getAsDouble());
		
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
		IVector1 xrotation = SObjectInspector.getVector1AsDirection(obj, this.xRotation);
		IVector1 yrotation = SObjectInspector.getVector1AsDirection(obj, this.yRotation);
		IVector1 zrotation = SObjectInspector.getVector1AsDirection(obj, this.zRotation);
		IVector2 position = SObjectInspector.getVector2(obj, this.position);
		
		if ((position == null) || (size == null) || (xrotation == null) || (yrotation == null) || (zrotation == null))
		{
			return false;
		}
		
		gl.glTranslatef(position.getXAsFloat(), position.getYAsFloat(), 0.0f);
		gl.glScalef(size.getXAsFloat(), size.getYAsFloat(), 1.0f);
		gl.glRotated(Math.toDegrees(xrotation.getAsFloat()), 1.0, 0.0, 0.0);
		gl.glRotated(Math.toDegrees(yrotation.getAsFloat()), 0.0, 1.0, 0.0);
		gl.glRotated(Math.toDegrees(zrotation.getAsFloat()), 0.0, 0.0, 1.0);
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
		boolean draw = drawcondition==null;
		if(!draw)
		{
			SimpleValueFetcher fetcher = new SimpleValueFetcher();
			fetcher.setValue("$object", obj);
			draw = ((Boolean)drawcondition.getValue(fetcher)).booleanValue();
		}
		
		if(draw)
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
		boolean draw = drawcondition==null;
		if(!draw)
		{
			SimpleValueFetcher fetcher = new SimpleValueFetcher();
			fetcher.setValue("$object", obj);
			draw = ((Boolean)drawcondition.getValue(fetcher)).booleanValue();
		}

		if(draw)
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
	public void setDrawCondition(IParsedExpression drawcondition)
	{
		this.drawcondition = drawcondition;
	}
}
