package jadex.bridge.service;

/**
 * 
 */
public class PublishInfo
{
	/** The publish id. */
	protected String pid;
	
	/** The publish type. */
	protected String type;

	/**
	 *  Create a new publish info.
	 */
	public PublishInfo()
	{
	}

	/**
	 *  Create a new publish info.
	 *  @param pid The publish id, e.g. url.
	 *  @param type The type.
	 */
	public PublishInfo(String pid, String type)
	{
		this.pid = pid;
		this.type = type;
	}

	/**
	 *  Get the publishid.
	 *  @return the publishid.
	 */
	public String getPublishId()
	{
		return pid;
	}

	/**
	 *  Set the publishid.
	 *  @param publishid The publishid to set.
	 */
	public void setPublishId(String pid)
	{
		this.pid = pid;
	}

	/**
	 *  Get the type.
	 *  @return the type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}
}
