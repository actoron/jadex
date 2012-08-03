package jadex.webservice.examples.rs.hello;

import jadex.extension.rs.publish.mapper.IValueMapper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.sun.jersey.api.client.ClientResponse;

public class XMLResultMapper implements IValueMapper
{

	@Override
	public Object convertValue(Object value) throws Exception
	{
		String result = null;
		if (value instanceof ClientResponse)
		{
			ClientResponse cr = (ClientResponse) value;
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
