package jadex.extension.rs.invoke.mapper;

import jadex.extension.rs.publish.mapper.IValueMapper;

/**
 *  Color to hex string mapper.
 */
public class ColorStringMapperAndroid implements IValueMapper
{
	/**
	 *  Convert the given value.
	 *  @param value The value to convert.
	 *  @return The converted value.
	 */
	public Object convertValue(Object value) throws Exception 
	{
		String ret = Integer.toHexString(((Integer)value));
		return ret.substring(2, ret.length());
	}
}
