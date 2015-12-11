package jadex.platform.service.remote.replacements;

import java.lang.reflect.Proxy;

import jadex.commons.transformation.annotations.Alias;
import jadex.platform.service.remote.IMethodReplacement;

/**
 *  Default replacement code for hashCode() method. 
 */
@Alias("jadex.base.service.remote.replacements.DefaultHashcodeMethodReplacement")
public class DefaultHashcodeMethodReplacement implements IMethodReplacement
{
	/**
	 *  Invoke the method on the given object with the given args.
	 */
	public Object invoke(Object obj, Object[] args)
	{
		// Todo: hash code of proxy info instead of invocation handler?
		return Integer.valueOf(Proxy.getInvocationHandler(obj).hashCode());
	}
}