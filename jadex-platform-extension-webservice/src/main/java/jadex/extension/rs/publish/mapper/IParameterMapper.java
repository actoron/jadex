package jadex.extension.rs.publish.mapper;

/**
 *  The parameter mapper interface is used for mappers that
 *  take as input a parameter array and produce a converted one
 *  as output (n:m).
 */
public interface IParameterMapper
{
	/**
	 *  Convert parameters.
	 *  @param parameters The values to convert.
	 *  @return The converted parameters.
	 */
	public Object[] convertParameters(Object[] parameters) throws Exception;
}
