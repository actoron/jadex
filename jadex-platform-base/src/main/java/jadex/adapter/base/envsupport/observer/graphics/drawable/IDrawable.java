package jadex.adapter.base.envsupport.observer.graphics.drawable;

import jadex.adapter.base.envsupport.math.IVector1;
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

	/**
	 * Sets the position of the visual to a fixed position.
	 * 
	 * @param pos fixed position
	 */
	public void setPosition(IVector2 pos);

	/**
	 * Sets the rotation of the visual to a fixed rotation.
	 * 
	 * @param rotation the fixed rotation
	 */
	public void setRotation(IVector1 rotation);

	/**
	 * Sets the size (scale) of the visual to a fixed size.
	 * 
	 * @param size fixed size
	 */
	public void setSize(IVector2 size);
	
	/**
	 * Sets the draw condition.
	 * 
	 * @param drawcondition the draw condition
	 */
	public void setDrawCondition(DrawCondition drawcondition);
	
	/**
	 * Binds the position of the visual to an object property.
	 * 
	 * @param propId the property ID
	 */
	public void bindPosition(String propId);
	
	/**
	 * Binds the rotation of the visual to an object property.
	 * 
	 * @param propId the property ID
	 */
	public void bindRotation(String propId);
	
	/**
	 * Binds the size of the visual to an object property.
	 * 
	 * @param propId the property ID
	 */
	public void bindSize(String propId);
}
