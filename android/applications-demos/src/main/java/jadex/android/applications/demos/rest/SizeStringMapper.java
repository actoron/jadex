package jadex.android.applications.demos.rest;

import jadex.commons.SReflect;
import jadex.extension.rs.publish.mapper.IValueMapper;

import java.util.Iterator;

/**
 * 
 */
public class SizeStringMapper implements IValueMapper
{
	public Object convertValue(Object value) throws Exception
	{
		Iterator<Integer> it = SReflect.getIterator(value);
		Integer width = it.next().intValue();
		Integer height = it.next().intValue();
		return width+"x"+height;
	}
}
