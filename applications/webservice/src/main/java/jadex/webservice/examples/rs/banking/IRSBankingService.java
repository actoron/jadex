package jadex.webservice.examples.rs.banking;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import jadex.bridge.service.annotation.Value;
import jadex.extension.rs.publish.annotation.MethodMapper;
import jadex.extension.rs.publish.annotation.ParametersMapper;
import jadex.extension.rs.publish.annotation.ResultMapper;

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
	@ParametersMapper(@Value(clazz=RequestMapper.class))
	@ResultMapper(@Value(clazz=BeanToHTMLMapper.class))
	public String getAcci(String begin, String end);
	
}
