package jadex.webservice.examples.rs.chart;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import jadex.extension.rs.invoke.RestResponse;
import jadex.extension.rs.publish.mapper.IValueMapper;

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
		RestResponse res = (RestResponse)value;
		InputStream is = res.getEntityInputStream();
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
