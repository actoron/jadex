package jadex.extension.rs.publish.mapper;

import jadex.commons.SUtil;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.glassfish.grizzly.http.server.Request;

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
		
		ResponseBuilder ret = null;
		
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
			ret = Response.ok(o);
			if(ri.getStatus()!=null)
			{
				ret.status(ri.getStatus());
			}
			if(ri.getMediatype()!=null)
			{
				ret = ret.type(ri.getMediatype());
			}
			if(ri.getHeaders()!=null)
			{
				Map<String, String>	headers	= ri.getHeaders();
				if(headers!=null)
				{
					for(Map.Entry<String, String> entry: headers.entrySet())
					{
						ret = ret.header(entry.getKey(), entry.getValue());
					}
				}
			}
			else if(ri.getPath()!=null)
			{
				String cttype = SUtil.guessContentTypeByFilename(ri.getPath());
				if(cttype!=null)
				{
					ret = ret.type(cttype);
				}
			}
		}
		else if(o instanceof String)
		{
			ret = Response.ok(o);
		}
		else if(o instanceof Exception)
		{
			if(!isProduction())
			{
				ret = Response.status(Status.INTERNAL_SERVER_ERROR).entity("<html><head></head>" +
					"<body><pre>\n"+SUtil.getExceptionStacktrace((Exception)o)+"\n</pre></body></html>");
			}
			else
			{
				ret = Response.status(Status.INTERNAL_SERVER_ERROR).entity("<html><head></head>" +
					"<body><h1>500 Internal server error</h1></body></html>");
			}
		}
		else if(o instanceof URI)
		{
			URI uri = (URI)o;
			if(uri.toString().indexOf(":")==-1)
			{
				uri = new URI("http://"+uri.toString());
			}
			ret = Response.status(Status.SEE_OTHER).location(uri);
		}
		
//		ret	= buildResponse(ret, value);
		
		return ret.build();
	}
	
	/**
	 *  Prestep for extracting the content of a value.
	 */
	protected Object extractContent(Object value)
	{
		return value;
	}
	
//	/**
//	 *  Post step to change or augment the response.
//	 */
//	protected ResponseBuilder	buildResponse(ResponseBuilder rb, Object value)
//	{
//		return rb;
//	}
	
	/**
	 *  Test if is in debug mode.
	 */
	protected boolean isProduction()
	{
		boolean ret = false;
		String pro = System.getProperty("production", System.getenv("production"));
		if(pro!=null)
		{
			try
			{
				ret = Boolean.parseBoolean(pro);
			}
			catch(Exception e)
			{
			}
		}
		return ret;
	}
}
