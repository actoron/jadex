package jadex.bridge.component;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.commons.future.IFuture;

/**
 *  Interface for remotely executable commands.
 */
public interface IRemoteCommand<T>
{
	/**
	 *  Execute a command.
	 *  @param access The agent that is running the command.
	 *  @param secinf The established security level to e.g. decide if the command is allowed.
	 *  @return A future for return value(s). May also be intermediate, subscription, etc.
	 */
	public IFuture<T>	execute(IInternalAccess access, IMsgSecurityInfos secinf);

	/**
	 *  Get the return type.
	 *  @return A class representing a future interface for mapping the result of the command.
	 */
	public Class<? extends IFuture<T>>	getReturnType(IInternalAccess access);
}
