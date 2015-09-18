package jadex.extension.rs.publish.mapper;

import jadex.commons.SUtil;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

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
			ret.header("Access-Control-Allow-Origin", "*");
    		// http://stackoverflow.com/questions/3136140/cors-not-working-on-chrome
    		ret.header("Access-Control-Allow-Credentials", "true ");
    		ret.header("Access-Control-Allow-Methods", "OPTIONS, GET, POST");
    		ret.header("Access-Control-Allow-Headers", "Content-Type, Depth, User-Agent, X-File-Size, X-Requested-With, If-Modified-Since, X-File-Name, Cache-Control");
		}
		else if(o instanceof String)
		{
			ret = Response.ok(o);
			ret.header("Access-Control-Allow-Origin", "*");
	   		// http://stackoverflow.com/questions/3136140/cors-not-working-on-chrome
    		ret.header("Access-Control-Allow-Credentials", "true ");
    		ret.header("Access-Control-Allow-Methods", "OPTIONS, GET, POST");
    		ret.header("Access-Control-Allow-Headers", "Content-Type, Depth, User-Agent, X-File-Size, X-Requested-With, If-Modified-Since, X-File-Name, Cache-Control");
		}
		else if(o instanceof Exception)
		{
			if(!isProduction())
			{
				ret = Response.status(Status.INTERNAL_SERVER_ERROR).entity("<html><head></head>" +
					"<body><pre>\n"+SUtil.getExceptionStacktrace((Exception)o)+"\n</pre></body></html>");
				ret.header("Access-Control-Allow-Origin", "*");
		   		// http://stackoverflow.com/questions/3136140/cors-not-working-on-chrome
	    		ret.header("Access-Control-Allow-Credentials", "true ");
	    		ret.header("Access-Control-Allow-Methods", "OPTIONS, GET, POST");
	    		ret.header("Access-Control-Allow-Headers", "Content-Type, Depth, User-Agent, X-File-Size, X-Requested-With, If-Modified-Since, X-File-Name, Cache-Control");
			}
			else
			{
				ret = Response.status(Status.INTERNAL_SERVER_ERROR).entity("<html><head></head>" +
					"<body><h1>500 Internal server error</h1></body></html>");
				ret.header("Access-Control-Allow-Origin", "*");
		   		// http://stackoverflow.com/questions/3136140/cors-not-working-on-chrome
	    		ret.header("Access-Control-Allow-Credentials", "true ");
	    		ret.header("Access-Control-Allow-Methods", "OPTIONS, GET, POST");
	    		ret.header("Access-Control-Allow-Headers", "Content-Type, Depth, User-Agent, X-File-Size, X-Requested-With, If-Modified-Since, X-File-Name, Cache-Control");
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
			ret.header("Access-Control-Allow-Origin", "*");
	   		// http://stackoverflow.com/questions/3136140/cors-not-working-on-chrome
    		ret.header("Access-Control-Allow-Credentials", "true ");
    		ret.header("Access-Control-Allow-Methods", "OPTIONS, GET, POST");
    		ret.header("Access-Control-Allow-Headers", "Content-Type, Depth, User-Agent, X-File-Size, X-Requested-With, If-Modified-Since, X-File-Name, Cache-Control");
		}
		
//		ret	= buildResponse(ret, value);
		
		return ret==null? value: ret.build();
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
