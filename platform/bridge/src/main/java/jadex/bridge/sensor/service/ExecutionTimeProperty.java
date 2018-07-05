package jadex.bridge.sensor.service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import jadex.bridge.IInternalAccess;
import jadex.bridge.ProxyFactory;
import jadex.bridge.sensor.time.TimedProperty;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.MethodInfo;
import jadex.commons.future.IFuture;

/**
 *  Property for the overall execution time of a method or a service.
 */
public class ExecutionTimeProperty extends TimedProperty
{
	/** The name of the property. */
	public static final String NAME = "waiting time";
	
	/** The service identifier. */
	protected IServiceIdentifier sid;
	
	/** The listener. */
	protected IMethodInvocationListener listener;
	
	/** The method info. */
	protected MethodInfo method;
	
	/** The clock. */
	protected IClockService clock;
	
	/**
	 *  Create a new property.
	 */
	public ExecutionTimeProperty(final IInternalAccess comp, IService service, MethodInfo method)
	{
		super(NAME, comp, true);
		this.method = method;
		
		if(service!=null)
		{
			this.sid = service.getServiceIdentifier();
		
			clock = comp.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IClockService.class));
		
			if(ProxyFactory.isProxyClass(service.getClass()))
			{
				listener = new UserMethodInvocationListener(new IMethodInvocationListener()
				{
					Map<Object, Long> times = new HashMap<Object, Long>();
					
					public void methodCallStarted(Object proxy, Method method, Object[] args, Object callid, ServiceInvocationContext context)
					{
						if(clock!=null)
							times.put(callid, Long.valueOf(clock.getTime()));
					}
					
					public void methodCallFinished(Object proxy, Method method, Object[] args, Object callid, ServiceInvocationContext context)
					{
						if(clock!=null)
						{
							Long start = times.remove(callid);
							// May happen that property is added during ongoing call
							if(start!=null)
							{
								long dur = clock.getTime() - start.longValue();
								setValue(dur);
							}
						}
					}
				});
	//			System.out.println("installing lis: "+comp.getComponentIdentifier().getName());
				comp.getFeature(IProvidedServicesFeature.class).addMethodInvocationListener(service.getServiceIdentifier(), method, listener);
			}
			else
			{
				throw new RuntimeException("Cannot install waiting time listener hook.");
			}
		}
	}
	
	/**
	 *  Measure the value.
	 */
	public Long measureValue()
	{
		return null;
//		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Set the value.
	 */
	public void setValue(Long value) 
	{
//		System.out.println("Set value: "+value);
		
		// ema calculatio: EMAt = EMAt-1 +(SF*(Ct-EMAt-1)) SF=2/(n+1)
		if(this.value!=null && value!=null)
		{
			double sf = 2d/(10d+1); // 10 periods per default
			double delta = value-this.value;
			value = Long.valueOf((long)(this.value+sf*delta));
		}
		
		if(value!=null)
		{
//			System.out.println("Setting exp value: "+value);
			super.setValue(value);
		}
	}
	
	/**
	 *  Property was removed and should be disposed.
	 */
	public IFuture<Void> dispose()
	{
		comp.getFeature(IProvidedServicesFeature.class).removeMethodInvocationListener(sid, method, listener);
		return IFuture.DONE;
	}
}
