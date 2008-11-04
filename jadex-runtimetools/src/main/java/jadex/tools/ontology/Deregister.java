package jadex.tools.ontology;


/**
 *  Java class for concept Deregister of jadex.tools.introspector ontology.
 */
public class Deregister extends ToolRequest
{
	//-------- constructors --------

	/**
	 *  Default Constructor.
	 *  Create a new Deregister.
	 */
	public Deregister()
	{
	}

	/**
	 *  Init Constructor.
	 *  Create a new Deregister.
	 *  Initializes the object with required attributes.
	 * @param tooltype
	 */
	public Deregister(String tooltype)
	{
		this();
		setToolType(tooltype);
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this Deregister.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Deregister(" + "tooltype=" + getToolType() + ")";
	}

}
