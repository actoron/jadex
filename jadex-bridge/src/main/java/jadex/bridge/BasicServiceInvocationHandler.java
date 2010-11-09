package jadex.bridge;

import jadex.commons.IResultCommand;
import jadex.commons.collection.MultiCollection;
import jadex.commons.service.IServiceIdentifier;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

/**
 * 
 */
public class BasicServiceInvocationHandler implements InvocationHandler
{
	//-------- attributes --------

	/** The service identifier. */
	protected IServiceIdentifier sid;
	
	/** The map of own methods. */
	protected MultiCollection interceptors;
	
	/** The fallback invocation interceptor. */
	protected IResultCommand fallback;

	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public BasicServiceInvocationHandler(IServiceIdentifier sid, MultiCollection interceptors, IResultCommand fallback)
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
		ServiceInvocationContext sic = new ServiceInvocationContext(proxy, method, args);
		Collection icps = interceptors==null? null: (Collection)interceptors.getCollection(method);
		if(icps!=null && icps.size()>0)
		{
			for(Iterator it=icps.iterator(); it.hasNext(); )
			{
				IResultCommand com = (IResultCommand)it.next();
				sic.setResult(com.execute(sic));
			}
		}
		else
		{
			sic.setResult(fallback.execute(sic));
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
