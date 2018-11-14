package jadex.bridge.component.impl.remotecommands;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.impl.IRemoteConversationCommand;
import jadex.bridge.service.types.security.ISecurityInfo;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITerminableFuture;

/**
 *  Command for future termmination.
 */
public class RemoteTerminationCommand<T> implements IRemoteConversationCommand<T>
{
	/** The termination reason (if any). */
	protected Exception reason;
	
	/**
	 *  Create the command.
	 */
	public RemoteTerminationCommand()
	{
	}
	
	/**
	 *  Create the command.
	 */
	public RemoteTerminationCommand(Exception reason)
	{
		this.reason = reason;
	}
	
	/**
	 *  Execute a command.
	 *  @param access The agent to run the command on.
	 *  @param future Future of the active conversation.
	 *  @param secinf The established security level to decide if the command is allowed.
	 */
	public void	execute(IInternalAccess access, IFuture<T> future, ISecurityInfo secinf)
	{
		if (reason!=null)
		{
			((ITerminableFuture<T>)future).terminate(reason);
		}
		else
		{
			((ITerminableFuture<T>)future).terminate();
		}
	}
	
	/**
	 *  Get the reason.
	 *  @return The reason.
	 */
	public Exception getReason()
	{
		return reason;
	}

	/**
	 *  Set the reason.
	 *  @param reason The reason to set.
	 */
	public void setReason(Exception reason)
	{
		this.reason = reason;
	}
	
}
