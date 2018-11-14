package jadex.extension.envsupport.observer.graphics.opengl;

import javax.media.opengl.GL;

import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.IVector3;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector3Double;
import jadex.extension.envsupport.observer.graphics.IViewport;
import jadex.extension.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.extension.envsupport.observer.graphics.drawable.Primitive;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

public abstract class AbstractGLRenderer implements IGLRenderer
{
	/**
	 * Prepares the object for rendering to a JOGL viewport
	 * 
	 * @param dc the DrawableCombiner drawing the object
	 * @param primitive the primitive being drawn
	 * @param obj the object being drawn
	 * @param vp the viewport
	 */
	public final void prepareAndExecuteDraw(DrawableCombiner dc, Primitive primitive, Object obj, ViewportJOGL vp)
	{
		IParsedExpression drawcondition = primitive.getDrawCondition();
		boolean draw = drawcondition==null;
		if(!draw)
		{
			SimpleValueFetcher fetcher = new SimpleValueFetcher(vp.getPerspective().getObserverCenter().getSpace().getFetcher());
			fetcher.setValue("$object", obj);
			fetcher.setValue("$perspective", vp.getPerspective());
//			fetcher.setValue("$space", vp.getPerspective().getObserverCenter().getSpace());
			draw = ((Boolean)drawcondition.getValue(fetcher)).booleanValue();
		}

		if(draw)
		{
			GL gl = vp.getContext();
			gl.glPushMatrix();
			if(!setupDCMatrix(dc, obj, primitive.isRelativePosition(), primitive.isRelativeSize(), primitive.isRelativeRotation(), vp))
				return;
			draw(dc, primitive, obj, vp);
			gl.glPopMatrix();
		}
	}
	
	/**
	 * Sets up the transformation matrix before drawing.
	 * 
	 * @param obj object being drawn
	 * @param g graphics context
	 * @return true, if the setup was successful
	 */
	protected boolean setupMatrix(DrawableCombiner dc, Primitive primitive, Object obj, GL gl, IViewport vp)
	{
		IVector2 size = (IVector2)dc.getBoundValue(obj, primitive.getSize(), vp);
		IVector3 rot = (IVector3)dc.getBoundValue(obj, primitive.getRotation(), vp);
		IVector2 position = (IVector2)dc.getBoundValue(obj, primitive.getPosition(), vp);
		
		if((position == null) || (size == null) || (rot == null))
		{
			return false;
		}
		
		gl.glTranslatef(position.getXAsFloat(), position.getYAsFloat(), 0.0f);
		gl.glRotated(Math.toDegrees(rot.getXAsFloat()), 1.0, 0.0, 0.0);
		gl.glRotated(Math.toDegrees(rot.getYAsFloat()), 0.0, 1.0, 0.0);
		gl.glRotated(Math.toDegrees(rot.getZAsFloat()), 0.0, 0.0, 1.0);
		gl.glScalef(size.getXAsFloat(), size.getYAsFloat(), 1.0f);
		
		return true;
	}
	
	/**
	 * Sets the basic matrix for the combiner, call can be skipped if alternative draw method is required.
	 * 
	 * @param obj object being drawn
	 * @param gl the viewport context
	 * @param enablePos enables position setup
	 * @param enableSize enables size setup
	 * @param enableRot enables rotation setup
	 */
	public static final boolean setupDCMatrix(DrawableCombiner dc, Object obj, boolean enablePos, boolean enableSize, boolean enableRot, ViewportJOGL vp)
	{
		GL gl = vp.getContext();
		if(enablePos)
		{
			IVector2 position = (IVector2)dc.getBoundValue(obj, dc.getPosition(), vp);
			if(position==null)
				return false;
			gl.glTranslatef(position.getXAsFloat(), position.getYAsFloat(), 0.0f);
		}
		
		if(enableSize)
		{
			IVector2 size = (IVector2)dc.getBoundValue(obj, dc.getSize(), vp);
			if(size==null)
				size = new Vector2Double(1,0);
			gl.glScalef(size.getXAsFloat(), size.getYAsFloat(), 1.0f);
		}
		
		if(enableRot)
		{
			IVector3 rot = (IVector3)dc.getBoundValue(obj, dc.getRotation(), vp);
			if(rot==null)
				rot = Vector3Double.ZERO.copy();
			gl.glRotated(Math.toDegrees(rot.getXAsFloat()), 1.0, 0.0, 0.0);
			gl.glRotated(Math.toDegrees(rot.getYAsFloat()), 0.0, 1.0, 0.0);
			gl.glRotated(Math.toDegrees(rot.getZAsFloat()), 0.0, 0.0, 1.0);
		}
		
		return true;
	}
	
	/**
	 * Draws the primitive.
	 * @param dc the DrawableCombiner drawing the object
	 * @param primitive the primitive being drawn
	 * @param obj the object being drawn
	 * @param vp the viewport
	 */
	public abstract void draw(DrawableCombiner dc, Primitive primitive, Object obj, ViewportJOGL vp);
}
