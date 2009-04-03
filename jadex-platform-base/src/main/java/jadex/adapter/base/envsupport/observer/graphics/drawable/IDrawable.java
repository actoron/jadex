package jadex.adapter.base.envsupport.observer.graphics.drawable;

import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJ2D;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJOGL;

import java.awt.Graphics2D;

import javax.media.opengl.GL;


public interface IDrawable
{
	/**
	 * Initializes the object for a Java2D viewport
	 * 
	 * @param vp the viewport
	 * @param g Graphics2D context
	 */
	public void init(ViewportJ2D vp, Graphics2D g);

	/**
	 * Initializes the object for an OpenGL viewport
	 * 
	 * @param vp the viewport
	 * @param gl OpenGL context
	 */
	public void init(ViewportJOGL vp, GL gl);

	/**
	 * Draws the object to a Java2D viewport
	 * 
	 * @param layer the current layer
	 * @param vp the viewport
	 * @param g Graphics2D context
	 */
	public void draw(ViewportJ2D vp, Graphics2D g);

	/**
	 * Draws the object to an OpenGL viewport
	 * 
	 * @param layer the current layer
	 * @param vp the viewport
	 * @param gl OpenGL context
	 */
	public void draw(ViewportJOGL vp, GL gl);

	/**
	 * Sets the position of the object.
	 * 
	 * @param pos new position
	 */
	public void setPosition(IVector2 pos);

	/**
	 * Sets the velocity of the object. Depending on implementation, this may
	 * result in rotation or other graphical features being used.
	 * 
	 * @param velocity new velocity
	 */
	public void setVelocity(IVector2 velocity);

	/**
	 * Sets the size of the drawable on screen.
	 * 
	 * @param size new size
	 */
	public void setSize(IVector2 size);

	/**
	 * Sets the shift away from the centered position.
	 * 
	 * @param shift the shift from the centered position
	 */
	public void setShift(IVector2 shift);
}
