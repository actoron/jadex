package jadex.webservice.examples.rs.banking;

import java.text.SimpleDateFormat;

import jadex.extension.rs.publish.mapper.IParameterMapper;

/**
 * 
 */
public class RequestMapper implements IParameterMapper
{
	/**
	 * 
	 */
	public Object[] convertParameters(Object[] parameters) throws Exception
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		return new Object[]{new Request(sdf.parse((String)parameters[0]), sdf.parse((String)parameters[0]))};		
	}
}
