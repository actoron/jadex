package jadex.bridge.component;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.security.ISecurityInfo;
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
	public IFuture<T>	execute(IInternalAccess access, ISecurityInfo secinf);
	
	/**
	 *  Checks if the remote command is internally valid.
	 * 
	 *  @param access The component access.
	 *  @return Exception describing the error if invalid.
	 */
	public Exception isValid(IInternalAccess access);
}
