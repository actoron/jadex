package jadex.webservice.examples.rs.banking;

import jadex.extension.rs.publish.annotation.MethodMapper;
import jadex.extension.rs.publish.annotation.ParameterMapper;
import jadex.extension.rs.publish.annotation.ResultMapper;
import jadex.micro.annotation.Value;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * 
 */
public interface IRSBankingService
{
	/**
	 *  Get the account statement.
	 *  @param request The request.
	 *  @return The account statement.
	 */
	@GET
	@Path("getAS/")
	@Produces(MediaType.TEXT_HTML)
	@MethodMapper(value="getAccountStatement", parameters={Request.class})
	@ParameterMapper(@Value(clazz=RequestMapper.class))
	@ResultMapper(@Value(clazz=BeanToHTMLMapper.class))
	public String getAcci(String begin, String end);
	
}
