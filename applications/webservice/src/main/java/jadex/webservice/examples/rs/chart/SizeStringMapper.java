package jadex.webservice.examples.rs.chart;

import java.util.Iterator;

import jadex.commons.SReflect;
import jadex.extension.rs.publish.mapper.IValueMapper;

/**
 * 
 */
public class SizeStringMapper implements IValueMapper
{
	public Object convertValue(Object value) throws Exception
	{
		Iterator<Integer> it = SReflect.getIterator(value);
		Integer width = it.next();
		Integer height = it.next();
		return width+"x"+height;
	}
}
