package jadex.bdi.planlib.simsupport.common.graphics.drawable;

import jadex.bdi.planlib.simsupport.common.graphics.ViewportJ2D;
import jadex.bdi.planlib.simsupport.common.graphics.ViewportJOGL;
import jadex.bdi.planlib.simsupport.common.math.IVector2;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL;

/** This drawable combines multiple drawables into a single drawable object.
 */
public class DrawableCombiner implements IDrawable
{
	/** The drawables.
	 */
	private List drawables_;
	
	/** Creates a new DrawableCombiner.
	 */
	public DrawableCombiner()
	{
		drawables_ = new ArrayList();
	}
	
	/** Adds a drawable to the combiner.
	 * 
	 *  @param d the drawable
	 */
	public void addDrawable(IDrawable d)
	{
		drawables_.add(d);
	}
	
	/** Removes a drawable from the combiner.
	 * 
	 * @param d the drawable
	 */
	public void removeDrawable(IDrawable d)
	{
		drawables_.remove(d);
	}
	
	/** Initializes all objects for a Java2D viewport
	 * 
	 *  @param vp the viewport
	 *  @param g Graphics2D context 
	 */
	public void init(ViewportJ2D vp, Graphics2D g)
	{
		for (Iterator it = drawables_.iterator(); it.hasNext(); )
		{
			IDrawable d = (IDrawable) it.next();
			d.init(vp, g);
		}
	}
	
	/** Initializes all objects for an OpenGL viewport
	 * 
	 *  @param vp the viewport
     *  @param gl OpenGL context 
	 */
	public void init(ViewportJOGL vp, GL gl)
	{
		for (Iterator it = drawables_.iterator(); it.hasNext(); )
		{
			IDrawable d = (IDrawable) it.next();
			d.init(vp, gl);
		}
	}
	/** Draws the objects to a Java2D viewport
	 * 
	 * @param vp the viewport
	 * @param g Graphics2D context
	 */
	public void draw(ViewportJ2D vp, Graphics2D g)
	{
		for (Iterator it = drawables_.iterator(); it.hasNext(); )
		{
			IDrawable d = (IDrawable) it.next();
			d.draw(vp, g);
		}
	}
	
    /** Draws the objects to an OpenGL viewport
     * 
     * @param vp the viewport
     * @param gl OpenGL context
     */
	public void draw(ViewportJOGL vp, GL gl)
	{
		for (Iterator it = drawables_.iterator(); it.hasNext(); )
		{
			IDrawable d = (IDrawable) it.next();
			d.draw(vp, gl);
		}
	}
	
	/** Sets the position of the objects.
     * 
     * 	@param pos new position
     */
	public void setPosition(IVector2 pos)
	{
		for (Iterator it = drawables_.iterator(); it.hasNext(); )
		{
			IDrawable d = (IDrawable) it.next();
			d.setPosition(pos);
		}
	}
	
	/** Sets the velocity of the objects.
     *  
     * 	@param velocity new velocity
     */
	public void setVelocity(IVector2 velocity)
	{
		for (Iterator it = drawables_.iterator(); it.hasNext(); )
		{
			IDrawable d = (IDrawable) it.next();
			d.setVelocity(velocity);
		}
	}
	
	public IDrawable copy()
	{
		DrawableCombiner newCombiner = new DrawableCombiner();
		for (Iterator it = drawables_.iterator(); it.hasNext(); )
		{
			IDrawable d = (IDrawable) it.next();
			newCombiner.addDrawable(d.copy());
		}
		return newCombiner;
	}
}
