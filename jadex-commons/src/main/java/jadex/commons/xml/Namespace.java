package jadex.commons.xml;

public class Namespace
{
	protected String prefix;
	
	protected String uri;

	/**
	 *  Create a new namespace.
	 */
	public Namespace(String prefix, String uri)
	{
		super();
		this.prefix = prefix;
		this.uri = uri;
	}

	/**
	 *  Get the prefix.
	 *  @return The prefix.
	 */
	public String getPrefix()
	{
		return this.prefix;
	}

	/**
	 *  Get the uri.
	 *  @return The uri.
	 */
	public String getURI()
	{
		return this.uri;
	}
}
