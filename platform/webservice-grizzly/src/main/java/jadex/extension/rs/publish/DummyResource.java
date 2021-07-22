package jadex.extension.rs.publish;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

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
