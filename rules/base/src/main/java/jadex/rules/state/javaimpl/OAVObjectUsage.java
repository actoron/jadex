package jadex.rules.state.javaimpl;

import jadex.rules.state.OAVAttributeType;

/**
 *  Struct for storing an object usage.
 *  Identifies that a defined value (not part of this usage)
 *  is used in the defined object in the defined attribute.
 */
public class OAVObjectUsage
{
	//-------- attributes --------
	
	/** The object. */
	protected Object object;
	
	/** The attribute. */
	protected OAVAttributeType attr;
	
	/** External flag (i.e. referencing object is only externally referenced and no longer in state). */
	protected boolean external;
	
	//-------- constructors --------
	
	/**
	 *  Create a new object usage.  
	 */
	public OAVObjectUsage(Object object, OAVAttributeType attr)
	{
		this.object	= object;
		this.attr	= attr;
		this.external	= false;
	}

	//-------- methods --------
	
	/**
	 *  Get the object.
	 */
	public Object getObject()
	{
		return object;
	}

	/**
	 *  Get the attribute.
	 */
	public OAVAttributeType getAttribute()
	{
		return attr;
	}
	/**
	 *  Get the external flag.
	 */
	public boolean isExternal()
	{
		return external;
	}

	/**
	 *  Set the external flag.
	 */
	public void setExternal(boolean external)
	{
		this.external	= external;
	}

	/**
	 *  Generate the hash code.
	 */
	public int hashCode()
	{
		int result = 31 + attr.hashCode();
		result = 31 * result + object.hashCode();
		return result;
	}

	/**
	 *  Test if two objects are equal.
	 */
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		
		boolean	ret	= false;
		if(obj instanceof OAVObjectUsage)
		{
			final OAVObjectUsage other = (OAVObjectUsage)obj;
			ret	= object.equals(other.getObject())
				&& attr.equals(other.getAttribute());
		}
		return ret;
	}
	
	/**
	 *  Create a string representation.
	 */
	public String toString()
	{
		return "Usage("+object+" "+attr.getName()+" <used value>)";
	}
}