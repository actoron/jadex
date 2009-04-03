package jadex.adapter.base.envsupport.observer.graphics.drawable;

import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Double;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJ2D;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJOGL;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL;


/**
 * This drawable combines multiple drawables into a single drawable object.
 */
public class DrawableCombiner
{
	/** The drawables. */
	private Map			drawables_;

	/** The size of the object. */
	private IVector2	size_;

	/**
	 * Creates a new DrawableCombiner of size 1.0.
	 */
	public DrawableCombiner()
	{
		this(new Vector2Double(1.0));
	}

	/**
	 * Creates a new DrawableCombiner.
	 * 
	 * @param size size of the object
	 */
	public DrawableCombiner(IVector2 size)
	{
		drawables_ = new HashMap();
		size_ = size;
	}

	/**
	 * Adds a drawable to the combiner.
	 * 
	 * @param d the drawable
	 */
	public void addDrawable(IDrawable d)
	{
		addDrawable(d, 0);
	}

	/**
	 * Adds a drawable to the combiner in a specific layer.
	 * 
	 * @param d the drawable
	 * @param layer the layer
	 * @param sizeDefining true if the added object should be the size-defining
	 *        one
	 */
	public void addDrawable(IDrawable d, int layer)
	{
		Integer l = new Integer(layer);
		List drawList = (List)drawables_.get(l);
		if(drawList == null)
		{
			drawList = new ArrayList();
			drawables_.put(l, drawList);
		}
		drawList.add(d);
	}

	/**
	 * Removes a drawable from all layers in the combiner.
	 * 
	 * @param d the drawable
	 */
	public void removeDrawable(IDrawable d)
	{
		Collection drawLists = drawables_.values();

		for(Iterator it = drawLists.iterator(); it.hasNext();)
		{
			List drawList = (List)it.next();
			drawList.remove(d);
		}
	}

	/**
	 * Initializes all objects for a Java2D viewport
	 * 
	 * @param vp the viewport
	 * @param g Graphics2D context
	 */
	public void init(ViewportJ2D vp, Graphics2D g)
	{
		for(Iterator it = drawables_.values().iterator(); it.hasNext();)
		{
			List drawList = (List)it.next();
			for(Iterator it2 = drawList.iterator(); it2.hasNext();)
			{
				IDrawable d = (IDrawable)it2.next();
				d.init(vp, g);
			}
		}
	}

	/**
	 * Initializes all objects for an OpenGL viewport
	 * 
	 * @param vp the viewport
	 * @param gl OpenGL context
	 */
	public void init(ViewportJOGL vp, GL gl)
	{
		for(Iterator it = drawables_.values().iterator(); it.hasNext();)
		{
			List drawList = (List)it.next();
			for(Iterator it2 = drawList.iterator(); it2.hasNext();)
			{
				IDrawable d = (IDrawable)it2.next();
				d.init(vp, gl);
			}
		}
	}

	/**
	 * Draws the objects to a Java2D viewport
	 * 
	 * @param layer the current layer
	 * @param vp the viewport
	 * @param g Graphics2D context
	 */
	public void draw(Integer layer, ViewportJ2D vp, Graphics2D g)
	{
		List drawList = (List)drawables_.get(layer);
		if(drawList == null)
		{
			return;
		}

		for(Iterator it = drawList.iterator(); it.hasNext();)
		{
			IDrawable d = (IDrawable)it.next();
			d.draw(vp, g);
		}
	}

	/**
	 * Draws the objects to an OpenGL viewport
	 * 
	 * @param layer the current layer
	 * @param vp the viewport
	 * @param gl OpenGL context
	 */
	public void draw(Integer layer, ViewportJOGL vp, GL gl)
	{
		List drawList = (List)drawables_.get(layer);
		if(drawList == null)
		{
			return;
		}

		for(Iterator it = drawList.iterator(); it.hasNext();)
		{
			IDrawable d = (IDrawable)it.next();
			d.draw(vp, gl);
		}
	}

	/**
	 * Sets the position of the drawables.
	 * 
	 * @param pos new position
	 */
	public void setPosition(IVector2 pos)
	{
		for(Iterator it = drawables_.values().iterator(); it.hasNext();)
		{
			List drawList = (List)it.next();
			for(Iterator it2 = drawList.iterator(); it2.hasNext();)
			{
				IDrawable d = (IDrawable)it2.next();
				d.setPosition(pos);
			}
		}
	}

	/**
	 * Sets the velocity of the drawables.
	 * 
	 * @param velocity new velocity
	 */
	public void setVelocity(IVector2 velocity)
	{
		for(Iterator it = drawables_.values().iterator(); it.hasNext();)
		{
			List drawList = (List)it.next();
			for(Iterator it2 = drawList.iterator(); it2.hasNext();)
			{
				IDrawable d = (IDrawable)it2.next();
				d.setVelocity(velocity);
			}
		}
	}

	/**
	 * Sets the sizes of all drawables to a single value.
	 * 
	 * @param size new size
	 */
	public void setDrawableSizes(IVector2 size)
	{
		for(Iterator it = drawables_.values().iterator(); it.hasNext();)
		{
			List drawList = (List)it.next();
			for(Iterator it2 = drawList.iterator(); it2.hasNext();)
			{
				IDrawable d = (IDrawable)it2.next();
				d.setSize(size);
			}
		}
	}

	/**
	 * Returns the size of the object
	 */
	public IVector2 getSize()
	{
		return size_.copy();
	}

	/**
	 * Sets the size of the object
	 * 
	 * @param size new size of the object
	 */
	public void setSize(IVector2 size)
	{
		size_ = size.copy();
	}

	/**
	 * Returns all layers used by this DrawableCombiner.
	 * 
	 * @return all layers used by the DrawableCombiner
	 */
	public Set getLayers()
	{
		Set layers = new HashSet(drawables_.keySet());
		return layers;
	}
}
