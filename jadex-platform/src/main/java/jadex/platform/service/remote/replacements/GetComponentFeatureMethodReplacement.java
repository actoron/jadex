package jadex.platform.service.remote.replacements;

import jadex.platform.service.remote.IMethodReplacement;

import java.lang.reflect.Proxy;

/**
 * 
 */
public class GetComponentFeatureMethodReplacement implements IMethodReplacement
{
	/**
	 *  Invoke the method on the given object with the given args.
	 */
	public Object invoke(Object obj, Object[] args)
	{
		return Proxy.newProxyInstance(obj.getClass().getClassLoader(), new Class[]{(Class<?>)args[0]}, Proxy.getInvocationHandler(obj));
	}
}