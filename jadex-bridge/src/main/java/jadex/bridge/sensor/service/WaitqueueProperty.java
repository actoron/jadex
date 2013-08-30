package jadex.bridge.sensor.service;

import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.bridge.nonfunctional.SimpleValueNFProperty;
import jadex.bridge.service.IService;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.commons.MethodInfo;
import jadex.commons.future.IFuture;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/**
 *  Property for the waiting time of a method or a service as a whole.
 */
public class WaitqueueProperty extends SimpleValueNFProperty<Integer, Void>
{
	/** The name of the property. */
	public static final String WAITQUEUE = "wait queue length";
	
	/** The handler. */
	protected BasicServiceInvocationHandler handler;
	
	/** The listener. */
	protected IMethodInvocationListener listener;
	
	/** The method info. */
	protected MethodInfo method;
	
	/**
	 *  Create a new property.
	 */
	public WaitqueueProperty(IInternalAccess comp, IService service, MethodInfo method)
	{
		super(comp, new NFPropertyMetaInfo(WAITQUEUE, int.class, Void.class, true, -1, null));
		this.method = method;
		
		if(Proxy.isProxyClass(service.getClass()))
		{
			handler = (BasicServiceInvocationHandler)Proxy.getInvocationHandler(service);
			listener = new UserMethodInvocationListener(new IMethodInvocationListener()
			{
				int cnt = 0;
				
				public void methodCallStarted(Object proxy, Method method, Object[] args, long callid)
				{
//					System.out.println("started: "+method+" "+cnt);
					setValue(new Integer(++cnt));
				}
				
				public void methodCallFinished(Object proxy, Method method, Object[] args, long callid)
				{
//					System.out.println("ended: "+method+" "+cnt);
					if(cnt>0)
						--cnt;
					setValue(new Integer(cnt));
				}
			});
			handler.addMethodListener(method, listener);
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
		if(handler!=null)
			handler.removeMethodListener(method, listener);
		return IFuture.DONE;
	}
}
