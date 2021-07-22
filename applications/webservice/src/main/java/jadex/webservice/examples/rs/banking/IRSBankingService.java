package jadex.webservice.examples.rs.banking;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

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
