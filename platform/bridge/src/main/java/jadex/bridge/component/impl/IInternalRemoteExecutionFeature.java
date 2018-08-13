package jadex.bridge.component.impl;

import java.lang.reflect.Method;

import jadex.bridge.component.impl.remotecommands.RemoteReference;
import jadex.commons.future.IFuture;

/**
 *  Feature for securely sending and handling remote execution commands.
 *  Internal methods, e.g., for platform-specific commands.
 */
public interface IInternalRemoteExecutionFeature
{
	/**
	 *  Invoke a method on a remote object.
	 *  @param ref	The target reference.
	 *  @param method	The method to be executed.
	 *  @param args	The arguments.
	 *  @return	The result(s) of the method invocation, if any. Connects any futures involved.
	 */
	public <T> IFuture<T>	executeRemoteMethod(RemoteReference ref, Method method, Object[] args);
}
