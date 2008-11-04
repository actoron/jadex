package jadex.commons;

import java.io.IOException;
import java.io.InputStream;

/**
 *  Info for a resource to load.
 */
public class ResourceInfo
{
	//-------- attributes --------

	/** The filename. */
	protected String filename;

	/** The input stream. */
	protected InputStream input;

	/** The last modified date. */
	protected long lastmodified;

	//-------- constructors --------

	/**
	 *  Create a new resource info.
	 */
	public ResourceInfo(String filename, InputStream input, long lastmodified)
	{
		this.filename = filename;
		this.input = input;
		this.lastmodified = lastmodified;
	}

	//-------- methods --------

	/**
	 *  Get the filename.
	 *  @return The filename.
	 */
	public String getFilename()
	{
		return filename;
	}

	/**
	 *  Get the input stream.
	 *  @return The input stream.
	 */
	public InputStream getInputStream()
	{
		return input;
	}

	/**
	 *  Get the last modified date.
	 *  @return The last modified date.
	 */
	public long getLastModified()
	{
		return lastmodified;
	}
	
	/**
	 *  Cleanup the resource info
	 *  when it is no longer used.
	 */
	public void	cleanup()
	{
		if(input!=null)
		{
			try
			{
				input.close();
			}
			catch(IOException e)
			{
			}
		}
	}
	
	/**
	 *  On finalize, close the input stream.
	 */
	protected void finalize() throws Throwable
	{
		cleanup();
	}
}