package jadex.tools.ontology;

import jadex.base.fipa.IComponentAction;

/**
 *  Java class for concept ToolRequest of jadex.tools.introspector ontology.
 */
public abstract class ToolRequest implements IComponentAction
{
	//-------- attributes ----------

	/** A tool type such as "tracer". */
	protected String	tooltype;

	//-------- constructors --------

	/**
	 *  Default Constructor.
	 *  Create a new ToolRequest.
	 */
	public ToolRequest()
	{
	}

	//-------- accessor methods --------

	/**
	 *  Get the tool-type of this ToolRequest.
	 *  A tool type such as "tracer".
	 * @return tool-type
	 */
	public String getToolType()
	{
		return this.tooltype;
	}

	/**
	 *  Set the tool-type of this ToolRequest.
	 *  A tool type such as "tracer".
	 * @param tooltype the value to be set
	 */
	public void setToolType(String tooltype)
	{
		this.tooltype = tooltype;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this ToolRequest.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "ToolRequest(" + "tooltype=" + getToolType() + ")";
	}

}
