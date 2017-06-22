package jadex.bridge.component.impl;

import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.IFuture;

/**
 *  Feature for securely sending and handling remote execution commands.
 *  Internal methods, e.g., for platform-specific commands.
 */
public interface IInternalRemoteExecutionFeature
{
	/**
	 *  Execute a command on a remote agent.
	 *  @param target	The component to send the command to.
	 *  @param command	The command to be executed.
	 *  @return	The result(s) of the command, if any.
	 */
	public <T> IFuture<Set<T>>	executeRemoteSearch(IComponentIdentifier target, ServiceQuery<T> query);
}
