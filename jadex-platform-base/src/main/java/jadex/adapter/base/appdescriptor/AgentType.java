package jadex.adapter.base.appdescriptor;

/**
 *  Agent type representation.
 */
public class AgentType
{
	//-------- attributes --------

	/** The name. */
	protected String name;

	/** The filename. */
	protected String filename;

	//-------- constructors --------

	/**
	 *  Create a new agent type.
	 */
	public AgentType()
	{
	}

	//-------- methods --------

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the filename.
	 *  @return The filename.
	 */
	public String getFilename()
	{
		return this.filename;
	}

	/**
	 *  Set the filename.
	 *  @param filename The name to set.
	 */
	public void setFilename(String filename)
	{
		this.filename = filename;
	}
}
