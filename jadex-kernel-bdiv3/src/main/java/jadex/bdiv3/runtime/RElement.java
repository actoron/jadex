package jadex.bdiv3.runtime;

import jadex.bdiv3.model.MElement;

/**
 * 
 */
public class RElement
{
	protected static long cnt;
	
	/** The model element. */
	protected MElement modelelement;
		
	/** The element id. */
	protected String id;
	
	/**
	 * 
	 */
	public RElement(MElement modelelement)
	{
		this.modelelement = modelelement;
		this.id = modelelement.getName()+"_#"+cnt++;
	}

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
}
