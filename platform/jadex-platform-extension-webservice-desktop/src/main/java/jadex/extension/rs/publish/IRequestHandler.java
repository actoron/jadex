package jadex.extension.rs.publish;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  Interface for handling http requests.
 */
public interface IRequestHandler
{
	/**
	 *  Handle the request.
	 *  @param request The request.
	 *  @param response The response.
	 *  @param args Container specific args.
	 */
	public void handleRequest(HttpServletRequest request, HttpServletResponse response, 
		Object args) throws Exception;
}
