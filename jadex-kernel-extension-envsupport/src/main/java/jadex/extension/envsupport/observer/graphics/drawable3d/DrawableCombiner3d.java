package jadex.extension.envsupport.observer.graphics.drawable3d;

import jadex.commons.IPropertyObject;
import jadex.extension.envsupport.math.IVector3;
import jadex.extension.envsupport.math.Vector3Double;
import jadex.extension.envsupport.observer.graphics.IViewport3d;
import jadex.extension.envsupport.observer.gui.SObjectInspector;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;



/**
 * This drawable combines multiple drawables into a single drawable object.
 */
public class DrawableCombiner3d extends AbstractVisual3d implements IPropertyObject
{
	//-------- attributes --------
	
	/** The primitives. */
	private List<Primitive3d> primitives3d;
	

	/** The properties */
	public Map<String, Object> properties;
	
	/** Has a SpaceObject?*/

	//TODO : better Name
	public boolean _hasSpaceobject;
	
	/** Uses 3d Rotation? */
	public boolean _rotation3d;
	
	/** Constant for 90 degree. */
	public static IVector3 DEG90X = new Vector3Double((Math.PI/180)*90, 0, 0);
	
	/** Constant for 90 degree. */
	public static IVector3 DEG90Y = new Vector3Double(0, (Math.PI/180)*90, 0);

	/** Constant for 90 degree. */
	public static IVector3 DEG90Z = new Vector3Double(0, 0, (Math.PI/180)*90);

	//-------- constructors --------
	
	/**
	 * Creates a new DrawableCombiner of size 1.0.
	 */
	public DrawableCombiner3d()
	{
		this(null, null, null, true, false);
	}

	//-------- methods --------
	
	/**
	 * Creates a new DrawableCombiner3d
	 */
	public DrawableCombiner3d(Object position, Object rotation, Object size, boolean hasSpaceobject, boolean rotation3d)
	{
		super(position==null? "position": position, rotation, size);
		_hasSpaceobject = hasSpaceobject;
		_rotation3d = rotation3d;
		primitives3d = new ArrayList<Primitive3d>();
		
		setProperty("$deg90x", DEG90X);
		setProperty("$deg90y", DEG90Y);
		setProperty("$deg90z", DEG90Z);
	}

	/**
	 * Adds a primitive
	 * 
	 * @param p the primitive
	 * @param sizeDefining true if the added object should be the size-defining
	 *        one
	 */
	public void addPrimitive(Primitive3d p)
	{
		primitives3d.add(p);
	}

	/**
	 * Removes a primitive from all layers in the combiner.
	 * 
	 * @param p the primitive
	 */
	public void removePrimitive(Primitive3d p)
	{
		primitives3d.remove(p);
	}
	
	
	
	/**
	 * Gets the bound value for a property.
	 * @return The bound value.
	 */
	public Object getBoundValue(Object obj, Object prop, IViewport3d viewport)
	{
		Object ret = prop;
		if(prop instanceof String)
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
				ret = SObjectInspector.getProperty(obj, name);
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
	public Set<?> getPropertyNames()
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
			properties = new HashMap<String, Object>();
		properties.put(name, value);
	}
	
	/*
	 * (non-Javadoc)
	 * @see jadex.commons.IPropertyObject#hasProperty(java.lang.String)
	 */
	public boolean hasProperty(String name) {
		return properties != null && properties.containsKey(name);
	}

	public void flushRenderInfo() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return //TODO
	 */
	public boolean hasSpaceobject()
	{
		return _hasSpaceobject;
	}

	/**
	 * @param //TODO
	 */
	public void setHasSpaceobject(boolean hasSpaceobject)
	{
		this._hasSpaceobject = hasSpaceobject;
	}

	/**
	 * @return the primitives3d
	 */
	public List<Primitive3d> getPrimitives3d()
	{
		return primitives3d;
	}

	/**
	 * @param primitives3d the primitives3d to set
	 */
	public void setPrimitives3d(List<Primitive3d> primitives3d)
	{
		this.primitives3d = primitives3d;
	}

	/**
	 * @return the _rotation3d
	 */
	public boolean isRotation3d()
	{
		return _rotation3d;
	}

	/**
	 * @param _rotation3d the _rotation3d to set
	 */
	public void setRotation3d(boolean _rotation3d)
	{
		this._rotation3d = _rotation3d;
	}

}
