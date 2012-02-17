package jadex.extension.rs.invoke.mapper;

import java.awt.Color;

import jadex.extension.rs.publish.mapper.IValueMapper;

/**
 * 
 *
 */
public class ColorStringMapper implements IValueMapper
{
	/**
	 * 
	 */
	public Object convertValue(Object value) throws Exception 
	{
		String ret = Integer.toHexString(((Color)value).getRGB());
		return ret.substring(2, ret.length());
	}
}
