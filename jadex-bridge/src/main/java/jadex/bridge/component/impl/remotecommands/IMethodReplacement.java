package jadex.bridge.component.impl.remotecommands;

/**
 *  Interface to be implemented by the user
 *  for replacing a remote method with custom code.
 */
public interface IMethodReplacement
{
	/**
	 *  Invoke the method on the given object with the given args.
	 */
	public Object	invoke(Object obj, Object[] args);
}
