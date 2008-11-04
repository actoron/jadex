package jadex.tools.ontology;


/**
 *  Java class for concept ToolSupport of jadex.tools.introspector ontology.
 */
public class ToolSupport extends ToolRequest
{
	//-------- attributes ----------

	/** True, when the given tool type is supported by the agent. */
	protected boolean	supported;

	//-------- constructors --------

	/**
	 *  Default Constructor.
	 *  Create a new ToolSupport.
	 */
	public ToolSupport()
	{
	}

	/**
	 *  Init Constructor. <br>
	 *  Create a new ToolSupport.<br>
	 *  Initializes the object with required attributes.
	 * @param tooltype
	 */
	public ToolSupport(String tooltype)
	{
		this();
		setToolType(tooltype);
	}

	//-------- accessor methods --------

	/**
	 *  Get the supported of this ToolSupport.
	 *  True, when the given tool type is supported by the agent.
	 * @return supported
	 */
	public boolean isSupported()
	{
		return this.supported;
	}

	/**
	 *  Set the supported of this ToolSupport.
	 *  True, when the given tool type is supported by the agent.
	 * @param supported the value to be set
	 */
	public void setSupported(boolean supported)
	{
		this.supported = supported;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this ToolSupport.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "ToolSupport(" + "tooltype=" + getToolType() + ")";
	}

}
