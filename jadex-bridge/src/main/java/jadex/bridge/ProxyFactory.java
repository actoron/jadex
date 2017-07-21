package jadex.bridge;

import java.lang.reflect.InvocationHandler;

/**
 *  Create a proxy with standard Java or per Jadex ASM.
 */
public class ProxyFactory
{
	public static boolean useasm = false;
	
	/**
     * Returns an instance of a proxy class for the specified interfaces
     * that dispatches method invocations to the specified invocation
     * handler.
     */
    public static Object newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h)
    {
    	if(useasm)
    	{
    		return jadex.bytecode.Proxy.newProxyInstance(loader, interfaces, h);
    	}
    	else
    	{
    		return java.lang.reflect.Proxy.newProxyInstance(loader, interfaces, h);
    	}
    }
}
