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
		return value;
	}

}
