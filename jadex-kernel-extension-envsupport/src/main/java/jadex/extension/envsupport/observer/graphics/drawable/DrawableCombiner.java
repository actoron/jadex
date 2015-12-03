package jadex.extension.envsupport.observer.graphics.drawable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.commons.IPropertyObject;
import jadex.extension.envsupport.observer.graphics.IViewport;
import jadex.extension.envsupport.observer.gui.SObjectInspector;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

/**
 * This drawable combines multiple drawables into a single drawable object.
 */
public class DrawableCombiner extends AbstractVisual2D implements IPropertyObject
{
	//-------- attributes --------
	
	/** The primitives. */
	private Map	primitives;

	/** The properties */
	protected Map properties;
	
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
		primitives = new HashMap();
	}
	
	/**
	 * Adds a primitive to the combiner.
	 * 
	 * @param p the primitive
	 */
	public void addPrimitive(Primitive p)
	{
		addPrimitive(p, 0);
	}

	/**
	 * Adds a primitive to the combiner in a specific layer.
	 * 
	 * @param p the primitive
	 * @param layer the layer
	 * @param sizeDefining true if the added object should be the size-defining
	 *        one
	 */
	public void addPrimitive(Primitive p, int layer)
	{
		Integer l = Integer.valueOf(layer);
		List drawList = (List)primitives.get(l);
		if(drawList == null)
		{
			drawList = new ArrayList();
			primitives.put(l, drawList);
		}
		drawList.add(p);
	}

	/**
	 * Removes a primitive from all layers in the combiner.
	 * 
	 * @param p the primitive
	 */
	public void removePrimitive(Primitive p)
	{
		Collection drawLists = primitives.values();

		for(Iterator it = drawLists.iterator(); it.hasNext();)
		{
			List drawList = (List)it.next();
			drawList.remove(p);
		}
	}
	
	/**
	 * Draws the objects to a viewport
	 * 
	 * @param obj object being drawn
	 * @param layer the current layer
	 * @param vp the viewport
	 */
	public void draw(Object obj, Integer layer, IViewport vp)
	{
		List drawList = (List)primitives.get(layer);
		if(drawList == null)
			return;

		for(Iterator it = drawList.iterator(); it.hasNext();)
		{
			Primitive p = (Primitive)it.next();
			vp.drawPrimitive(this, p, obj);
		}
	}

	/**
	 * Returns all layers used by this DrawableCombiner.
	 * 
	 * @return all layers used by the DrawableCombiner
	 */
	public Set getLayers()
	{
		Set layers = new HashSet(primitives.keySet());
		return layers;
	}
	
	/**
	 *  Flushes the render information.
	 */
	public void flushRenderInfo()
	{
		for(Iterator it = primitives.values().iterator(); it.hasNext();)
		{
			List drawList = (List)it.next();
			for(Iterator it2 = drawList.iterator(); it2.hasNext();)
			{
				Primitive p = (Primitive)it2.next();
				p.flushRenderInfo();
			}
		}
	}
	
	/**
	 * Gets the bound value for a property.
	 * @return The bound value.
	 */
	public Object getBoundValue(Object obj, Object prop, IViewport viewport)
	{
//		Object ret = prop;
//		if(prop instanceof String)
//		{
//			String name = (String)prop;
//				
//			ret = getProperty(name);
//			if(ret instanceof IParsedExpression)
//			{
//				SimpleValueFetcher fetcher = new SimpleValueFetcher(viewport.getPerspective().getObserverCenter().getSpace().getFetcher());
//				fetcher.setValue("$drawable", this);
//				fetcher.setValue("$object", obj);
//				fetcher.setValue("$perspective", viewport.getPerspective());
//				ret = ((IParsedExpression)ret).getValue(fetcher);
//			}
//			
//			if(ret==null)
//				ret = SObjectInspector.getProperty(obj, name);
//		}
//		return ret;
		
		Object ret = prop;
		RuntimeException[] exceptions = new RuntimeException[2];
		
		if(prop instanceof IParsedExpression)
		{
			SimpleValueFetcher fetcher = new SimpleValueFetcher(viewport.getPerspective().getObserverCenter().getSpace().getFetcher());
			fetcher.setValue("$drawable", this);
			fetcher.setValue("$object", obj);
			fetcher.setValue("$perspective", viewport.getPerspective());
			
			String name = ((IParsedExpression)prop).getExpressionText();//(String)prop;
			ret = getProperty(name);
			
			if(ret instanceof IParsedExpression)
			{
				try
				{
					ret = ((IParsedExpression)ret).getValue(fetcher);
				}
				catch(RuntimeException e)
				{
					exceptions[0] = e;
				}
			}
			
			if(ret==null)
			{
				try
				{
					ret = ((IParsedExpression)prop).getValue(fetcher);
				}
				catch(RuntimeException e)
				{
					exceptions[1] = e;
				}
			}
			
			if(ret==null)
			{
				ret = SObjectInspector.getProperty(obj, name);
			}
		}
		else if(prop instanceof String)
		{
			String name = (String)prop;
			
			ret = getProperty(name);
			if(ret instanceof IParsedExpression)
			{
				SimpleValueFetcher fetcher = new SimpleValueFetcher(viewport.getPerspective().getObserverCenter().getSpace().getFetcher());
				fetcher.setValue("$drawable", this);
				fetcher.setValue("$object", obj);
				fetcher.setValue("$perspective", viewport.getPerspective());
				ret = ((IParsedExpression)ret).getValue(fetcher);
			}
			
			if(ret==null)
			{
				ret = SObjectInspector.getProperty(obj, name);
			}
		}
		
		if (ret instanceof IParsedExpression) {
			// Obviously parsing didn't work -> fail-fast. 
			// Throw last exception first, hoping that this is the relevant error.
			for(int i = exceptions.length-1; i > -1; i--)
			{
				if (exceptions[i] != null) {
					throw exceptions[i];
				}
			}
		}
		
		return ret;
	}	
	
	//-------- IPropertyObject --------
	
	/**
	 * Returns a property.
	 * @param name name of the property
	 * @return the property
	 */
	public Object getProperty(String name)
	{
		return properties==null? null: properties.get(name);
	}
	
	/**
	 * Returns all of the properties.
	 * @return the properties
	 */
	public Set getPropertyNames()
	{
		return properties==null? Collections.EMPTY_SET: properties.keySet();
	}
	
	/**
	 * Sets a property
	 * @param name name of the property
	 * @param value value of the property
	 */
	public void setProperty(String name, Object value)
	{
		if(properties==null)
			properties = new HashMap();
		properties.put(name, value);
	}
	
	/*
	 * (non-Javadoc)
	 * @see jadex.commons.IPropertyObject#hasProperty(java.lang.String)
	 */
	public boolean hasProperty(String name) {
		return properties != null && properties.containsKey(name);
	}
}
