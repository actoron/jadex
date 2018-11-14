package jadex.commons;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *  Representation of data loaded on first access.
 *  Used, e.g., for component type icons.
 */
public class LazyResource
{
	//-------- attributes --------
	
	/** The reference class for using correct package and class loader. */
	protected Class<?>	clazz;
	
	/** The resource path (relative to clazz or absolute). */
	protected String	path;
	
	/** The resource, if already loaded. */
	protected byte[]	data;
	
	/** The exception, if already failed. */
	protected IOException	exception;
	
	//-------- constructors --------
	
	/**
	 *  Create a lazy resource.
	 */
	public LazyResource(Class<?> clazz, String path)
	{
		this.clazz	= clazz;
		this.path	= path;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the resource contents.
	 */
	public byte[]	getData()	throws IOException
	{
		if(exception!=null)
		{
			throw exception;
		}
		else if(data==null)
		{
			InputStream	is = null;
			try
			{
				ByteArrayOutputStream	bos	= new ByteArrayOutputStream();
				is	= clazz.getResourceAsStream(path);
				byte[]	buf	= new byte[8192];
				int read;
				while((read=is.read(buf))!=-1)
				{
					bos.write(buf, 0, read);
				}
				data	= bos.toByteArray();
			}
			catch(IOException e)
			{
				exception	= e;
				throw e;
			}
			finally
			{
				try
				{
					if(is!=null)
						is.close();
				}
				catch(Exception e)
				{
				}
			}
		}
		return data;
	}
}
