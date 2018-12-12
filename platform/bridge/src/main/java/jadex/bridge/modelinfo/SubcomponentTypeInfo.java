package jadex.bridge.modelinfo;


/**
 *  Component type representation.
 */
public class SubcomponentTypeInfo extends Startable
{
	//-------- attributes --------

	/** The name. */
	protected String name;

	/** The filename. */
	protected String filename;

	//-------- constructors --------

	/**
	 *  Create a new component type.
	 */
	public SubcomponentTypeInfo()
	{
	}
	
	/**
	 *  Create a new component type.
	 */
	public SubcomponentTypeInfo(String name, String filename)
	{
		this.name = name;
		this.filename = filename;
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
