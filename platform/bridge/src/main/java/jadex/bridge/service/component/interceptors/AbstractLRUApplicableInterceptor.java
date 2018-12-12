package jadex.bridge.service.component.interceptors;

import java.lang.reflect.Method;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.collection.LRU;

/**
 *  Abstract interceptor class that uses a LRU for caching applicable states of invocations
 *  for subsequent calls. Should be used whenever isApplicable method is expensive.
 */
public abstract class AbstractLRUApplicableInterceptor extends ComponentThreadInterceptor
{
	/** The LRU. */
	protected LRU<Method, Boolean> applicables = new LRU<Method, Boolean>(50);
	
	/**
	 *  Create a new AbstractLRUApplicableInterceptor. 
	 */
	public AbstractLRUApplicableInterceptor(IInternalAccess ia)
	{
		super(ia);
	}

	/**
	 *  Test if the interceptor is applicable.
	 *  @return True, if applicable.
	 */
	public final boolean isApplicable(ServiceInvocationContext context)
	{
		boolean ret = false;
		if(super.isApplicable(context))
		{
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
		}
		return ret;
	}
	
	/**
	 *  Replacement method for isApplicable.
	 */
	public abstract boolean customIsApplicable(ServiceInvocationContext context);
}