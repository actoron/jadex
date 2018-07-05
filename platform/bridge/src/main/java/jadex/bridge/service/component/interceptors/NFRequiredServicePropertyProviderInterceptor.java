package jadex.bridge.service.component.interceptors;

import java.lang.reflect.Method;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.INFPropertyComponentFeature;
import jadex.bridge.nonfunctional.INFMixedPropertyProvider;
import jadex.bridge.nonfunctional.INFRPropertyProvider;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Delegates 'getRequiredServicePropertyProvider()' calls
 *  to the underlying component.
 */
public class NFRequiredServicePropertyProviderInterceptor extends ComponentThreadInterceptor
{
	protected static final Method METHOD;
	
	static
	{
		try
		{
			METHOD = INFRPropertyProvider.class.getMethod("getRequiredServicePropertyProvider", new Class[0]);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	//-------- methods --------

	/** The service indentifier. */
	protected IServiceIdentifier sid;

	/**
	 *  Create a new interceptor.
	 */
	public NFRequiredServicePropertyProviderInterceptor(IInternalAccess component, IServiceIdentifier sid)
	{
		super(component);
		this.sid = sid;
	}
	
	/**
	 *  Test if the interceptor is applicable.
	 *  @return True, if applicable.
	 */
	public boolean isApplicable(ServiceInvocationContext context)
	{
		return super.isApplicable(context) && context.getMethod().equals(METHOD);
	}
	
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture<Void> execute(ServiceInvocationContext sic)
	{
//		INFMixedPropertyProvider res = component.getRequiredServicePropertyProvider((IServiceIdentifier)sic.getArgumentArray()[0]);
		INFMixedPropertyProvider res = getComponent().getFeature(INFPropertyComponentFeature.class).getRequiredServicePropertyProvider(sid);
		sic.setResult(new Future<INFMixedPropertyProvider>(res));
		return IFuture.DONE;
	}
}