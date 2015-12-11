package jadex.bridge.service.types.cli;

import jadex.bridge.service.annotation.CheckNotNull;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;

/**
 *  Service to invoke the command line via a service call.
 */
//@Service
public interface ICliService
{
	/**
	 *  Execute a command line command and
	 *  get back the results.
	 *  @param command The command.
	 *  @return The result of the command.
	 */
	public IFuture<String> executeCommand(@CheckNotNull String command, 
		@CheckNotNull Tuple2<String, Integer> session);
}
