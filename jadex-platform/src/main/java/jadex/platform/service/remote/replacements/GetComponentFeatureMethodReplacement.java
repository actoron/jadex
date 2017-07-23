package jadex.platform.service.remote.replacements;

import java.lang.reflect.Proxy;

import jadex.bridge.ProxyFactory;
import jadex.platform.service.remote.IMethodReplacement;

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
		return ProxyFactory.newProxyInstance(obj.getClass().getClassLoader(), new Class[]{(Class<?>)args[0]}, ProxyFactory.getInvocationHandler(obj));
	}
}