package jadex.extension.envsupport.observer.graphics.java2d;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.IVector3;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector3Double;
import jadex.extension.envsupport.observer.graphics.IViewport;
import jadex.extension.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.extension.envsupport.observer.graphics.drawable.Primitive;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

/**
 * 
 */
public abstract class AbstractJ2DRenderer implements IJ2DRenderer
{
	/**
	 * Prepares the object for rendering to a Java2D viewport
	 * 
	 * @param dc the DrawableCombiner drawing the object
	 * @param primitive the primitive being drawn
	 * @param obj the object being drawn
	 * @param vp the viewport
	 */
	public final void prepareAndExecuteDraw(DrawableCombiner dc, Primitive primitive, Object obj, ViewportJ2D vp)
	{
		IParsedExpression drawcondition = primitive.getDrawCondition();
		boolean draw = drawcondition==null;
		if(!draw)
		{
			SimpleValueFetcher fetcher = new SimpleValueFetcher(vp.getPerspective().getObserverCenter().getSpace().getFetcher());
			fetcher.setValue("$object", obj);
			fetcher.setValue("$perspective", vp.getPerspective());

			draw = ((Boolean)drawcondition.getValue(fetcher)).booleanValue();
		}
		
		if(draw)
		{
			Graphics2D g = vp.getContext();
			AffineTransform t = g.getTransform();
			if(!setupDCMatrix(dc, obj, primitive.isRelativePosition(), primitive.isRelativeSize(), primitive.isRelativeRotation(), vp))
				return;
			draw(dc, primitive, obj, vp);
			g.setTransform(t);
		}
	}
	
	/**
	 * Sets the basic matrix for the combiner, call can be skipped if alternative draw method is required.
	 * 
	 * @param obj object being drawn
	 * @param g the viewport context
	 * @param enablePos enables position setup
	 * @param enableSize enables size setup
	 * @param enableRot enables rotation setup
	 */
	public static final boolean setupDCMatrix(DrawableCombiner dc, Object obj, boolean enablePos, boolean enableSize, boolean enableRot, ViewportJ2D vp)
	{
		Graphics2D g = vp.getContext();
		if(enablePos)
		{
			IVector2 position = (IVector2)dc.getBoundValue(obj, dc.getPosition(), vp);
			if(position==null)
				return false;
			g.translate(position.getXAsDouble(), position.getYAsDouble());
		}
		
		if(enableSize)
		{
			IVector2 size = (IVector2)dc.getBoundValue(obj, dc.getSize(), vp);
			if(size==null)
				size = new Vector2Double(1,0);
			g.scale(size.getXAsDouble(), size.getYAsDouble());
		}
		
		if(enableRot)
		{
			IVector3 rot = (IVector3)dc.getBoundValue(obj, dc.getRotation(), vp);
			if(rot==null)
				rot = Vector3Double.ZERO.copy();
			g.scale(Math.cos(rot.getYAsDouble()), Math.cos(rot.getXAsDouble()));
			g.rotate(rot.getZAsDouble());
		}
		
		return true;
	}
	
	/**
	 * Sets up the transformation matrix before drawing.
	 * 
	 * @param obj object being drawn
	 * @param g graphics context
	 * @return true, if the setup was successful
	 */
	protected boolean setupMatrix(DrawableCombiner dc, Primitive primitive, Object obj, Graphics2D g, IViewport vp)
	{
		IVector2 size = (IVector2)dc.getBoundValue(obj, primitive.getSize(), vp);
		IVector3 rot = (IVector3)dc.getBoundValue(obj, primitive.getRotation(), vp);
		IVector2 position = (IVector2)dc.getBoundValue(obj, primitive.getPosition(), vp);
		
		if((position == null) || (size == null) || (rot == null))
		{
			return false;
		}
		
		g.translate(position.getXAsDouble(), position.getYAsDouble());
		g.rotate(rot.getZAsDouble());
		g.scale(Math.cos(rot.getXAsDouble()), Math.cos(rot.getYAsDouble()));
		g.scale(size.getXAsDouble(), size.getYAsDouble());
		
		return true;
	}
	
	/**
	 * Draws the primitive.
	 * @param dc the DrawableCombiner drawing the object
	 * @param primitive the primitive being drawn
	 * @param obj the object being drawn
	 * @param vp the viewport
	 */
	public abstract void draw(DrawableCombiner dc, Primitive primitive, Object obj, ViewportJ2D vp);
}
