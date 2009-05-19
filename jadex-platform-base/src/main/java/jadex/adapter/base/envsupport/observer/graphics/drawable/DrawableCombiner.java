package jadex.adapter.base.envsupport.observer.graphics.drawable;

import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.IVector3;
import jadex.adapter.base.envsupport.math.Vector2Double;
import jadex.adapter.base.envsupport.math.Vector3Double;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJ2D;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJOGL;
import jadex.adapter.base.envsupport.observer.gui.SObjectInspector;
import jadex.commons.IPropertyObject;
import jadex.commons.SimplePropertyChangeSupport;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

import java.awt.Graphics2D;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
public class DrawableCombiner extends AbstractVisual2D
{
	//-------- attributes --------
	
	/** The drawables. */
	private Map	drawables;

	//-------- constructors --------
	
	/**
	 * Creates a new DrawableCombiner of size 1.0.
	 */
	public DrawableCombiner()
	{
		this(null, null, null);
	}

	//-------- methods --------
	
	/**
	 * Creates a new DrawableCombiner of size 1.0.
	 */
	public DrawableCombiner(Object position, Object rotation, Object size)
	{
		super(position==null? "position": position, rotation, size);
		drawables = new HashMap();
		this.pcs = new SimplePropertyChangeSupport(this);
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
		List drawList = (List)drawables.get(l);
		if(drawList == null)
		{
			drawList = new ArrayList();
			drawables.put(l, drawList);
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
		Collection drawLists = drawables.values();

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
	public void init(ViewportJ2D vp)
	{
		for(Iterator it = drawables.values().iterator(); it.hasNext();)
		{
			List drawList = (List)it.next();
			for(Iterator it2 = drawList.iterator(); it2.hasNext();)
			{
				IDrawable d = (IDrawable)it2.next();
				d.init(vp);
			}
		}
	}

	/**
	 * Initializes all objects for an OpenGL viewport
	 * 
	 * @param vp the viewport
	 * @param gl OpenGL context
	 */
	public void init(ViewportJOGL vp)
	{
		for(Iterator it = drawables.values().iterator(); it.hasNext();)
		{
			List drawList = (List)it.next();
			for(Iterator it2 = drawList.iterator(); it2.hasNext();)
			{
				IDrawable d = (IDrawable)it2.next();
				d.init(vp);
			}
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
	public boolean setupMatrix(Object obj, Graphics2D g, boolean enablePos, boolean enableSize, boolean enableRot)
	{
		if(enablePos)
		{
			IVector2 position = SObjectInspector.getVector2(obj, this.position);
			if(position==null)
				return false;
			g.translate(position.getXAsDouble(), position.getYAsDouble());
		}
		
		if(enableSize)
		{
			IVector2 size = SObjectInspector.getVector2(obj, this.size);
			if(size==null)
				size = new Vector2Double(1,0);
			g.scale(size.getXAsDouble(), size.getYAsDouble());
		}
		
		if(enableRot)
		{
			IVector3 rot = SObjectInspector.getVector3(obj, this.rotation);
			if(rot==null)
				rot = Vector3Double.ZERO.copy();
			g.scale(Math.cos(rot.getYAsDouble()), Math.cos(rot.getXAsDouble()));
			g.rotate(rot.getZAsDouble());
		}
		
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
	public boolean setupMatrix(Object obj, GL gl, boolean enablePos, boolean enableSize, boolean enableRot)
	{
		if (enablePos)
		{
			IVector2 position = SObjectInspector.getVector2(obj, this.position);
			if(position==null)
				return false;
			gl.glTranslatef(position.getXAsFloat(), position.getYAsFloat(), 0.0f);
		}
		
		if (enableSize)
		{
			IVector2 size = SObjectInspector.getVector2(obj, this.size);
			if(size==null)
				size = new Vector2Double(1,0);
			gl.glScalef(size.getXAsFloat(), size.getYAsFloat(), 1.0f);
		}
		
		if (enableRot)
		{
			IVector3 rot = SObjectInspector.getVector3(obj, this.rotation);
			if(rot==null)
				rot = Vector3Double.ZERO.copy();
			gl.glRotated(Math.toDegrees(rot.getXAsFloat()), 1.0, 0.0, 0.0);
			gl.glRotated(Math.toDegrees(rot.getYAsFloat()), 0.0, 1.0, 0.0);
			gl.glRotated(Math.toDegrees(rot.getZAsFloat()), 0.0, 0.0, 1.0);
		}
		
		return true;
	}

	/**
	 * Draws the objects to a Java2D viewport
	 * 
	 * @param obj object being drawn
	 * @param layer the current layer
	 * @param vp the viewport
	 */
	public void draw(Object obj, Integer layer, ViewportJ2D vp)
	{
		List drawList = (List)drawables.get(layer);
		if(drawList == null)
		{
			return;
		}
		
		Graphics2D g = vp.getContext();

		//System.out.println("draw: "+obj+" "+size+" "+rot+" "+position);
		for(Iterator it = drawList.iterator(); it.hasNext();)
		{
			IDrawable d = (IDrawable)it.next();
			d.draw(this, obj, vp);
		}
	}

	/**
	 * Draws the objects to an OpenGL viewport
	 * 
	 * @param obj object being drawn
	 * @param layer the current layer
	 * @param vp the viewport
	 */
	public void draw(Object obj, Integer layer, ViewportJOGL vp)
	{
		List drawList = (List)drawables.get(layer);
		if(drawList == null)
		{
			return;
		}
		
		GL gl = vp.getContext();
		
//		System.out.println("draw: "+obj+" "+size+" "+rot+" "+position);
		for(Iterator it = drawList.iterator(); it.hasNext();)
		{
			IDrawable d = (IDrawable)it.next();
			d.draw(this, obj, vp);
		}
	}

	/**
	 * Returns the scale of the combiner in relation to an object.
	 * @param obj an object
	 */
	public IVector2 getSize(Object obj)
	{
		return SObjectInspector.getVector2(obj, size);
	}

	/**
	 * Returns all layers used by this DrawableCombiner.
	 * 
	 * @return all layers used by the DrawableCombiner
	 */
	public Set getLayers()
	{
		Set layers = new HashSet(drawables.keySet());
		return layers;
	}
}
