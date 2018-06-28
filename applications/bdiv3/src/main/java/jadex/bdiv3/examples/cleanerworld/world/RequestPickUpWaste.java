package jadex.bdiv3.examples.cleanerworld.world;

import jadex.bridge.fipa.IComponentAction;


/**
 *  Java class for concept RequestPickUpWaste of cleaner_beans ontology.
 */
public class RequestPickUpWaste implements IComponentAction
{

	//-------- attributes ----------

	/** Attribute for slot waste. */
	protected Waste waste;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>RequestPickUpWaste</code>.
	 */
	public RequestPickUpWaste()
	{
	}

	//-------- accessor methods --------

	/**
	 *  Get the waste of this RequestPickUpWaste.
	 * @return waste
	 */
	public Waste getWaste()
	{
		return this.waste;
	}

	/**
	 *  Set the waste of this RequestPickUpWaste.
	 * @param waste the value to be set
	 */
	public void setWaste(Waste waste)
	{
		this.waste = waste;
	}

	//-------- object methods --------

	/**
	 *  Get a string representation of this RequestPickUpWaste.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "RequestPickUpWaste(" + ")";
	}
}
