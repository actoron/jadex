package jadex.bridge.component.impl.remotecommands;

import java.util.Collection;
import java.util.Map;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.impl.IRemoteOrderedConversationCommand;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.commons.future.IFuture;
import jadex.commons.future.IntermediateFuture;

/**
 *  Command for finished intermediate futures.
 */
public class RemoteFinishedCommand<T>	extends AbstractResultCommand
{
	/**
	 *  Create the command.
	 */
	public RemoteFinishedCommand()
	{
	}
	
	/**
	 *  Create the command.
	 */
	public RemoteFinishedCommand(Map<String, Object> nonfunc)
	{
		super(nonfunc);
	}
	
	/**
	 *  Execute a command.
	 *  @param access The agent to run the command on.
	 *  @param future Future of the active conversation.
	 *  @param secinf The established security level to decide if the command is allowed.
	 */
	public void	doExecute(IInternalAccess access, IFuture<?> future, IMsgSecurityInfos secinf)
	{
		((IntermediateFuture<T>)future).setFinished();
	}
}
