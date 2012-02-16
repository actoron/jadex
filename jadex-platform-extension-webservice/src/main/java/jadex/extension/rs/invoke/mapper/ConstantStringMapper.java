package jadex.extension.rs.invoke.mapper;

import jadex.extension.rs.publish.mapper.IValueMapper;

/**
 * 
 */
public class ConstantStringMapper implements IValueMapper
{
	protected Object val;
	
	/**
	 * 
	 */
	public ConstantStringMapper(Object val)
	{
		this.val = val;
	}
	
	/**
	 * 
	 */
	public Object convertValue(Object value) throws Exception
	{
		return ""+val;
	}
}
