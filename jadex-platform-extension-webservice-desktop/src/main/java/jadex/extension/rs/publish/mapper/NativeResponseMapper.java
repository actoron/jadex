package jadex.extension.rs.publish.mapper;

import jadex.commons.SUtil;

import java.io.ByteArrayInputStream;
import java.net.URI;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;

/**
 *  The native response mapper allows for sending back native response objects.
 *  a) String (is treated as normal html response)
 *  b) ResourceInfo (is loaded as inputstream and transferred)
 *  c) URI (is treated as redirect url)
 */
public class NativeResponseMapper implements IValueMapper
{
	/**
	 *  Convert a value.
	 *  @param value The value to convert.
	 *  @return The converted value.
	 */
	public Object convertValue(Object value) throws Exception
	{
		Object o = extractContent(value);
		
		Response ret = null;
		
		if(o instanceof ResourceInfo)
		{
			ResourceInfo ri = (ResourceInfo)o;
			if(ri.getPath()!=null)
			{
				o = SUtil.getResource0(ri.getPath(), null);
			}
			else if(ri.getData()!=null)
			{
				o = new ByteArrayInputStream(ri.getData());
			}
			ResponseBuilder rb = Response.ok(o);
			if(ri.getMediatype()!=null)
			{
				rb = rb.type(ri.getMediatype());
			}
			else if(ri.getPath()!=null)
			{
				String cttype = SUtil.guessContentTypeByFilename(ri.getPath());
				if(cttype!=null)
				{
					rb = rb.type(cttype);
				}
			}
			ret = rb.build();
		}
		else if(o instanceof String)
		{
			ret = Response.ok(o).build();
		}
		else if(o instanceof Exception)
		{
			if(isDebug())
			{
				ret = Response.ok(SUtil.getExceptionStacktrace((Exception)o)).build();
			}
			else
			{
				ret = Response.status(Status.INTERNAL_SERVER_ERROR).entity("<html><head></head><body><h1>500 Internal server error</h1></body></html>").build();
			}
		}
		else if(o instanceof URI)
		{
			URI uri = (URI)o;
			if(uri.toString().indexOf(":")==-1)
			{
				uri = new URI("http://"+uri.toString());
			}
			ret = Response.status(Status.SEE_OTHER).location(uri).build();
		}
		
		return ret;
	}
	
	/**
	 *  Prestep for extracting the content of a value.
	 */
	public Object extractContent(Object value)
	{
		return value;
	}
	
	/**
	 *  Test if is in debug mode.
	 */
	protected boolean isDebug()
	{
		boolean ret = false;
		String debug = System.getProperty("EVDEBUG", System.getenv("EVDEBUG"));
		if(debug!=null)
		{
			try
			{
				ret = Boolean.parseBoolean(debug);
			}
			catch(Exception e)
			{
			}
		}
		return ret;
	}
}
