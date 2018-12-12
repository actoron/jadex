package jadex.bridge.service.search;

import jadex.bridge.ProxyFactory;
import jadex.commons.IAsyncFilter;
import jadex.commons.future.IFuture;

/**
 *  Test if a class is a proxy.
 */
public class ProxyFilter implements IAsyncFilter
{
	//-------- attributes --------
	
	/** Static proxy filter instance. */
	public static final IAsyncFilter PROXYFILTER = new ProxyFilter();

	//-------- methods --------
	
	/**
	 *  Test if service is a remote proxy.
	 */
	public IFuture<Boolean> filter(Object obj)
	{
		try
		{
		boolean ret = ProxyFactory.isProxyClass(obj.getClass()) && ProxyFactory.getInvocationHandler(obj).getClass()
			.getName().indexOf("RemoteMethodInvocationHandler")!=-1;
//		System.out.println("obj: "+obj==null? "null": obj.getClass()+" "+!ret);
		return !ret ? IFuture.TRUE : IFuture.FALSE;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
//		try
//		{
//		System.out.println("obj: "+obj==null? "null": obj.getClass());
//		if(obj!=null)
//			System.out.println("filter: "+Proxy.isProxyClass(obj.getClass())+" "+Proxy.getInvocationHandler(obj));
//		return new Future<Boolean>(!Proxy.isProxyClass(obj.getClass()) || 
//			// todo: fix this Hack	
//			!(Proxy.getInvocationHandler(obj).getClass().getName().indexOf("RemoteMethodInvocationHandler")!=-1));
////			!(Proxy.getInvocationHandler(obj) instanceof RemoteMethodInvocationHandler));
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
	}
	
	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		return getClass().hashCode();
	}

	/**
	 *  Test if an object is equal to this.
	 */
	public boolean equals(Object obj)
	{
		return obj!=null && obj.getClass().equals(this.getClass());
	}
}
