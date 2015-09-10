package jadex.extension.rs.publish;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  Interface for handling http requests.
 */
@Service
public interface IRequestHandlerService
{
	/**
	 *  Handle the request.
	 *  @param request The request.
	 *  @param response The response.
	 *  @param args Container specific args.
	 */
	public IFuture<Void> handleRequest(@Reference HttpServletRequest request, @Reference HttpServletResponse response, @Reference Object args) throws Exception;
}
