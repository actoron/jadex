package jadex.extension.rs.publish.mapper;


/**
 * 
 */
public class DefaultParameterMapper implements IParameterMapper
{
	/** The value mapper. */
	protected IValueMapper mapper;
	
	/**
	 * 
	 */
	public DefaultParameterMapper(IValueMapper mapper)
	{
		this.mapper = mapper;
	}
	
	/**
	 * 
	 */
	public Object[] convertParameters(Object[] parameters) throws Exception
	{
		Object[] ret = new Object[parameters.length];
		for(int i=0; i<parameters.length; i++)
		{
			ret[i] = mapper.convertValue(parameters[i]);
		}
		return ret;
	}
}
