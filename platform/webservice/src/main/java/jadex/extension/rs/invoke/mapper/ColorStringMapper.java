package jadex.extension.rs.invoke.mapper;

import java.awt.Color;

import jadex.extension.rs.publish.mapper.IValueMapper;

/**
 *  Color to hex string mapper.
 */
public class ColorStringMapper implements IValueMapper
{
	/**
	 *  Convert the given value.
	 *  @param value The value to convert.
	 *  @return The converted value.
	 */
	public Object convertValue(Object value) throws Exception 
	{
		String ret = Integer.toHexString(((Color)value).getRGB());
		return ret.substring(2, ret.length());
	}
}
