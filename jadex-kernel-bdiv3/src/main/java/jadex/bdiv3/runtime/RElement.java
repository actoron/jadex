package jadex.bdiv3.runtime;

import jadex.bdiv3.model.MElement;

/**
 * 
 */
public class RElement
{
	/** The model element. */
	protected MElement modelelement;
	
	/**
	 * 
	 */
	public RElement(MElement modelelement)
	{
		this.modelelement = modelelement;
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
	
	
}
