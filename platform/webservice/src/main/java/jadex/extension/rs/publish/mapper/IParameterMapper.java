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
	 *  @param context The context (could be the http servlet request or a custom container request).
	 *  @return The converted parameters.
	 */
	public Object[] convertParameters(Object[] parameters, Object request) throws Exception;
}
