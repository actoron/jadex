package jadex.bridge.sensor.service;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.commons.MethodInfo;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 *  Property for the waiting time of a method.
 */
public class MethodWaitingTimeProperty extends TimedProperty
{
	/** The name of the property. */
	public static final String WAITINGTIME = "waiting time";
	
	/**
	 *  Create a new property.
	 */
	public  MethodWaitingTimeProperty(IInternalAccess comp, IService service, MethodInfo method)
	{
		super(WAITINGTIME, comp, -1);
		
		if(Proxy.isProxyClass(service.getClass()))
		{
			BasicServiceInvocationHandler handler = (BasicServiceInvocationHandler)Proxy.getInvocationHandler(service);
			handler.addMethodListener(method, new IMethodInvocationListener()
			{
				Map<Long, Long> times = new HashMap<Long, Long>();
				
				public void methodCallStarted(Object proxy, Object[] args, long callid)
				{
					times.put(new Long(callid), new Long(System.currentTimeMillis()));
				}
				
				public void methodCallFinished(Object proxy, Object[] args, long callid)
				{
					Long start = times.remove(new Long(callid));
					long dur = System.currentTimeMillis() - start.longValue();
					setValue(dur);
				}
			});
		}
		else
		{
			System.out.println("Warning, cannot install property callback on service: "+getName());
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
		// ema calculatio: EMAt = EMAt-1 +(SF*(Ct-EMAt-1)) SF=2/(n+1)
		if(this.value!=null && value!=null)
		{
			double sf = 2d/(10d+1); // 10 periods per default
			double delta = value-this.value;
			value = new Long((long)(this.value+sf*delta));
		}
		
		if(value!=null)
		{
			System.out.println("Setting value: "+value);
			super.setValue((long)value);
		}
	}
}
