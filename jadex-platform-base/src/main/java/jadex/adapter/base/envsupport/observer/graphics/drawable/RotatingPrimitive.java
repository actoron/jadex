package jadex.adapter.base.envsupport.observer.graphics.drawable;

import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.IVector3;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJ2D;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJOGL;
import jadex.adapter.base.envsupport.observer.gui.SObjectInspector;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.media.opengl.GL;


/**
 *  Base class for rotating objects.
 */
public abstract class RotatingPrimitive extends AbstractVisual2D implements IDrawable
{
	public static final int ABSOLUTE_POSITION = 1;
	public static final int ABSOLUTE_SIZE = 2;
	public static final int ABSOLUTE_ROTATION = 4;
	
	/** Enable DrawableCombiner position */
	protected boolean enableDCPos;
	
	/** Enable DrawableCombiner position */
	protected boolean enableDCSize;
	
	/** Enable DrawableCombiner position */
	protected boolean enableDCRot;
	
	/** The condition deciding if the drawable should be drawn. */
	protected IParsedExpression drawcondition;
	
	/**
	 * Initializes the drawable.
	 */
	protected RotatingPrimitive()
	{
		super();
		enableDCPos = true;
		enableDCSize = true;
		enableDCRot = true;
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
	 */
	protected RotatingPrimitive(Object position, Object rotation, Object size, int absFlags,  IParsedExpression drawcondition)
	{
		super(position, rotation, size);
		enableDCPos = (absFlags & ABSOLUTE_POSITION) == 0;
		enableDCSize = (absFlags & ABSOLUTE_SIZE) == 0;
		enableDCRot = (absFlags & ABSOLUTE_ROTATION) == 0;
		this.drawcondition = drawcondition;
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
		IVector2 size = getSize(obj);
		IVector3 rot = getRotation(obj);
//		IVector1 xrotation = SObjectInspector.getVector1AsDirection(obj, rot.getXAsDouble());
//		IVector1 yrotation = SObjectInspector.getVector1AsDirection(obj, this.yRotatio);
//		IVector1 zrotation = SObjectInspector.getVector1AsDirection(obj, this.zRotation);
		IVector2 position = getPosition(obj);
		
		if((position == null) || (size == null) || (rot == null))
		{
			return false;
		}
		
		g.translate(position.getXAsDouble(), position.getYAsDouble());
		g.scale(size.getXAsDouble(), size.getYAsDouble());
		g.scale(Math.cos(rot.getXAsDouble()), Math.cos(rot.getYAsDouble()));
		g.rotate(rot.getZAsDouble());
		
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
		IVector2 size = getSize(obj);
		IVector3 rot = getRotation(obj);
//		IVector1 xrotation = SObjectInspector.getVector1AsDirection(obj, this.xRotation);
//		IVector1 yrotation = SObjectInspector.getVector1AsDirection(obj, this.yRotation);
//		IVector1 zrotation = SObjectInspector.getVector1AsDirection(obj, this.zRotation);
		IVector2 position = getPosition(obj);
		
		if((position == null) || (size == null) || (rot == null))
		{
			return false;
		}
		
		gl.glTranslatef(position.getXAsFloat(), position.getYAsFloat(), 0.0f);
		gl.glScalef(size.getXAsFloat(), size.getYAsFloat(), 1.0f);
		gl.glRotated(Math.toDegrees(rot.getXAsFloat()), 1.0, 0.0, 0.0);
		gl.glRotated(Math.toDegrees(rot.getYAsFloat()), 0.0, 1.0, 0.0);
		gl.glRotated(Math.toDegrees(rot.getZAsFloat()), 0.0, 0.0, 1.0);
		return true;
	}
	
	/**
	 * Draws the object to a Java2D viewport
	 * 
	 * @param dc the DrawableCombiner drawing the object
	 * @param obj the object being drawn
	 * @param vp the viewport
	 */
	public final void draw(DrawableCombiner dc, Object obj, ViewportJ2D vp)
	{
		boolean draw = drawcondition==null;
		if(!draw)
		{
			SimpleValueFetcher fetcher = new SimpleValueFetcher();
			fetcher.setValue("$object", obj);
			draw = ((Boolean)drawcondition.getValue(fetcher)).booleanValue();
		}
		
		if(draw)
		{
			Graphics2D g = vp.getContext();
			AffineTransform t = g.getTransform();
			if (!dc.setupMatrix(obj, g, enableDCPos, enableDCSize, enableDCRot))
				return;
			doDraw(obj, vp);
			g.setTransform(t);
		}
	}

	/**
	 * Draws the object to an OpenGL viewport
	 * 
	 * @param dc the DrawableCombiner drawing the object
	 * @param obj the object being drawn
	 * @param vp the viewport
	 */
	public final void draw(DrawableCombiner dc, Object obj, ViewportJOGL vp)
	{
		boolean draw = drawcondition==null;
		if(!draw)
		{
			SimpleValueFetcher fetcher = new SimpleValueFetcher();
			fetcher.setValue("$object", obj);
			draw = ((Boolean)drawcondition.getValue(fetcher)).booleanValue();
		}

		if(draw)
		{
			GL gl = vp.getContext();
			gl.glPushMatrix();
			if (!dc.setupMatrix(obj, gl, enableDCPos, enableDCSize, enableDCRot))
				return;
			doDraw(obj, vp);
			gl.glPopMatrix();
		}
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
	 * Enables using absolute positioning.
	 * 
	 * @param enable true, to use the drawable's value as an absolute.
	 */
	public void enableAbsolutePosition(boolean enable)
	{
		enableDCPos = !enable;
	}
	
	/** 
	 * Enables using absolute scaling.
	 * 
	 * @param enable true, to use the drawable's value as an absolute.
	 */
	public void enableAbsoluteSize(boolean enable)
	{
		enableDCSize = !enable;
	}
	
	/** 
	 * Enables using absolute rotation.
	 * 
	 * @param enable true, to use the drawable's value as an absolute.
	 */
	public void enableAbsoluteRotation(boolean enable)
	{
		enableDCRot = !enable;
	}
	
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
