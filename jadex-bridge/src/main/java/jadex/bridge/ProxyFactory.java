package jadex.bridge;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import jadex.commons.SReflect;

/**
 *  Create a proxy with standard Java or per Jadex ASM.
 */
public class ProxyFactory
{
	public static boolean useasm = true;
	
	/**
     * Returns an instance of a proxy class for the specified interfaces
     * that dispatches method invocations to the specified invocation
     * handler.
     */
    public static Object newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h)
    {
    	if(useasm && !SReflect.isAndroid())
    	{
    		try
    		{
    			return jadex.bytecode.Proxy.newProxyInstance(loader, interfaces, h);
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    			throw new RuntimeException(e);
    		}
    	}
    	else
    	{
    		return java.lang.reflect.Proxy.newProxyInstance(loader, interfaces, h);
    	}
    }
    
    /**
     *  Get the invocation handler of a proxy.
     *  @param proxy
     *  @return The handler
     */
    public static InvocationHandler getInvocationHandler(Object proxy) throws IllegalArgumentException
    {
    	if(proxy!=null && isASMProxyClass(proxy.getClass()))
    	{
    		return jadex.bytecode.Proxy.getInvocationHandler(proxy);
    	}
    	else 
    	{
    		return java.lang.reflect.Proxy.getInvocationHandler(proxy);    	
    	}
    }
    
    /**
     * Returns true if and only if the specified class was dynamically
     * generated to be a proxy class using the {@code getProxyClass}
     * method or the {@code newProxyInstance} method.
     *
     * <p>The reliability of this method is important for the ability
     * to use it to make security decisions, so its implementation should
     * not just test if the class in question extends {@code Proxy}.
     *
     * @param   cl the class to test
     * @return  {@code true} if the class is a proxy class and
     *          {@code false} otherwise
     * @throws  NullPointerException if {@code cl} is {@code null}
     */
    public static boolean isProxyClass(Class<?> cl) 
    {
    	return isASMProxyClass(cl) ||
    		Proxy.isProxyClass(cl);
    }
    
    /**
     *  Test if it is a ASM proxy class.
     *  @param cl The class.
     *  @return True, if is asm proxy class.
     */
    public static boolean isASMProxyClass(Class<?> cl) 
    {
    	return SReflect.getField(cl, "isproxy")!=null;
    }
}
