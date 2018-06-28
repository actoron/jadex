package jadex.webservice.examples.rs.hello;

import jadex.extension.rs.invoke.RestResponse;
import jadex.extension.rs.publish.mapper.IValueMapper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class XMLResultMapper implements IValueMapper
{

	@Override
	public Object convertValue(Object value) throws Exception
	{
		String result = null;
		if (value instanceof RestResponse)
		{
			RestResponse cr = (RestResponse) value;
			InputStream is = cr.getEntityInputStream();
			
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
