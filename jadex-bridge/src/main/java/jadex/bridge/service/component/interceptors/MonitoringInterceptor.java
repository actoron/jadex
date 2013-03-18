package jadex.bridge.service.component.interceptors;

import jadex.bridge.Cause;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.component.IServiceInvocationInterceptor;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService;
import jadex.bridge.service.types.monitoring.MonitoringEvent;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  Interceptor that creates service call start / end events and sends
 *  them to the monitoring service.
 */
public class MonitoringInterceptor implements IServiceInvocationInterceptor
{
	/** The component. */
	protected IInternalAccess component;
	
	/** The monitoring service. */
	protected IMonitoringService monser;
	protected long lastsearch;
	protected long delay = 10000;
	
	/**
	 * 
	 */
	public MonitoringInterceptor(IInternalAccess component)
	{
		this.component = component;
	}
	
	/**
	 *  Test if the interceptor is applicable.
	 *  @return True, if applicable.
	 */
	public boolean isApplicable(ServiceInvocationContext context)
	{
		// Do not monitor calls to the monitoring service itself and no constant calls
		boolean ret = !context.getMethod().getDeclaringClass().equals(IMonitoringService.class)
			&& SReflect.isSupertype(IFuture.class, context.getMethod().getReturnType());
	
//		System.out.println("isApp: "+context.getMethod()+" "+ret);
		
		return ret;
	}
	
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture<Void> execute(final ServiceInvocationContext context)
	{
		final Future<Void> ret = new Future<Void>();
		
		getMonitoringService().addResultListener(new ExceptionDelegationResultListener<IMonitoringService, Void>(ret)
		{
			public void customResultAvailable(IMonitoringService monser)
			{
				if(monser!=null)
				{
					// todo: clock?
					long start = System.currentTimeMillis();
					ServiceCall sc = context.getServiceCall();
					Cause cause = sc==null? null: sc.getCause();
					String src = component.getComponentIdentifier().getName()+"."+context.getMethod().getName();
					monser.publishEvent(new MonitoringEvent(src, 
						IMonitoringEvent.TYPE_SERVICECALL_START, cause, start));
				}
				context.invoke().addResultListener(new ReturnValueResultListener(ret, context));
			}
		});
	
		return ret;
	}
	
	/**
	 *  Get or search the monitoring service.
	 */
	protected IFuture<IMonitoringService> getMonitoringService()
	{
		final Future<IMonitoringService> ret = new Future<IMonitoringService>();
		
		if(lastsearch==0 || System.currentTimeMillis()>lastsearch+delay)
		{
			lastsearch = System.currentTimeMillis();

			SServiceProvider.getService(component.getServiceContainer(), IMonitoringService.class)
				.addResultListener(component.createResultListener(new IResultListener<IMonitoringService>()
			{
				public void resultAvailable(IMonitoringService result)
				{
					monser = result;
					ret.setResult(monser);
				}
				
				public void exceptionOccurred(Exception exception)
				{
					ret.setResult(null);
				}
			}));
		}
		else
		{
			ret.setResult(monser);
		}
		
		return ret;
	}
	
	/**
	 *  Listener that handles the end of the call.
	 */
	protected class ReturnValueResultListener extends DelegationResultListener<Void>
	{
		//-------- attributes --------
		
		/** The service invocation context. */
		protected ServiceInvocationContext	sic;
		
		//-------- constructors --------
		
		/**
		 *  Create a result listener.
		 */
		protected ReturnValueResultListener(Future<Void> future, ServiceInvocationContext sic)
		{
			super(future);
			this.sic = sic;
		}
		
		//-------- IResultListener interface --------

		/**
		 *  Called when the service call is finished.
		 */
		public void customResultAvailable(Void result)
		{
			getMonitoringService().addResultListener(new IResultListener<IMonitoringService>()
			{
				public void resultAvailable(IMonitoringService monser)
				{
					if(monser!=null)
					{
						// todo: clock?
						long end = System.currentTimeMillis();
						ServiceCall sc = sic.getServiceCall();
						Cause cause = sc==null? null: sc.getCause();
						String src = component.getComponentIdentifier().getName()+"."+sic.getMethod().getName();
						monser.publishEvent(new MonitoringEvent(src, IMonitoringEvent.TYPE_SERVICECALL_END, cause, end));
					}
					ReturnValueResultListener.super.customResultAvailable(null);
				}
				
				public void exceptionOccurred(Exception exception)
				{
					// never happens
					ReturnValueResultListener.super.exceptionOccurred(exception);
				}
			});
		}
	}
}
