package jadex.bridge.component.impl.remotecommands;

import java.util.Collection;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.impl.IRemoteConversationCommand;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.commons.future.IFuture;
import jadex.commons.future.IntermediateFuture;

/**
 *  Command for finished intermediate futures.
 */
public class RemoteFinishedCommand<T> implements IRemoteConversationCommand<Collection<T>>
{
	/**
	 *  Create the command.
	 */
	public RemoteFinishedCommand()
	{
	}
	
	/**
	 *  Execute a command.
	 *  @param access The agent to run the command on.
	 *  @param future Future of the active conversation.
	 *  @param secinf The established security level to decide if the command is allowed.
	 */
	public void	execute(IInternalAccess access, IFuture<Collection<T>> future, IMsgSecurityInfos secinf)
	{
		((IntermediateFuture<T>)future).setFinished();
	}
}
