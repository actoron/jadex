/**
 * 
 */
package jadex.platform.service.cli;

import jadex.bridge.service.types.cli.ICliService;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;

/**
 *  Internal cli service interface used by the implementation
 *  to redirect calls to remote shells.
 */
public interface IInternalCliService extends ICliService
{
	/**
	 *  Get the shell prompt.
	 *  @param sessionid The session id.
	 *  @return The prompt.
	 */
	public IFuture<String> internalGetShellPrompt(Tuple2<String, Integer> sessionid);
	
	/**
	 *  Remove a subshell.
	 *  @param sessionid The session id.
	 *  @return True, if could be removed.
	 */
	public IFuture<Boolean> removeSubshell(Tuple2<String, Integer> sessionid);
	
	
	/**
	 *  Add all commands from classpath.
	 *  @param sessionid The session id.
	 */
	public IFuture<Void> addAllCommandsFromClassPath(Tuple2<String, Integer> sessionid);
	
	/**
	 *  Add a specific command.
	 *  @param sessionid The session id.
	 */
	public IFuture<Void> addCommand(ICliCommand cmd, Tuple2<String, Integer> sessionid);
}
