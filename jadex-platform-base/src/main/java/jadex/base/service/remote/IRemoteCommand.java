package jadex.base.service.remote;

import jadex.commons.future.IFuture;
import jadex.micro.IMicroExternalAccess;


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
	public IFuture execute(IMicroExternalAccess component, RemoteServiceManagementService rsms);
}
