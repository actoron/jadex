package jadex.webservice.examples.rs.hello;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.ws.rs.core.Response;

import jadex.extension.rs.invoke.RestResponse;
import jadex.extension.rs.publish.mapper.IValueMapper;

public class XMLResultMapper implements IValueMapper
{

	@Override
	public Object convertValue(Object value) throws Exception
	{
		String result = value instanceof String? (String)value: null;
		if(value instanceof Response)
		{
			Response cr = (Response)value;
			InputStream is = (InputStream)cr.getEntity();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int b = is.read(); 
			while (b != -1)
			{
				bos.write(b);
				b = is.read();
				// System.out.print((char)b);
			}
			byte[] data = bos.toByteArray();
			result = new String(data);
		}
		else if(value instanceof RestResponse)
		{
			RestResponse cr = (RestResponse) value;
			InputStream is = cr.getEntity(InputStream.class);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int b = is.read(); 
			while (b != -1)
			{
				bos.write(b);
				b = is.read();
				// System.out.print((char)b);
			}
			byte[] data = bos.toByteArray();
			result = new String(data);
		}
		return result;
	}

}
