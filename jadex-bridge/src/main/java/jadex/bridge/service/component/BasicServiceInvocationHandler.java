package jadex.bridge.service.component;

import jadex.bridge.service.IServiceIdentifier;
import jadex.commons.collection.MultiCollection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

/**
 *  Basic service invocation interceptor.
 *  It has a multi collection of interceptors per method.
 *  Executes the list of interceptors one by one.
 *  In case no handler can be found a fallback handler is used.
 */
public class BasicServiceInvocationHandler implements InvocationHandler
{
	//-------- attributes --------

	/** The service identifier. */
	protected IServiceIdentifier sid;
	
	/** The map of own methods. */
	protected MultiCollection interceptors;
	
	/** The fallback invocation interceptor. */
	protected IServiceInvocationInterceptor fallback;

	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public BasicServiceInvocationHandler(IServiceIdentifier sid, MultiCollection interceptors, IServiceInvocationInterceptor fallback)
	{
		this.sid = sid;
		this.interceptors = interceptors;
		this.fallback = fallback;
	}
	
	//-------- methods --------
	
	/**
	 *  A proxy method has been invoked.
	 */
	public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable
	{
		ServiceInvocationContext sic = new ServiceInvocationContext(proxy, method, args, null);
		Collection icps = interceptors==null? null: (Collection)interceptors.getCollection(method);
		if(icps!=null && icps.size()>0)
		{
			for(Iterator it=icps.iterator(); it.hasNext(); )
			{
				((IServiceInvocationInterceptor)it.next()).execute(sic);
			}
		}
		else
		{
			fallback.execute(sic);
		}
		
		return sic.getResult();
	}
	
	/**
	 *  Get the sid.
	 *  @return the sid.
	 */
	public IServiceIdentifier getServiceIdentifier()
	{
		return sid;
	}

}
