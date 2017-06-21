package jadex.bridge.component;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Interface for remotely executably commands.
 */
public interface IRemoteCommand<T>
{
	/**
	 *  Execute a command.
	 *  @param access The agent to run the command on.
	 *  @param future Future of the active command.
	 *  @param secinf The established security level to decide if the command is allowed.
	 *  @return A return value to be sent back.
	 */
	public IFuture<T>	execute(IInternalAccess access, Future<T> future, IMsgSecurityInfos secinf);
}
