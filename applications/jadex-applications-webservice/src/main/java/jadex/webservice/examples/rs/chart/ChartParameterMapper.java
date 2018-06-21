package jadex.webservice.examples.rs.chart;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import jadex.extension.rs.publish.mapper.IValueMapper;

/**
 * 
 */
public class ChartParameterMapper implements IValueMapper
{
	/**
	 * 
	 */
	public Object convertValue(Object value) throws Exception
	{
		MultivaluedMap<String, String> ret = new MultivaluedHashMap<String, String>(); 

		Object[] params = (Object[])value;
		int width = ((Integer)params[0]).intValue();
		int height = ((Integer)params[1]).intValue();
		double[] data = (double[])params[2];
		String[] labels = (String[])params[3];

		// Chart type.
		ret.add("cht", "p3");
		
		// Chart size
		ret.add("chs", ""+width+"x"+height);
		
		// Chart data
		StringBuffer sb = new StringBuffer();
		sb.append("t:");
		for(int i=0; i<data.length; i++)
		{
			sb.append(data[i]);
			if(i+1<data.length)
				sb.append(",");
		}
		ret.add("chd", sb.toString());
	
		// Chart labels
		sb = new StringBuffer();
		for(int i=0; i<labels.length; i++)
		{
			sb.append(labels[i]);
			if(i+1<labels.length)
				sb.append("|");
		}
		ret.add("chl", sb.toString());
		
		return ret;
	}
}
