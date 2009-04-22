package jadex.adapter.base.envsupport.observer.graphics.drawable;

import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Double;

public class AbstractVisual2D
{
	/** The size (scale) of the visual or a bound property to the size. */
	protected Object	size;
	
	/** The rotation of the visual or a bound property to the rotation. */
	protected Object	rotation;
	
	/** The position of the visual or a bound property to the position. */
	protected Object	position;
	
	/**
	 * Initializes the members with default values.
	 */
	public AbstractVisual2D()
	{
		this.size = new Vector2Double(1.0);
		this.rotation = Vector2Double.ZERO.copy();
		this.position = Vector2Double.ZERO.copy();
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
	 * Sets the rotation of the visual to a fixed rotation.
	 * 
	 * @param rotation the fixed rotation
	 */
	public void setRotation(IVector1 rotation)
	{
		this.rotation = rotation;
	}

	/**
	 * Sets the size (scale) of the visual to a fixed size.
	 * 
	 * @param size fixed size
	 */
	public void setSize(IVector2 size)
	{
		this.size = size;
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
	 * Binds the rotation of the visual to an object property.
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
	 * Gets the rotation or rotation-binding.
	 * 
	 * @return rotation or rotation-binding
	 */
	public Object getRotation()
	{
		return rotation;
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
}
