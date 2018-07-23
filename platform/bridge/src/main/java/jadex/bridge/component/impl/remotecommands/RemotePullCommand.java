package jadex.bridge.component.impl.remotecommands;

import java.util.Collection;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.impl.IRemoteConversationCommand;
import jadex.bridge.service.types.security.ISecurityInfo;
import jadex.commons.future.IFuture;
import jadex.commons.future.IPullIntermediateFuture;

/**
 *  Command for pulling from pull intermediate futures.
 */
public class RemotePullCommand<T> implements IRemoteConversationCommand<Collection<T>>
{
	/**
	 *  Create the command.
	 */
	public RemotePullCommand()
	{
	}
	
	/**
	 *  Execute a command.
	 *  @param access The agent to run the command on.
	 *  @param future Future of the active conversation.
	 *  @param secinf The established security level to decide if the command is allowed.
	 */
	public void	execute(IInternalAccess access, IFuture<Collection<T>> future, ISecurityInfo secinf)
	{
		((IPullIntermediateFuture<T>)future).pullIntermediateResult();
	}
}
