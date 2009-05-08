package jadex.adapter.base.envsupport.observer.graphics.drawable;

import jadex.adapter.base.envsupport.observer.graphics.ViewportJ2D;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJOGL;


public interface IDrawable
{
	/**
	 * Initializes the object for a Java2D viewport
	 * 
	 * @param vp the viewport
	 * @param g Graphics2D context
	 */
	public void init(ViewportJ2D vp);

	/**
	 * Initializes the object for an OpenGL viewport
	 * 
	 * @param vp the viewport
	 * @param gl OpenGL context
	 */
	public void init(ViewportJOGL vp);

	/**
	 * Draws the object to a Java2D viewport
	 * 
	 * @param obj the object being drawn
	 * @param vp the viewport
	 */
	public void draw(Object obj, ViewportJ2D vp);

	/**
	 * Draws the object to an OpenGL viewport
	 * 
	 * @param obj the object being drawn
	 * @param vp the viewport
	 */
	public void draw(Object obj, ViewportJOGL vp);
}
