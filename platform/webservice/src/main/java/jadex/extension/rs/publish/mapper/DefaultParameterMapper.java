package jadex.extension.rs.publish.mapper;


/**
 *  Default parameter mapper that uses the same
 *  value mapper for all parameters.
 */
public class DefaultParameterMapper implements IParameterMapper
{
	//-------- attributes --------
	
	/** The value mapper. */
	protected IValueMapper mapper;
	
	//-------- constructors --------
	
	/**
	 *  Create a new mapper.
	 */
	public DefaultParameterMapper(IValueMapper mapper)
	{
		this.mapper = mapper;
	}
	
	//-------- methods --------
	
	/**
	 *  Convert parameters.
	 *  @param parameters The values to convert.
	 *  @return The converted parameters.
	 */
	public Object[] convertParameters(Object[] parameters, Object context) throws Exception
	{
		Object[] ret = new Object[parameters.length];
		for(int i=0; i<parameters.length; i++)
		{
			ret[i] = mapper.convertValue(parameters[i]);
		}
		return ret;
	}

}
