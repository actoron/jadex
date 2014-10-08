package jadex.bridge;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 
 */
public abstract class ExternalFeatureProvider
{
	/**
	 *  Get an external interface feature.
	 *  @param type The interface type of the feature.
	 *  @return The feature.
	 */
	public <T> T getExternalComponentFeature(Class<T> type)
	{
		System.out.println("called: "+type);
		return (T)Proxy.newProxyInstance(getInternalAccess().getClassLoader(), new Class[]{type}, new FeatureInvocationHandler(type));
	}
	
	/**
	 *  Handler for feature invocations.
	 */
	public class FeatureInvocationHandler implements InvocationHandler
	{
		/** The type. */
		protected Class<?> type;
		
		/**
		 *  Create a new handler.
		 */
		public FeatureInvocationHandler(Class<?> type)
		{
			this.type = type;
		}
		
		/**
		 *  Invoke from feature flyweights.
		 *  @param proxy The object.
		 *  @param method The method.
		 *  @param args The arguments.
		 *  @return The result of the call.
		 *  @throws Throwable
		 */
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
		{
			Object target = getInternalAccess().getComponentFeature(type);
			return method.invoke(target, args);
		}
	}
	
	/**
	 * 
	 */
	public abstract IInternalAccess getInternalAccess();
}
