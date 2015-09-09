package jadex.extension.rs.publish;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Service;

/**
 *  Interface for handling http requests.
 */
@Service // todo: move and rename?!
public interface IRequestHandler
{
	/**
	 *  Handle the request.
	 *  @param request The request.
	 *  @param response The response.
	 *  @param args Container specific args.
	 */
	public void handleRequest(@Reference HttpServletRequest request, @Reference HttpServletResponse response, @Reference Object args) throws Exception;
}
