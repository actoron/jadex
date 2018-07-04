package jadex.bridge.component.impl.remotecommands;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.commons.future.IForwardCommandFuture;
import jadex.commons.future.IFuture;

/**
 *  Remote command for sending future commands in ICommandFuture.
 */
public class RemoteForwardCmdCommand extends AbstractResultCommand //implements IRemoteConversationCommand<Object>
{
	/** The Command. */
	protected Object	command;
	
	/**
	 *  Create the command.
	 */
	public RemoteForwardCmdCommand()
	{
		// Bean constructor.
	}
	
	/**
	 *  Create the command.
	 */
	public RemoteForwardCmdCommand(Object command)
	{
		this.command = command;
	}
	
	/**
	 *  Execute a command.
	 *  @param access The agent to run the command on.
	 *  @param future Future of the active conversation.
	 *  @param secinf The established security level to decide if the command is allowed.
	 */
	public void	doExecute(IInternalAccess access, IFuture<?> future, IMsgSecurityInfos secinf)
	{
		((IForwardCommandFuture)future).sendForwardCommand(command);
	}
	
	/**
	 *  Get the command.
	 *  @return the command.
	 */
	public Object	getCommand()
	{
		return command;
	}

	/**
	 *  Set the command.
	 *  @param command The result to set.
	 */
	public void setCommand(Object command)
	{
		this.command = command;
	}
}
