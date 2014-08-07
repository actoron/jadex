package jadex.extension.rs.publish.mapper;

/**
 * 
 */
public class ResourceInfo
{
	/** The resource file path. */
	protected String path;

	/** The direct data as alternative to the path. */
	protected byte[] data;
	
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
	 *  Create a new ResourceInfo.
	 */
	public ResourceInfo(byte[] data, String mediatype)
	{
		this.mediatype = mediatype;
		this.data = data;
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

	/**
	 *  Get the data.
	 *  return The data.
	 */
	public byte[] getData()
	{
		return data;
	}

	/**
	 *  Set the data. 
	 *  @param data The data to set.
	 */
	public void setData(byte[] data)
	{
		this.data = data;
	}
}
