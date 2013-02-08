package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.model.MElement;
import jadex.commons.SReflect;

/**
 *  Base element for all runtime elements.
 */
public class RElement
{
	protected static long cnt;
	
	//-------- attributes --------

	/** The model element. */
	protected MElement modelelement;
		
	/** The element id. */
	protected String id;

	//-------- constructors --------
	
	/**
	 *  Create a new runtime element.
	 */
	public RElement(MElement modelelement)
	{
		this.modelelement = modelelement;
		this.id = modelelement.getName()+"_#"+cnt++;
	}

	//-------- methods --------
	
	/**
	 *  Get the modelelement.
	 *  @return The modelelement.
	 */
	public MElement getModelElement()
	{
		return modelelement;
	}

	/**
	 *  Set the modelelement.
	 *  @param modelelement The modelelement to set.
	 */
	public void setModelElement(MElement modelelement)
	{
		this.modelelement = modelelement;
	}

	/**
	 *  Get the id.
	 *  @return The id.
	 */
	public String getId()
	{
		return id;
	}

	/**
	 *  Set the id.
	 *  @param id The id to set.
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/** 
	 *  Get the hashcode.
	 *  @return The hashcode.
	 */
	public int hashCode()
	{
		return 31 + id.hashCode();
	}

	/** 
	 *  Test if equal to other object.
	 *  @param obj The other object.
	 *  @return True, if equal.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof RElement)
		{
			ret = ((RElement)obj).getId().equals(getId());
		}
		return ret;
	}

	/** 
	 * 
	 */
	public String toString()
	{
		return SReflect.getInnerClassName(this.getClass())+"(modelelement=" + modelelement + ", id=" + id + ")";
	}
}
