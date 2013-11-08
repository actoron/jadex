package jadex.bridge.service.component.interceptors;

import jadex.bridge.service.component.IServiceInvocationInterceptor;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.collection.LRU;

import java.lang.reflect.Method;

/**
 *  Abstract interceptor class that uses a LRU for caching applicable states of invocations
 *  for subsequent calls. Should be used whenever isApplicable method is expensive.
 */
public abstract class AbstractLRUApplicableInterceptor implements IServiceInvocationInterceptor
{
	/** The LRU. */
	protected LRU<Method, Boolean> applicables = new LRU<Method, Boolean>(50);
	
	/**
	 *  Test if the interceptor is applicable.
	 *  @return True, if applicable.
	 */
	public final boolean isApplicable(ServiceInvocationContext context)
	{
		boolean ret = false;
		Boolean app = applicables.get(context.getMethod());
		if(app!=null)
		{
			ret = app.booleanValue();
		}
		else
		{
			ret = customIsApplicable(context);
			applicables.put(context.getMethod(), ret? Boolean.TRUE: Boolean.FALSE);
		}
		return ret;
	}
	
	/**
	 *  Replacement method for isApplicable.
	 */
	public abstract boolean customIsApplicable(ServiceInvocationContext context);
}