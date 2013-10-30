package jadex.bridge.service.component.interceptors;

import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.INFMixedPropertyProvider;
import jadex.bridge.nonfunctional.INFRPropertyProvider;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.lang.reflect.Method;

/**
 *  Calls a methods on an object and returns the result.
 */
public class NFRequiredServicePropertyProviderInterceptor extends AbstractApplicableInterceptor
{
	protected static Method METHOD;
	
	static
	{
		try
		{
			METHOD = INFRPropertyProvider.class.getMethod("getRequiredServicePropertyProvider", new Class[0]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//-------- methods --------

	/** The component. */
	protected IInternalAccess component;
	
	/** The service indentifier. */
	protected IServiceIdentifier sid;

	/**
	 *  Create a new interceptor.
	 */
	public NFRequiredServicePropertyProviderInterceptor(IInternalAccess component, IServiceIdentifier sid)
	{
		this.component = component;
		this.sid = sid;
	}
	
	/**
	 *  Test if the interceptor is applicable.
	 *  @return True, if applicable.
	 */
	public boolean isApplicable(ServiceInvocationContext context)
	{
		return context.getMethod().equals(METHOD);
	}
	
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture<Void> execute(ServiceInvocationContext sic)
	{
//		INFMixedPropertyProvider res = component.getRequiredServicePropertyProvider((IServiceIdentifier)sic.getArgumentArray()[0]);
		INFMixedPropertyProvider res = component.getServiceContainer().getRequiredServicePropertyProvider(sid);
		sic.setResult(new Future<INFMixedPropertyProvider>(res));
		return IFuture.DONE;
	}
}