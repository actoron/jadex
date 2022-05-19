package jadex.bdiv3.runtime.impl;

import jadex.commons.SUtil;

/**
 *  Base class for transferable information about BDI elements.
 */
public class AbstractBDIInfo
{
	//-------- attributes --------
	
	/** The belief id. */
	protected String id;
	
	/** The element type. */
	protected String type;
	
	/** The parent element id. */
	protected String parentid;
		
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
	 * /
	public AbstractBDIInfo(String id, String type)
	{
		this.id	= id;
		this.type	= type;
	}*/
	
	//--------- methods ---------
	
	/**
	 *  Return the id.
	 */
	public String getId()
	{
		return id;
	}

	/**
	 *  Set the id.
	 */
	public AbstractBDIInfo setId(String id)
	{
		this.id = id;
		return this;
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
	public AbstractBDIInfo setType(String type)
	{
		this.type = type;
		return this;
	}
	
	/**
	 * @return the parentid
	 */
	public String getParentId() 
	{
		return parentid;
	}

	/**
	 * @param parentid the parentid to set
	 */
	public AbstractBDIInfo setParentId(String parentid) 
	{
		this.parentid = parentid;
		return this;
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
