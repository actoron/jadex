package jadex.extension.envsupport.observer.graphics.drawable3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.commons.IPropertyObject;
import jadex.extension.envsupport.math.IVector3;
import jadex.extension.envsupport.math.Vector3Double;
import jadex.extension.envsupport.observer.graphics.IViewport3d;
import jadex.extension.envsupport.observer.gui.SObjectInspector;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;



/**
 * This drawable combines multiple 3ddrawables into a single drawable object.
 */
public class DrawableCombiner3d extends AbstractVisual3d implements IPropertyObject
{
	//-------- attributes --------
	
	/** The primitives. */
	private List<Primitive3d> primitives3d;
	

	/** The properties */
	public Map<String, Object> properties;
	
	/** does the Drawable3d have dynamic subelements? */
	public boolean dynamic;
	
	/** Has a SpaceObject?*/
	//TODO : better Name
	public boolean _hasSpaceobject;
	
	/** Uses 3d Rotation? */
	public boolean _rotation3d;
	
	/** Calculate the Rotation ? */
	public boolean _autoRotation;
	
	/** Constant for 45 degree. In all three Dimensions (x,y,z) */
	public static final IVector3 DEG45X = new Vector3Double((Math.PI/180)*45, 0, 0);
	public static final IVector3 DEG45Y = new Vector3Double(0, (Math.PI/180)*45, 0);
	public static final IVector3 DEG45Z = new Vector3Double(0, 0, (Math.PI/180)*45);
	
	/** Constant for 90 degree. In all three Dimensions (x,y,z) */
	public static final IVector3 DEG90X = new Vector3Double((Math.PI/180)*90, 0, 0);
	public static final IVector3 DEG90Y = new Vector3Double(0, (Math.PI/180)*90, 0);
	public static final IVector3 DEG90Z = new Vector3Double(0, 0, (Math.PI/180)*90);
	
	/** Constant for 180 degree. In all three Dimensions (x,y,z) */
	public static final IVector3 DEG180X = new Vector3Double((Math.PI/180)*180, 0, 0);
	public static final IVector3 DEG180Y = new Vector3Double(0, (Math.PI/180)*180, 0);
	public static final IVector3 DEG180Z = new Vector3Double(0, 0, (Math.PI/180)*180);
	
	/** Constant for 270 degree. In all three Dimensions (x,y,z) */
	public static final IVector3 DEG270X = new Vector3Double((Math.PI/180)*270, 0, 0);
	public static final IVector3 DEG270Y = new Vector3Double(0, (Math.PI/180)*270, 0);
	public static final IVector3 DEG270Z = new Vector3Double(0, 0, (Math.PI/180)*270);
	


	//-------- constructors --------
	
	/**
	 * Creates a new DrawableCombiner of size 1.0.
	 */
	public DrawableCombiner3d()
	{
		this(null, null, null, true, true, false, true);
	}

	//-------- methods --------
	
	/**
	 * Creates a new DrawableCombiner3d
	 */
	public DrawableCombiner3d(Object position, Object rotation, Object size, boolean dynamic, boolean hasSpaceobject, boolean rotation3d, boolean autoRotation)
	{
		super(position==null? "position": position, rotation, size);
		_hasSpaceobject = hasSpaceobject;
		_rotation3d = rotation3d;
		_autoRotation = autoRotation;
		this.dynamic = dynamic;
		primitives3d = new ArrayList<Primitive3d>();
		
		setProperty("$deg45x", DEG45X);
		setProperty("$deg45y", DEG45Y);
		setProperty("$deg45z", DEG45Z);
		
		setProperty("$deg90x", DEG90X);
		setProperty("$deg90y", DEG90Y);
		setProperty("$deg90z", DEG90Z);
		
		setProperty("$deg180x", DEG180X);
		setProperty("$deg180y", DEG180Y);
		setProperty("$deg180z", DEG180Z);
		
		setProperty("$deg270x", DEG270X);
		setProperty("$deg270y", DEG270Y);
		setProperty("$deg270z", DEG270Z);
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
				ret = ((IParsedExpression)ret).getValue(fetcher);
			}
			
			if(ret==null)
			{
				ret = ((IParsedExpression)prop).getValue(fetcher);
			}
			
			if(ret==null)
			{
				ret = SObjectInspector.getProperty(obj, name);
			}
		}
		else if(prop instanceof String)
		{
			ret = SObjectInspector.getProperty(obj, (String)prop);
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


	/**
	 */
	public boolean hasSpaceobject()
	{
		return _hasSpaceobject;
	}

	/**
	 * @param 
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

	/**
	 * @return the _autoRotation
	 */
	public boolean isAutoRotation()
	{
		return _autoRotation;
	}

	/**
	 * @param _autoRotation the _autoRotation to set
	 */
	public void setAutoRotation(boolean _autoRotation)
	{
		this._autoRotation = _autoRotation;
	}

	public boolean isDynamic() {
		return dynamic;
	}

	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

}
