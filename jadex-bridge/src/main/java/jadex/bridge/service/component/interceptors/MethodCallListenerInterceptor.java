package jadex.bridge.service.component.interceptors;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.MethodInfo;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  Calls a methods on an object and returns the result.
 */
public class MethodCallListenerInterceptor extends ComponentThreadInterceptor
{
	//-------- methods --------

	/** The service indentifier. */
	protected IServiceIdentifier sid;
	
	/** The service container. */
	protected IServiceContainer container;

	/**
	 *  Create a new interceptor.
	 */
	public MethodCallListenerInterceptor(IInternalAccess component, IServiceIdentifier sid)
	{
		super(component);
		this.sid = sid;
		this.container = component.getServiceContainer();
	}
	
	/**
	 *  Test if the interceptor is applicable.
	 *  @return True, if applicable.
	 */
	public boolean isApplicable(ServiceInvocationContext context)
	{
		// Interceptor is used in both chains, provided and required
//		if(context.getMethod().getName().indexOf("methodA")!=-1)
//			System.out.println("interceptor: "+component.getComponentIdentifier());
//		boolean ret = component.getServiceContainer().hasMethodListeners(sid, new MethodInfo(context.getMethod()));
		boolean ret = super.isApplicable(context) && container.hasMethodListeners(sid, new MethodInfo(context.getMethod()));
//		System.out.println("app: "+context.getMethod().getName()+" "+ret);
		return ret;
	}
	
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture<Void> execute(final ServiceInvocationContext sic)
	{
//		System.out.println("method call lis start: "+sic.hashCode());
		Future<Void> ret = new Future<Void>();
		container.notifyMethodListeners(sid, true, null, sic.getMethod(), sic.getArgumentArray(), sic.hashCode(), sic);
		sic.invoke().addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
//				System.out.println("method call lis end: "+sic.hashCode());
				Object	res	= sic.getResult();
				if(res instanceof IFuture)
				{
					((IFuture<Object>)res).addResultListener(new IResultListener<Object>()
					{
						public void resultAvailable(Object result)
						{
							getComponent().getServiceContainer().notifyMethodListeners(sid, false, null, sic.getMethod(), sic.getArgumentArray(), sic.hashCode(), sic);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							getComponent().getServiceContainer().notifyMethodListeners(sid, false, null, sic.getMethod(), sic.getArgumentArray(), sic.hashCode(), sic);
						}
					});
				}
				else
				{
					getComponent().getServiceContainer().notifyMethodListeners(sid, false, null, sic.getMethod(), sic.getArgumentArray(), sic.hashCode(), sic);
				}
				super.customResultAvailable(result);
			}
		});
		return ret;
	}
}