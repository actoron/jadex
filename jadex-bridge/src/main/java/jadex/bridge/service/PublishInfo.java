package jadex.bridge.service;

/**
 * 
 */
public class PublishInfo
{
	/** The url. */
	protected String url;
	
	/** The interface to publish. */
	protected Class type;

	/**
	 *  Create a new publish info.
	 */
	public PublishInfo()
	{
	}

	/**
	 * 
	 *  @param url The url.
	 *  @param type The type.
	 */
	public PublishInfo(String url, Class type)
	{
		this.url = url;
		this.type = type;
	}

	/**
	 *  Get the url.
	 *  @return The url.
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 *  Set the url.
	 *  @param url The url to set.
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}

	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public Class getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(Class type)
	{
		this.type = type;
	}
}
