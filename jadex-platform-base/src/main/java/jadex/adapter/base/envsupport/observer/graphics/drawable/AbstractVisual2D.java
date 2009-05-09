package jadex.adapter.base.envsupport.observer.graphics.drawable;

import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.IVector3;
import jadex.adapter.base.envsupport.math.Vector2Double;
import jadex.adapter.base.envsupport.math.Vector3Double;

public class AbstractVisual2D
{
	/** The size (scale) of the visual or a bound property to the size. */
	protected Object	size;
	
	/** The rotation of the visual or a bound property to the rotation along the x-axis. */
	protected Object	rotation;
	
	/** The position of the visual or a bound property to the position. */
	protected Object	position;
	
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
	
	/**
	 * Sets the position of the visual to a fixed position.
	 * 
	 * @param pos fixed position
	 */
	public void setPosition(IVector2 pos)
	{
		position = pos.copy();
	}

	/**
	 * Sets the z-rotation of the visual to a fixed rotation.
	 * (alias for setZRotation)
	 * 
	 * @param rotation the fixed z-rotation
	 */
	public void setRotation(IVector3 rotation)
	{
		this.rotation = rotation.copy();
	}

	/**
	 * Sets the size (scale) of the visual to a fixed size.
	 * 
	 * @param size fixed size
	 */
	public void setSize(IVector2 size)
	{
		this.size = size.copy();
	}
	
	/**
	 * Binds the position of the visual to an object property.
	 * 
	 * @param propId the property ID
	 */
	public void bindPosition(String propId)
	{
		position = propId;
	}
	
	/**
	 * Binds the z-rotation of the visual to an object property.
	 * (alias for bindZRotation)
	 * 
	 * @param propId the property ID
	 */
	public void bindRotation(String propId)
	{
		rotation = propId;
	}
	
	/**
	 * Binds the size of the visual to an object property.
	 * 
	 * @param propId the property ID
	 */
	public void bindSize(String propId)
	{
		size = propId;
	}
	
	/**
	 * Gets the position or position-binding.
	 * 
	 * @return position or position-binding
	 */
	public Object getPosition()
	{
		return position;
	}
	
	/**
	 * Gets the size or size-binding.
	 * 
	 * @return size or size-binding
	 */
	public Object getSize()
	{
		return size;
	}

	
	public Object getRotation()
	{
		return rotation;
	}
}
