package jadex.bridge.sensor.service;

import java.lang.reflect.Method;

import jadex.bridge.IInternalAccess;
import jadex.bridge.ProxyFactory;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.bridge.nonfunctional.SimpleValueNFProperty;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.MethodInfo;
import jadex.commons.future.IFuture;


/**
 *  Property for the waitqueue length (in calls) of a method or a service.
 */
public class WaitqueueProperty extends SimpleValueNFProperty<Integer, Void>
{
	/** The name of the property. */
	public static final String NAME = "wait queue length";
	
	/** The service identifier. */
	protected IServiceIdentifier sid;
	
	/** The listener. */
	protected IMethodInvocationListener listener;
	
	/** The method info. */
	protected MethodInfo method;
	
	/**
	 *  Create a new property.
	 */
	public WaitqueueProperty(IInternalAccess comp, IService service, MethodInfo method)
	{
		super(comp, new NFPropertyMetaInfo(NAME, int.class, Void.class, true, -1, true, null));
		this.method = method;
		this.sid = service.getServiceId();
		
		if(ProxyFactory.isProxyClass(service.getClass()))
		{
			listener = new UserMethodInvocationListener(new IMethodInvocationListener()
			{
				int cnt = 0;
				
				public void methodCallStarted(Object proxy, Method method, Object[] args, Object callid, ServiceInvocationContext context)
				{
//					System.out.println("started: "+method+" "+cnt);
					setValue(Integer.valueOf(++cnt));
				}
				
				public void methodCallFinished(Object proxy, Method method, Object[] args, Object callid, ServiceInvocationContext context)
				{
//					System.out.println("ended: "+method+" "+cnt);
					if(cnt>0)
						--cnt;
					setValue(Integer.valueOf(cnt));
				}
			});
			comp.getFeature(IProvidedServicesFeature.class).addMethodInvocationListener(sid, method, listener);
		}
		else
		{
			throw new RuntimeException("Cannot install wait queue listener hook.");
		}
	}
	
	/**
	 *  Measure the value.
	 */
	public Integer measureValue()
	{
		return null;
//		throw new UnsupportedOperationException();
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
