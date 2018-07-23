package jadex.bridge.component.impl.remotecommands;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.impl.IRemoteConversationCommand;
import jadex.bridge.service.types.security.ISecurityInfo;
import jadex.commons.future.IBackwardCommandFuture;
import jadex.commons.future.IFuture;

/**
 *  Command for sending backward command data.
 */
public class RemoteBackwardCommand<T> implements IRemoteConversationCommand<T>
{
	/** The backward command info. */
	protected Object info;
	
	/**
	 *  Create the command.
	 */
	public RemoteBackwardCommand()
	{
		// Bean constructor
	}
	
	/**
	 *  Create the command.
	 */
	public RemoteBackwardCommand(Object info)
	{
		this.info = info;
	}
	
	/**
	 *  Execute a command.
	 *  @param access The agent to run the command on.
	 *  @param future Future of the active conversation.
	 *  @param secinf The established security level to decide if the command is allowed.
	 */
	public void	execute(IInternalAccess access, IFuture<T> future, ISecurityInfo secinf)
	{
		((IBackwardCommandFuture)future).sendBackwardCommand(info);
	}
	
	/**
	 *  Get the info.
	 *  @return The info.
	 */
	public Object getInfo()
	{
		return info;
	}

	/**
	 *  Set the info.
	 *  @param info The info to set.
	 */
	public void setInfo(Object info)
	{
		this.info = info;
	}
	
}
