package jadex.webservice.examples.rs.banking;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jadex.extension.rs.publish.mapper.IParameterMapper;

/**
 *  Example mapper that shows how a request can be transformed.
 */
public class RequestMapper implements IParameterMapper
{
	/** The date formatetr. */
	protected SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

	/**
	 *  Convert the parameters.
	 *  @param parameters The parameters.
	 *  @return The converted parameters.
	 */
	public Object[] convertParameters(Object[] parameters, Object request) throws Exception
	{
		return new Object[]{new Request(parseDate(parameters[0]), parseDate(parameters[1]))};		
	}
	
	/**
	 *  Parse a date.
	 *  @param val The value.
	 *  @return The date.
	 */
	protected Date parseDate(Object val) throws ParseException
	{
		Date ret;
		if(val instanceof String && ((String)val).length()>0)
		{
			ret = sdf.parse((String)val);
		}
		else
		{
			ret = new Date();
		}
		return ret;
	}
}
