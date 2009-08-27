package jadex.adapter.base.envsupport.observer.graphics.drawable;

import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.IVector3;
import jadex.adapter.base.envsupport.math.Vector2Double;
import jadex.adapter.base.envsupport.math.Vector3Double;

/**
 *  Base class for visual elements.
 */
public class AbstractVisual2D //extends SimplePropertyObject
{
	//-------- attributes --------
	
	/** The size (scale) of the visual or a bound property to the size. */
	private Object	size;
	
	/** The rotation of the visual or a bound property to the rotation along the x-axis. */
	private Object	rotation;
	
	/** The position of the visual or a bound property to the position. */
	private Object	position;
	
	//-------- constructors --------
	
	/**
	 * Initializes the members with default values.
	 */
	public AbstractVisual2D()
	{
		this(null, null, null);
	}
	
	/**
	 * Initializes the members with default values.
	 */
	public AbstractVisual2D(Object position, Object rotation, Object size)
	{
		this.size = size!=null? size: new Vector2Double(1.0);
		this.rotation = rotation!=null? rotation: Vector3Double.ZERO.copy();
		this.position = position!=null? position: Vector2Double.ZERO.copy();
	}
	
	//-------- methods --------
	
	/**
	 * Sets the position of the visual to a fixed position.
	 * @param pos fixed position
	 */
	public void setPosition(IVector2 pos)
	{
		position = pos.copy();
	}

	/**
	 * Sets the rotation of the visual to a fixed rotation.
	 * @param rotation the fixed rotation
	 */
	public void setRotation(IVector3 rotation)
	{
		this.rotation = rotation.copy();
	}

	/**
	 * Sets the size (scale) of the visual to a fixed size.
	 * @param size fixed size
	 */
	public void setSize(IVector2 size)
	{
		this.size = size.copy();
	}
	
	/**
	 * Binds the position of the visual to an object property.
	 * @param propId the property ID
	 */
	public void bindPosition(String propId)
	{
		position = propId;
	}
	
	/**
	 * Binds the z-rotation of the visual to an object property.
	 * (alias for bindZRotation)
	 * @param propId the property ID
	 */
	public void bindRotation(String propId)
	{
		rotation = propId;
	}
	
	/**
	 * Binds the size of the visual to an object property.
	 * @param propId the property ID
	 */
	public void bindSize(String propId)
	{
		size = propId;
	}

	/**
	 *  Get the size.
	 *  @return the size.
	 */
	public Object getSize()
	{
		return this.size;
	}

	/**
	 *  Get the rotation.
	 *  @return the rotation.
	 */
	public Object getRotation()
	{
		return this.rotation;
	}

	/**
	 *  Get the position.
	 *  @return the position.
	 */
	public Object getPosition()
	{
		return this.position;
	}
	
	/**
	 * Gets the position or position-binding.
	 * @return position or position-binding
	 * /
	public IVector2 getPosition(Object obj)
	{
		IVector2 ret = null;
		if(position instanceof String)
		{
			SimpleValueFetcher fetcher = new SimpleValueFetcher();
			fetcher.setValue("$drawable", this);
			fetcher.setValue("object", obj);
			ret = (IVector2)getProperty((String)position, fetcher);
		}
		
		if(ret==null)
			ret = SObjectInspector.getVector2(obj, position);
		return ret;
//		return position;
	}*/
	
	/**
	 * Gets the size or size-binding.
	 * @return size or size-binding
	 * /
	public IVector2 getSize(Object obj)
	{
		IVector2 ret = null;
		if(size instanceof String)
		{
			SimpleValueFetcher fetcher = new SimpleValueFetcher();
			fetcher.setValue("$drawable", this);
			fetcher.setValue("$object", obj);
			ret = (IVector2)getProperty((String)size, fetcher);
		}
		
		if(ret==null)
			ret = SObjectInspector.getVector2(obj, size);
		return ret;
//		return size;
	}*/

	/**
	 *  Get the rotation.
	 *  @return The rotation.
	 * /
	public IVector3 getRotation(Object obj)
	{
		IVector3 ret = null;
		if(rotation instanceof String)
		{
			SimpleValueFetcher fetcher = new SimpleValueFetcher();
			fetcher.setValue("$drawable", this);
			fetcher.setValue("$object", obj);
			ret = (IVector3)getProperty((String)rotation, fetcher);
		}
		if(ret==null)
			ret = SObjectInspector.getVector3(obj, rotation);
		return ret;
//		return rotation;
	}*/
	
	/**
	 * Returns a property.
	 * @param name name of the property
	 * @return the property
	 * /
	public Object getProperty(String name, IValueFetcher fetcher)
	{
		Object ret = properties==null? null: properties.get(name);
		
		if(ret instanceof IParsedExpression)
			ret = ((IParsedExpression)ret).getValue(fetcher);
		
		return ret;
	}*/
}
