package jadex.bdiv3.runtime.impl;

import jadex.commons.SUtil;

/**
 *  Base class for transferable information about BDI elements.
 */
public class AbstractBDIInfo
{
	//-------- attributes --------
	
	/** The belief id. */
	protected Object	id;
	
	/** The element type. */
	protected String	type;
		
	//-------- constructors --------
	
	/**
	 *  Create a new info.
	 */
	public AbstractBDIInfo()
	{
		// Bean constructor.
	}

	/**
	 *  Create a new info.
	 */
	public AbstractBDIInfo(Object id, String type)
	{
		this.id	= id;
		this.type	= type;
	}
	
	//--------- methods ---------
	
	/**
	 *  Return the id.
	 */
	public Object getId()
	{
		return id;
	}

	/**
	 *  Set the id.
	 */
	public void setId(Object id)
	{
		this.id = id;
	}

	/**
	 *  Return the type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 *  Test if two objects are equal.
	 */
	public boolean	equals(Object obj)
	{
		return obj instanceof AbstractBDIInfo && SUtil.equals(((AbstractBDIInfo)obj).id, id);
	}
	
	/**
	 *  Get the hashcode
	 */
	public int	hashCode()
	{
		return 31+id.hashCode();
	}
}
