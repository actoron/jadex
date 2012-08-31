/**
 * 
 */
package jadex.platform.service.cli;

import jadex.bridge.service.types.cli.ICliService;
import jadex.commons.future.IFuture;

/**
 *
 */
public interface IInternalCliService extends ICliService
{
	/**
	 * 
	 */
	public IFuture<String> internalGetShellPrompt(String session);
	
	/**
	 * 
	 */
	public IFuture<Boolean> removeSubshell(String session);
	
	
	/**
	 * 
	 */
	public IFuture<Void> addAllCommandsFromClassPath(String session);
	
	/**
	 * 
	 */
	public IFuture<Void> addCommand(ICliCommand cmd, String session);
}
