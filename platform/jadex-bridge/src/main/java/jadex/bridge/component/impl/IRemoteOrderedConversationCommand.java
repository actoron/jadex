package jadex.bridge.component.impl;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.security.IMsgSecurityInfos;

/**
 *  Interface for intermediate (or final) commands in existing conversations. 
 */
public interface IRemoteOrderedConversationCommand
{
	/**
	 *  Execute a command.
	 *  @param access The agent to run the command on.
	 *  @param conv The active conversation.
	 *  @param secinf The established security level to decide if the command is allowed.
	 */
	public void	execute(IInternalAccess access, IOrderedConversation conv, IMsgSecurityInfos secinf);
}
