package jadex.base.service.remote.replacements;

import jadex.base.service.remote.IMethodReplacement;

import java.lang.reflect.Proxy;

/**
 *  Default replacement code for equals() method.
 */
public class DefaultEqualsMethodReplacement implements IMethodReplacement
{
	/**
	 *  Invoke the method on the given object with the given args.
	 */
	public Object invoke(Object obj, Object[] args)
	{
		// Todo: compare proxy infos instead of invocation handlers?
		return new Boolean(args[0]!=null && Proxy.isProxyClass(args[0].getClass())
			&& Proxy.getInvocationHandler(obj).equals(Proxy.getInvocationHandler(args[0])));
	}
}