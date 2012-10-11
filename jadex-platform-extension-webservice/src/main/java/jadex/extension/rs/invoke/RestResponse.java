package jadex.extension.rs.invoke;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Rest Response container. Includes header data and body (as byte array).
 */
public class RestResponse
{
	private InputStream targetObjectInputStream;
	private long date;
	private long contentLength;
	private String contentType;

	/**
	 * Default Constructor, no data set.
	 */
	public RestResponse()
	{
	}
	
	/**
	 * Constructor
	 * 
	 * @param targetStream
	 *            the input stream that was transferred with this response.
	 */
	public RestResponse(InputStream targetStream)
	{
		this.targetObjectInputStream = targetStream;
	}
	
	/**
	 * Constructor
	 * 
	 * @param targetArray
	 *            the byte array that was transferred with this response.
	 */
	public RestResponse(byte[] targetArray)
	{
		this.targetObjectInputStream = new ByteArrayInputStream((byte[]) targetArray);
	}

	public void setTargetByteArray(byte[] targetArray)
	{
		this.targetObjectInputStream = new ByteArrayInputStream(targetArray);
	}

	public void setEntityInputStream(InputStream entityInputStream)
	{
		this.targetObjectInputStream = entityInputStream;
	}

	public InputStream getEntityInputStream()
	{
		return targetObjectInputStream;
	}

	public <T> T getEntity(Class<T> clazz)
	{
		if (clazz == InputStream.class)
		{
			return (T) getEntityInputStream();
		} else
		{
			throw new RuntimeException("unknown entity type: " + clazz);
		}
	}

	public String getContentType()
	{
		return contentType;
	}

	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}

	public long getContentLength()
	{
		return contentLength;
	}

	public void setContentLength(long length)
	{
		this.contentLength = length;
	}

	public void setDate(long date)
	{
		this.date = date;
	}

	public long getDate()
	{
		return date;
	}

}
