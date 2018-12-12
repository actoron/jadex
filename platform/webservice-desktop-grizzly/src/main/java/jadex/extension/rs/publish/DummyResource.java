package jadex.extension.rs.publish;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *  Dummy resource needed because jersey complains when a http server
 *  is started without any root resource :-(
 */
@Path("dummy")
public class DummyResource
{
	/**
	 *  Dummy method.
	 */
	@Path("dummy")
	@GET
	public String dummy()
	{
		return "dummy";
	}
}
