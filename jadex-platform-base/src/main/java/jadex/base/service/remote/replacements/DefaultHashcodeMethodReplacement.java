package jadex.base.service.remote.replacements;

import jadex.base.service.remote.IMethodReplacement;

import java.lang.reflect.Proxy;

/**
 *  Default replacement code for hashCode() method. 
 */
public class DefaultHashcodeMethodReplacement implements IMethodReplacement
{
	/**
	 *  Invoke the method on the given object with the given args.
	 */
	public Object invoke(Object obj, Object[] args)
	{
		// Todo: hash code of proxy info instead of invocation handler?
		return new Integer(Proxy.getInvocationHandler(obj).hashCode());
	}
}