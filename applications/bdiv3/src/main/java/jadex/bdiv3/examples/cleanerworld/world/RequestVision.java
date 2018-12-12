package jadex.bdiv3.examples.cleanerworld.world;

import jadex.bridge.fipa.IComponentAction;

/**
 *  Java class for concept RequestVision of cleaner_beans ontology.
 */
public class RequestVision implements IComponentAction
{
	//-------- attributes ----------

	/** Attribute for slot vision. */
	protected Vision vision;

	/** Attribute for slot cleaner. */
	protected Cleaner cleaner;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>RequestVision</code>.
	 */
	public RequestVision()
	{ //
	}

	//-------- accessor methods --------

	/**
	 *  Get the vision of this RequestVision.
	 * @return vision
	 */
	public Vision getVision()
	{
		return this.vision;
	}

	/**
	 *  Set the vision of this RequestVision.
	 * @param vision the value to be set
	 */
	public void setVision(Vision vision)
	{
		this.vision = vision;
	}

	/**
	 *  Get the cleaner of this RequestVision.
	 * @return cleaner
	 */
	public Cleaner getCleaner()
	{
		return this.cleaner;
	}

	/**
	 *  Set the cleaner of this RequestVision.
	 * @param cleaner the value to be set
	 */
	public void setCleaner(Cleaner cleaner)
	{
		this.cleaner = cleaner;
	}

	//-------- object methods --------

	/**
	 *  Get a string representation of this RequestVision.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "RequestVision(" + ")";
	}

}
