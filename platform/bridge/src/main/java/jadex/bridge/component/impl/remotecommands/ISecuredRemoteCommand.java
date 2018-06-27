package jadex.bridge.component.impl.remotecommands;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Security;

/**
 *  Remote command that can provide custom security settings
 *  for being checked before execution.
 */
public interface ISecuredRemoteCommand
{
	/**
	 *  Method to provide the required security level.
	 *  @return The security settings or null to inhibit execution.
	 */
	public Security getSecurityLevel(IInternalAccess access);
}
