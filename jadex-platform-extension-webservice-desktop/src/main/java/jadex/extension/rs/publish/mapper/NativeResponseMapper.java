package jadex.extension.rs.publish.mapper;

import jadex.commons.SUtil;
import jadex.extension.rs.publish.mapper.IValueMapper;

import java.io.ByteArrayInputStream;
import java.net.URI;

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
		
		Response ret = null;
		
		if(o instanceof ResourceInfo)
		{
			ResourceInfo ri = (ResourceInfo)o;
			o = SUtil.getResource0(ri.getPath(), null);
			ResponseBuilder rb = Response.ok(o);
			if(ri.getMediatype()!=null)
				rb = rb.type(ri.getMediatype());
			ret = rb.build();
		}
		else if(o instanceof String)
		{
			ret = Response.ok(o).build();
		}
		else if(o instanceof URI)
		{
			ret = Response.status(Status.SEE_OTHER).location((URI)o).build();
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
}
