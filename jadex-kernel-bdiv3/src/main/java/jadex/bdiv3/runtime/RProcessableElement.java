package jadex.bdiv3.runtime;

import jadex.bdiv3.model.MProcessableElement;

/**
 * 
 */
public class RProcessableElement
{
	/** The pojo element. */
	protected Object pojoelement;
	
	/** The model element. */
	protected MProcessableElement modelelement;

	/** The applicable plan list. */
	protected APL apl;
	
	/**
	 *  Create a new element.
	 */
	public RProcessableElement(Object pojoelement, MProcessableElement modelelement)
	{
		this.pojoelement = pojoelement;
		this.modelelement = modelelement;
	}

	/**
	 *  Get the modelelement.
	 *  @return The modelelement.
	 */
	public MProcessableElement getModelElement()
	{
		return modelelement;
	}

	/**
	 *  Set the modelelement.
	 *  @param modelelement The modelelement to set.
	 */
	public void setModelElement(MProcessableElement modelelement)
	{
		this.modelelement = modelelement;
	}

	/**
	 *  Get the apl.
	 *  @return The apl.
	 */
	public APL getApplicablePlanList()
	{
		if(apl==null)
			apl = new APL(this);
		return apl;
	}

	/**
	 *  Set the apl.
	 *  @param apl The apl to set.
	 */
	public void setApplicablePlanList(APL apl)
	{
		this.apl = apl;
	}

	/**
	 *  Get the pojoelement.
	 *  @return The pojoelement.
	 */
	public Object getPojoElement()
	{
		return pojoelement;
	}

	/**
	 *  Set the pojoelement.
	 *  @param pojoelement The pojoelement to set.
	 */
	public void setPojoElement(Object pojoelement)
	{
		this.pojoelement = pojoelement;
	}
	
	
}