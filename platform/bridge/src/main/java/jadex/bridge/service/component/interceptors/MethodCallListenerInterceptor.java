package jadex.bridge.service.component.interceptors;

import java.util.Collection;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.MethodInfo;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Interceptor for observing method calls start and end e.g. for timing.
 */
public class MethodCallListenerInterceptor extends ComponentThreadInterceptor
{
	//-------- methods --------

	/** The service indentifier. */
	protected IServiceIdentifier sid;
	
	/**
	 *  Create a new interceptor.
	 */
	public MethodCallListenerInterceptor(IInternalAccess component, IServiceIdentifier sid)
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
		// Interceptor is used in both chains, provided and required
//		if(context.getMethod().getName().indexOf("methodA")!=-1)
//			System.out.println("interceptor: "+component.getComponentIdentifier());
//		boolean ret = component.getServiceContainer().hasMethodListeners(sid, new MethodInfo(context.getMethod()));
		boolean ret = super.isApplicable(context) && getComponent().getFeature(IProvidedServicesFeature.class).hasMethodListeners(sid, new MethodInfo(context.getMethod()));
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
		getComponent().getFeature(IProvidedServicesFeature.class).notifyMethodListeners(sid, true, null, sic.getMethod(), sic.getArgumentArray(), sic.hashCode(), sic);
		sic.invoke().addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
//				if(sic.getMethod().getName().indexOf("destroy")!=-1)
//					System.out.println("hhhhuuuuu");
//				System.out.println("method call lis end: "+sic.hashCode());
				Object	res	= sic.getResult();
				if(res instanceof IIntermediateFuture)
				{
					IIntermediateResultListener<Object> lis = new IIntermediateResultListener<Object>()
					{
						public void intermediateResultAvailable(Object result)
						{
						}
							
						public void finished()
						{
							getComponent().getFeature(IProvidedServicesFeature.class).notifyMethodListeners(sid, false, null, sic.getMethod(), sic.getArgumentArray(), sic.hashCode(), sic);
						}
						
						public void resultAvailable(Collection<Object> result)
						{
							getComponent().getFeature(IProvidedServicesFeature.class).notifyMethodListeners(sid, false, null, sic.getMethod(), sic.getArgumentArray(), sic.hashCode(), sic);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							getComponent().getFeature(IProvidedServicesFeature.class).notifyMethodListeners(sid, false, null, sic.getMethod(), sic.getArgumentArray(), sic.hashCode(), sic);
						}
					};
					
					if(res instanceof ISubscriptionIntermediateFuture)
					{
						((ISubscriptionIntermediateFuture)res).addQuietListener(lis);
					}
					else
					{
						((IIntermediateFuture)res).addResultListener(lis);
					}
				}
				else if(res instanceof IFuture)
				{
					((IFuture<Object>)res).addResultListener(new IResultListener<Object>()
					{
						public void resultAvailable(Object result)
						{
							getComponent().getFeature(IProvidedServicesFeature.class).notifyMethodListeners(sid, false, null, sic.getMethod(), sic.getArgumentArray(), sic.hashCode(), sic);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							getComponent().getFeature(IProvidedServicesFeature.class).notifyMethodListeners(sid, false, null, sic.getMethod(), sic.getArgumentArray(), sic.hashCode(), sic);
						}
					});
				}
				else
				{
					getComponent().getFeature(IProvidedServicesFeature.class).notifyMethodListeners(sid, false, null, sic.getMethod(), sic.getArgumentArray(), sic.hashCode(), sic);
				}
				super.customResultAvailable(result);
			}
		});
		return ret;
	}
}