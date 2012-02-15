package jadex.webservice.examples.rs.banking;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jadex.extension.rs.publish.mapper.IParameterMapper;

/**
 * 
 */
public class RequestMapper implements IParameterMapper
{
	protected SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

	/**
	 * 
	 */
	public Object[] convertParameters(Object[] parameters) throws Exception
	{
		return new Object[]{new Request(parseDate(parameters[0]), parseDate(parameters[1]))};		
	}
	
	/**
	 * 
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
