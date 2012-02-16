package jadex.webservice.examples.rs.chart;

import jadex.extension.rs.publish.mapper.IValueMapper;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 */
public class ChartResultMapper implements IValueMapper
{
	/**
	 * 
	 */
	public Object convertValue(Object value) throws Exception
	{
//		Image ret = null;
		
		// Handle client response
		ClientResponse res = (ClientResponse)value;
		InputStream is = res.getEntity(InputStream.class);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int b = 0;
		while(b!=-1) 
		{ 
			b = is.read();
			bos.write(b);
//			System.out.print((char)b);
		}
		byte[] data = bos.toByteArray();
//		ret = Toolkit.getDefaultToolkit().createImage(data);
		
		return data;
	}
	
}
