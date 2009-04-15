package jadex.adapter.base.envsupport;

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
	
	/** The size. */
	protected double size;
	
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
	 *  Get the size.
	 *  @return The size.
	 */
	public double getSize()
	{
		return this.size;
	}

	/**
	 *  Set the size.
	 *  @param size The size to set.
	 */
	public void setSize(double size)
	{
		this.size = size;
	}
	
	/**
	 *  Add a part.
	 *  @param part The part.
	 */
	public void addPart(MEnvTexturedRectangle part)
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
}
