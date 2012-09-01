/**
 * 
 */
package jadex.platform.service.cli;

import jadex.bridge.service.types.cli.ICliService;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;

/**
 *
 */
public interface IInternalCliService extends ICliService
{
	/**
	 * 
	 */
	public IFuture<String> internalGetShellPrompt(Tuple2<String, Integer> sessionid);
	
	/**
	 * 
	 */
	public IFuture<Boolean> removeSubshell(Tuple2<String, Integer> sessionid);
	
	
	/**
	 * 
	 */
	public IFuture<Void> addAllCommandsFromClassPath(Tuple2<String, Integer> sessionid);
	
	/**
	 * 
	 */
	public IFuture<Void> addCommand(ICliCommand cmd, Tuple2<String, Integer> sessionid);
}
