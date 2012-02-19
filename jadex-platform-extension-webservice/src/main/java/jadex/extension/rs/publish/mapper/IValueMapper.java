package jadex.extension.rs.publish.mapper;

/**
 *  The value mapper interface. It is used for all mappers
 *  that have the task to convert between different representations.
 */
public interface IValueMapper
{	
	/**
	 *  Convert a value.
	 *  @param value The value to convert.
	 *  @return The converted value.
	 */
	public Object convertValue(Object value) throws Exception;
}
