package jadex.extension.rs.publish.mapper;

/**
 * 
 */
public class ResourceInfo
{
	/** The resource file path. */
	protected String path;

	/** The media type. */
	protected String mediatype;

	/**
	 *  Create a new ResourceInfo.
	 */
	public ResourceInfo(String path, String mediatype)
	{
		this.mediatype = mediatype;
		this.path = path;
	}

	/**
	 *  Get the mediatype.
	 *  return The mediatype.
	 */
	public String getMediatype()
	{
		return mediatype;
	}

	/**
	 *  Set the mediatype. 
	 *  @param mediatype The mediatype to set.
	 */
	public void setMediatype(String mediatype)
	{
		this.mediatype = mediatype;
	}

	/**
	 *  Get the path.
	 *  return The path.
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 *  Set the path. 
	 *  @param path The path to set.
	 */
	public void setPath(String path)
	{
		this.path = path;
	}
}
