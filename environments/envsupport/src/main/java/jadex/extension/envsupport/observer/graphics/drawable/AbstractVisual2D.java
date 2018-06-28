package jadex.extension.envsupport.observer.graphics.drawable;

import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.IVector3;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector3Double;

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
}
