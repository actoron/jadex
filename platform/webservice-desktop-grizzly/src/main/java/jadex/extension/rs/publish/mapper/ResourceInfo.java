package jadex.extension.rs.publish.mapper;

import java.util.Map;

/**
 * 
 */
public class ResourceInfo
{
	/** The resource file path. */
	protected String path;

	/** The direct data as alternative to the path. */
	protected byte[] data;
	
//	/** The direct data as alternative to the path. */
//	protected String sdata;
	
	/** The media type. */
	protected String mediatype;
	
	/** The response http state. */
	protected Integer status;
	
	/** The headers. */
	protected Map<String, String> headers;

	/**
	 *  Create a new ResourceInfo.
	 */
	public ResourceInfo(String path, String mediatype)
	{
		this(path, mediatype, null);
	}
	
	/**
	 *  Create a new ResourceInfo.
	 */
	public ResourceInfo(byte[] data, String mediatype)
	{
		this(data, mediatype, null);
	}
	
	/**
	 *  Create a new ResourceInfo.
	 */
	public ResourceInfo(String path, String mediatype, Integer status)
	{
		this.mediatype = mediatype;
		this.path = path;
		this.status = status;
	}
	
	/**
	 *  Create a new ResourceInfo.
	 */
	public ResourceInfo(byte[] data, String mediatype, Integer status)
	{
		this.mediatype = mediatype;
		this.data = data;
		this.status = status;
	}
	
//	/**
//	 *  Create a new ResourceInfo.
//	 */
//	public ResourceInfo(String sdata, String mediatype, Integer status)
//	{
//		this.mediatype = mediatype;
//		this.data = data;
//		this.status = status;
//	}

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

	/**
	 *  Get the state.
	 *  @return The state.
	 */
	public Integer getStatus() 
	{
		return status;
	}

	/**
	 *  Set the state.
	 *  @param state The state to set.
	 */
	public void setStatus(Integer status) 
	{
		this.status = status;
	}
	
	/**
	 *  Get the response headers (if any).
	 */
	public Map<String, String>	getHeaders()
	{
		return headers;
	}
	
	/**
	 *  Set the response headers.
	 */
	public void	setHeaders(Map<String, String> headers)
	{
		this.headers	= headers;
	}
}
