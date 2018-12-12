package jadex.xml;

/**
 *  Namespace definition.
 */
public class Namespace
{
	//-------- attributes --------
	
	/** The prefix. */
	protected String prefix;
	
	/** The URI. */
	protected String uri;
	
	//-------- constructors --------

	/**
	 *  Create the namespace.
	 */
	public Namespace(String prefix, String uri)
	{
		this.prefix = prefix;
		this.uri = uri;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the prefix.
	 *  @return The prefix.
	 */
	public String getPrefix()
	{
		return prefix;
	}

	/**
	 *  Get the uri.
	 *  @return The uri.
	 */
	public String getURI()
	{
		return uri;
	}
}
