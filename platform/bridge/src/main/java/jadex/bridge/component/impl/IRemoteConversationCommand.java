package jadex.bridge.component.impl;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.commons.future.IFuture;

/**
 *  Interface for intermediate (or final) commands in existing conversations. 
 */
public interface IRemoteConversationCommand<T>
{
	/**
	 *  Execute a command.
	 *  @param access The agent to run the command on.
	 *  @param future Future of the active conversation.
	 *  @param secinf The established security level to decide if the command is allowed.
	 */
	public void	execute(IInternalAccess access, IFuture<T> future, IMsgSecurityInfos secinf);
}
