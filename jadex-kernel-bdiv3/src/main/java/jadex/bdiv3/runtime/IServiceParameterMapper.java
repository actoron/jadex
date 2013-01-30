package jadex.bdiv3.runtime;

import java.lang.reflect.Method;

/**
 * 
 */
public interface IServiceParameterMapper<T>
{
	/**
	 *  Create service parameters.
	 */
	public Object[] createServiceParameters(T obj, Method m);
	
	/**
	 *  Create service result.
	 */
	public void handleServiceResult(T obj, Method m, Object result);
}
