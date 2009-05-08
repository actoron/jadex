package jadex.adapter.base.envsupport.observer.graphics.drawable;

import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Double;

public class AbstractVisual2D
{
	/** The size (scale) of the visual or a bound property to the size. */
	protected Object	size;
	
	/** The rotation of the visual or a bound property to the rotation along the x-axis. */
	protected Object	xRotation;
	
	/** The rotation of the visual or a bound property to the rotation along the y-axis. */
	protected Object	yRotation;
	
	/** The rotation of the visual or a bound property to the rotation along the z-axis. */
	protected Object	zRotation;
	
	/** The position of the visual or a bound property to the position. */
	protected Object	position;
	
	/**
	 * Initializes the members with default values.
	 */
	public AbstractVisual2D()
	{
		this.size = new Vector2Double(1.0);
		this.xRotation = Vector2Double.ZERO.copy();
		this.yRotation = Vector2Double.ZERO.copy();
		this.zRotation = Vector2Double.ZERO.copy();
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
	 * Sets the z-rotation of the visual to a fixed rotation.
	 * (alias for setZRotation)
	 * 
	 * @param rotation the fixed z-rotation
	 */
	public void setRotation(IVector1 rotation)
	{
		setZRotation(rotation);
	}
	
	/**
	 * Sets the x-rotation of the visual to a fixed rotation.
	 * 
	 * @param rotation the fixed x-rotation
	 */
	public void setXRotation(IVector1 rotation)
	{
		this.xRotation = rotation;
	}
	
	/**
	 * Sets the y-rotation of the visual to a fixed rotation.
	 * 
	 * @param rotation the fixed y-rotation
	 */
	public void setYRotation(IVector1 rotation)
	{
		this.yRotation = rotation;
	}
	
	/**
	 * Sets the z-rotation of the visual to a fixed rotation.
	 * 
	 * @param rotation the fixed z-rotation
	 */
	public void setZRotation(IVector1 rotation)
	{
		this.zRotation = rotation;
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
	 * Binds the z-rotation of the visual to an object property.
	 * (alias for bindZRotation)
	 * 
	 * @param propId the property ID
	 */
	public void bindRotation(String propId)
	{
		bindZRotation(propId);
	}
	
	/**
	 * Binds the x-rotation of the visual to an object property.
	 * 
	 * @param propId the property ID
	 */
	public void bindXRotation(String propId)
	{
		xRotation = propId;
	}
	
	/**
	 * Binds the y-rotation of the visual to an object property.
	 * 
	 * @param propId the property ID
	 */
	public void bindYRotation(String propId)
	{
		yRotation = propId;
	}
	
	/**
	 * Binds the z-rotation of the visual to an object property.
	 * 
	 * @param propId the property ID
	 */
	public void bindZRotation(String propId)
	{
		zRotation = propId;
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
	 * Gets the x-axis rotation or rotation-binding.
	 * 
	 * @return x-axis rotation or rotation-binding
	 */
	public Object getXRotation()
	{
		return xRotation;
	}
	
	/**
	 * Gets the y-axis rotation or rotation-binding.
	 * 
	 * @return y-axis rotation or rotation-binding
	 */
	public Object getYRotation()
	{
		return yRotation;
	}

	/**
	 * Gets the z-axis rotation or rotation-binding.
	 * 
	 * @return z-axis rotation or rotation-binding
	 */
	public Object getZRotation()
	{
		return zRotation;
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
