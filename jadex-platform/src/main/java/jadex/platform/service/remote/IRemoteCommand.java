package jadex.platform.service.remote;

import jadex.bridge.service.types.security.IAuthorizable;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.transformation.annotations.Alias;
import jadex.micro.IMicroExternalAccess;


/**
 *  Remote command interface for commands that the 
 *  remote service management can execute.
 */
public interface IRemoteCommand	extends IAuthorizable
{
	/**
	 *  Execute the command.
	 *  @param component The component.
	 *  @return An optional result command that will be 
	 *  sent back to the command origin. 
	 */
	public IIntermediateFuture<IRemoteCommand> execute(IMicroExternalAccess component, RemoteServiceManagementService rsms);
}
