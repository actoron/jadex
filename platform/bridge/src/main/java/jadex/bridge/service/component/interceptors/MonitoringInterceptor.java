package jadex.bridge.service.component.interceptors;

import jadex.bridge.Cause;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.component.impl.AbstractComponentFeature;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.bridge.service.types.monitoring.MonitoringEvent;
import jadex.commons.SReflect;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Interceptor that creates service call start / end events and sends
 *  them to the monitoring service.
 */
public class MonitoringInterceptor extends ComponentThreadInterceptor
{
//	/** The service getter. */
//	protected ServiceGetter<IMonitoringService> getter;
	
	/**
	 *  Create a new interceptor.
	 */
	public MonitoringInterceptor(IInternalAccess component)
	{
		super(component);
//		this.getter = new ServiceGetter<IMonitoringService>(component, IMonitoringService.class, RequiredServiceInfo.SCOPE_PLATFORM);
	}
	
	/**
	 *  Test if the interceptor is applicable.
	 *  @return True, if applicable.
	 */
	public boolean isApplicable(ServiceInvocationContext context)
	{
		boolean ret = super.isApplicable(context);
		
		if(ret)
		{
			// Do not monitor calls to the monitoring service itself and no constant calls
			
			Boolean mon = (Boolean)context.getNextServiceCall().getProperty(ServiceCall.MONITORING);
			
			if(mon!=null)
			{
				ret = mon.booleanValue();
			}
			else
			{
				ret = !context.getMethod().getDeclaringClass().equals(IMonitoringService.class)
					&& SReflect.isSupertype(IFuture.class, context.getMethod().getReturnType());
				//&& context.getMethod().getName().indexOf("getChildren")==-1;
			}
			
//			System.out.println("isApp: "+context.getMethod()+" "+ret);
	//
//			if(context.getMethod().getName().indexOf("isValid")!=-1)
//				System.out.println("gggggg");
			
	//		if(ret)
	//			System.out.println("ok: "+context.getMethod().getDeclaringClass()+"."+context.getMethod().getName());
					
	//		if(context.getMethod().getName().indexOf("getExternalAccess")!=-1)
	//			System.out.println("getExt");g
		}
		
		return ret;
	}
	
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture<Void> execute(final ServiceInvocationContext context)
	{
		final Future<Void> ret = new Future<Void>();
		
		IMonitoringComponentFeature	feat	= getComponent().getFeature0(IMonitoringComponentFeature.class);
		
		if(feat!=null && feat.hasEventTargets(PublishTarget.TOALL, PublishEventLevel.MEDIUM))
		{
			// Hack, necessary because getService() is not a service call and the first contained
			// service call (getChildren) will reset the call context afterwards :-(
//			final ServiceCall cur = CallAccess.getCurrentInvocation();
//			final ServiceCall next = CallAccess.getNextInvocation();
//			Map<String, Object>	props	= new HashMap<String, Object>();
	//		props.put("method", context.getMethod().getName());
//			ServiceCall sc = CallAccess.getOrCreateNextInvocation(props);
//			sc.setProperty(ServiceCall.MONITORING, Boolean.FALSE);
//			sc.setProperty(ServiceCall.INHERIT, Boolean.TRUE);
//			CallAccess.setCurrentInvocation(sc); 
			
	//		if(context.getMethod().getName().equals("shutdownService") && component.getComponentIdentifier().getParent()==null)
	//			System.out.println("start shut in mon: "+context.getObject());
			
	//		if(context.getMethod().getName().indexOf("log")!=-1)
	//			System.out.println("log");
			IMonitoringService monser = ((AbstractComponentFeature)feat).getRawService(IMonitoringService.class);
			if(monser!=null)
			{
				long start = System.currentTimeMillis();
				ServiceCall sc = context.getNextServiceCall();
				Cause cause = sc==null? null: sc.getCause();
				String info = context.getMethod().getDeclaringClass().getName()+"."+context.getMethod().getName();
//					info += context.getArguments();
				// Todo: creation time.
				MonitoringEvent ev = new MonitoringEvent(getComponent().getIdentifier(), 0 /*getComponent().getComponentDescription().getCreationTime()*/,
					info, IMonitoringEvent.TYPE_SERVICECALL_START, cause, start, PublishEventLevel.MEDIUM);
				
//					if(context.getMethod().getName().indexOf("method")!=-1)
//						System.out.println("call method: "+ev.getCause().getChainId());
				
				monser.publishEvent(ev).addResultListener(new DefaultResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
					}
				});
			}
		}
			
		context.invoke().addResultListener(new ReturnValueResultListener(ret, context));
			
