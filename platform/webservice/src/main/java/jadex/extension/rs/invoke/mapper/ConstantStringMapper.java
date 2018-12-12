package jadex.extension.rs.invoke.mapper;

import jadex.extension.rs.publish.mapper.IValueMapper;

/**
 *  Mapper that returns a constant value not
 *  depending on the input value.
 */
public class ConstantStringMapper implements IValueMapper
{
	//-------- attributes --------

	/** The value that is returned. */
	protected Object val;

	//-------- constructors --------
	
	/**
	 *  Create a new mapper.
	 *  @param val The constant value.
	 */
	public ConstantStringMapper(Object val)
	{
		this.val = val;
	}
	
	//-------- methods --------

	/**
	 *  Convert the given value.
	 *  @param value The value to convert.
	 *  @return The converted value.
	 */
	public Object convertValue(Object value) throws Exception
	{
		return ""+val;
	}
}
