package jadex.base.service.remote;

import java.util.Map;

import jadex.bridge.IExternalAccess;
import jadex.commons.IFuture;

/**
 *  Remote command interface for commands that the 
 *  remote service management can execute.
 */
public interface IRemoteCommand
{
	/**
	 *  Execute the command.
	 *  @param component The component.
	 *  @return An optional result command that will be 
	 *  sent back to the command origin. 
	 */
	public IFuture execute(IExternalAccess component, Map waitingcalls);
}