//			getter.getService().addResultListener(new ExceptionDelegationResultListener<IMonitoringService, Void>(ret)
//			{
//				public void customResultAvailable(IMonitoringService monser)
//				{
//	//				if(context.getMethod().getName().indexOf("log")!=-1)
////						System.out.println("log");
//					
//	//				if(context.getMethod().getName().equals("shutdownService") && component.getComponentIdentifier().getParent()==null)
//	//					System.out.println("end shut in mon: "+context.getObject());
//					
//					CallAccess.setCurrentInvocation(cur); 
//					CallAccess.setNextInvocation(next);
//					
//					// Publish event if monitoring service was found
//					if(monser!=null)
//					{
//						// todo: clock?
//	//					if(context.getMethod().getName().indexOf("test")!=-1)
//	//						System.out.println("test call context: "+context.getServiceCall());
//						long start = System.currentTimeMillis();
//						ServiceCall sc = context.getServiceCall();
//						Cause cause = sc==null? null: sc.getCause();
//						String info = context.getMethod().getDeclaringClass().getName()+"."+context.getMethod().getName();
//	//					info += context.getArguments();
//						// Todo: creation time.
//						MonitoringEvent ev = new MonitoringEvent(getComponent().getComponentIdentifier(), 0 /*getComponent().getComponentDescription().getCreationTime()*/,
//							info, IMonitoringEvent.TYPE_SERVICECALL_START, cause, start, PublishEventLevel.MEDIUM);
//						
//	//					if(context.getMethod().getName().indexOf("method")!=-1)
//	//						System.out.println("call method: "+ev.getCause().getChainId());
//						
//						
//						monser.publishEvent(ev).addResultListener(new ExceptionResultListener<Void>()
//						{
//							public void exceptionOccurred(Exception e)
//							{
//								// Reset mon service if error on publish
//								getter.resetService();
//							}
//						});
//					}
////					else
////					{
////						System.out.println("monitoring service not found");
////					}
//					
//					context.invoke().addResultListener(new ReturnValueResultListener(ret, context));
//				}
//			});
//		}
//		else
//		{
//			context.invoke().addResultListener(new ReturnValueResultListener(ret, context));
//		}
		
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
			IMonitoringComponentFeature	feat	= getComponent().getFeature0(IMonitoringComponentFeature.class);
			
			if(feat!=null && feat.hasEventTargets(PublishTarget.TOALL, PublishEventLevel.MEDIUM))
			{
				// Hack, necessary because getService() is not a service call and the first contained
				// service call (getChildren) will reset the call context afterwards :-(
//				final ServiceCall cur = CallAccess.getCurrentInvocation();
//				final ServiceCall next = CallAccess.getNextInvocation();
	//			Map<String, Object>	props	= new HashMap<String, Object>();
	//			props.put("method5", sic.getMethod().getName());
//				ServiceCall sc = CallAccess.getOrCreateNextInvocation();
//				sc.setProperty(ServiceCall.MONITORING, Boolean.FALSE);
//				sc.setProperty(ServiceCall.INHERIT, Boolean.TRUE);
//				CallAccess.setCurrentInvocation(sc); 
	
				IMonitoringService monser = ((AbstractComponentFeature)feat).getRawService(IMonitoringService.class);
				if(monser!=null)
				{
					long end = System.currentTimeMillis();
					ServiceCall sc = sic.getNextServiceCall();
					Cause cause = sc==null? null: sc.getCause();
					// Todo: creation time.
					monser.publishEvent(new MonitoringEvent(getComponent().getIdentifier(), 0 /*getComponent().getComponentDescription().getCreationTime()*/,
						sic.getMethod().getDeclaringClass().getName()+"."+sic.getMethod().getName(), IMonitoringEvent.TYPE_SERVICECALL_END, cause, end, PublishEventLevel.MEDIUM));
				}
			}
			ReturnValueResultListener.super.customResultAvailable(null);
				
//				getter.getService().addResultListener(new IResultListener<IMonitoringService>()
//				{
//					public void resultAvailable(IMonitoringService monser)
//					{
//						CallAccess.setCurrentInvocation(cur); 
//						CallAccess.setNextInvocation(next);
//	
//						if(monser!=null)
//						{
//							// todo: clock?
//							long end = System.currentTimeMillis();
//							ServiceCall sc = sic.getServiceCall();
//							Cause cause = sc==null? null: sc.getCause();
//							// Todo: creation time.
//							monser.publishEvent(new MonitoringEvent(getComponent().getComponentIdentifier(), 0 /*getComponent().getComponentDescription().getCreationTime()*/,
//								sic.getMethod().getDeclaringClass().getName()+"."+sic.getMethod().getName(), IMonitoringEvent.TYPE_SERVICECALL_END, cause, end, PublishEventLevel.MEDIUM));
//						}
//						ReturnValueResultListener.super.customResultAvailable(null);
//					}
//					
//					public void exceptionOccurred(Exception exception)
//					{
//						CallAccess.setCurrentInvocation(cur); 
//						CallAccess.setNextInvocation(next);
//	
//						// never happens
//						ReturnValueResultListener.super.exceptionOccurred(exception);
//					}
//				});
//			}
//			else
//			{
//				ReturnValueResultListener.super.customResultAvailable(null);
//			}
		}
	}
}
