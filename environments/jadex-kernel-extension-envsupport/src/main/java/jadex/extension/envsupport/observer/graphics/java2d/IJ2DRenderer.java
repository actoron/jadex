package jadex.extension.envsupport.observer.graphics.java2d;

import jadex.extension.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.extension.envsupport.observer.graphics.drawable.Primitive;

public interface IJ2DRenderer
{
	/**
	 * Prepares the object for rendering to a Java2D viewport
	 * 
	 * @param dc the DrawableCombiner drawing the object
	 * @param primitive the primitive being drawn
	 * @param obj the object being drawn
	 * @param vp the viewport
	 */
	public abstract void prepareAndExecuteDraw(DrawableCombiner dc, Primitive primitive, Object obj, ViewportJ2D vp);
	
	/**
	 * Draws the primitive.
	 * @param dc the DrawableCombiner drawing the object
	 * @param primitive the primitive being drawn
	 * @param obj the object being drawn
	 * @param vp the viewport
	 */
	public abstract void draw(DrawableCombiner dc, Primitive primitive, Object obj, ViewportJ2D vp);
}
