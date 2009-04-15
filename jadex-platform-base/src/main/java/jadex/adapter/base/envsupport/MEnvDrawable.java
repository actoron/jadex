package jadex.adapter.base.envsupport;

import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Double;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class MEnvDrawable
{
	//-------- attributes --------

	/** The objecttype. */
	protected String objecttype;
	
	/** The width. */
	protected double width;
	
	/** The height. */
	protected double height;
	
	/** The contained parts. */
	protected List parts;

	//-------- methods --------

	/**
	 *  Get the objecttype.
	 *  @return The objecttype.
	 */
	public String getObjectType()
	{
		return this.objecttype;
	}

	/**
	 *  Set the objecttype.
	 *  @param objecttype The objecttype to set.
	 */
	public void setObjectType(String objecttype)
	{
		this.objecttype = objecttype;
	}
	
	/**
	 *  Get the width.
	 *  @return The width.
	 */
	public double getWidth()
	{
		return this.width;
	}

	/**
	 *  Set the width.
	 *  @param width The width to set.
	 */
	public void setWidth(double width)
	{
		this.width = width;
	}
	
	/**
	 *  Get the height.
	 *  @return The height.
	 */
	public double getHeight()
	{
		return this.height;
	}

	/**
	 *  Set the height.
	 *  @param height The height to set.
	 */
	public void setHeight(double height)
	{
		this.height = height;
	}
	
	/**
	 *  Add a part.
	 *  @param part The part.
	 */
	public void addPart(Object part)
	{
		if(parts==null)
			parts = new ArrayList();
		parts.add(part);	
	}
	
	/**
	 *  Get the parts.
	 *  @return The parts.
	 */
	public List getParts()
	{
		return parts;
	}
	
	/**
	 * 
	 */
	public IVector2 getSize()
	{
		return width==0 && height==0? Vector2Double.ZERO: new Vector2Double(width, height);
	}
}
