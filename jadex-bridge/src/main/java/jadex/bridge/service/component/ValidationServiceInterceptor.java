package jadex.bridge.service.component;

import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IService;
import jadex.bridge.service.ServiceInvalidException;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 */
public class ValidationServiceInterceptor extends AbstractApplicableInterceptor
{
	//-------- constants --------
	
	/** The static map of subinterceptors (method -> interceptor). */
	protected static Set ALWAYSOK;
	
	static
	{
		try
		{
			ALWAYSOK = new HashSet();
			ALWAYSOK.add(Object.class.getMethod("toString", new Class[0]));
			ALWAYSOK.add(Object.class.getMethod("equals", new Class[]{Object.class}));
			ALWAYSOK.add(Object.class.getMethod("hashCode", new Class[0]));
			ALWAYSOK.add(IService.class.getMethod("getServiceIdentifier", new Class[0]));
			ALWAYSOK.add(IInternalService.class.getMethod("startService", new Class[0]));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture execute(final ServiceInvocationContext sic)
	{
		final Future ret = new Future();
		IService ser = (IService)sic.getObject();
		
		ser.isValid().addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				if(!((Boolean)result).booleanValue() && !ALWAYSOK.contains(sic.getMethod()))
				{
					ret.setException(new ServiceInvalidException(sic.getMethod().getName()));
				}
				else
				{
					sic.invoke().addResultListener(new DelegationResultListener(ret));
				}
			}
		});
		
		return ret;
	}
}
