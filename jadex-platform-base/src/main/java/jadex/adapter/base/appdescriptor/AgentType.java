package jadex.adapter.base.appdescriptor;

/**
 * 
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
	 * 
	 */
	public AgentType()
	{
	}

	//-------- methods --------

	/**
	 * @return the name
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the filename
	 */
	public String getFilename()
	{
		return this.filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename)
	{
		this.filename = filename;
	}
}
