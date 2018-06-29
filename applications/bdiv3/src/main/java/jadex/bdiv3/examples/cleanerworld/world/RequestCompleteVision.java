package jadex.bdiv3.examples.cleanerworld.world;

import jadex.bridge.fipa.IComponentAction;


/**
 *  Java class for concept RequestCompleteVision of cleaner_beans ontology.
 */
public class RequestCompleteVision implements IComponentAction
{
	//-------- attributes ----------

	/** Attribute for slot vision. */
	protected Vision vision;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>RequestCompleteVision</code>.
	 */
	public RequestCompleteVision()
	{ 
	}

	//-------- accessor methods --------

	/**
	 *  Get the vision of this RequestCompleteVision.
	 * @return vision
	 */
	public Vision getVision()
	{
		return this.vision;
	}

	/**
	 *  Set the vision of this RequestCompleteVision.
	 * @param vision the value to be set
	 */
	public void setVision(Vision vision)
	{
		this.vision = vision;
	}

	//-------- object methods --------

	/**
	 *  Get a string representation of this RequestCompleteVision.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "RequestCompleteVision(" + ")";
	}
}
