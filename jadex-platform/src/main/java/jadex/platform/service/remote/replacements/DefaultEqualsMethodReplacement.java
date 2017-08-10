package jadex.platform.service.remote.replacements;

import java.lang.reflect.Proxy;

import jadex.bridge.ProxyFactory;
import jadex.commons.transformation.annotations.Alias;
import jadex.platform.service.remote.IMethodReplacement;

/**
 *  Default replacement code for equals() method.
 */
@Alias("jadex.base.service.remote.replacements.DefaultEqualsMethodReplacement")
public class DefaultEqualsMethodReplacement implements IMethodReplacement
{
	/**
	 *  Invoke the method on the given object with the given args.
	 */
	public Object invoke(Object obj, Object[] args)
	{
		// Todo: compare proxy infos instead of invocation handlers?
		return Boolean.valueOf(args[0]!=null && Proxy.isProxyClass(args[0].getClass())
			&& ProxyFactory.getInvocationHandler(obj).equals(ProxyFactory.getInvocationHandler(args[0])));
	}
}