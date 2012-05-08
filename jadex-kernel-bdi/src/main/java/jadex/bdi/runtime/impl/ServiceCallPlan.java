package jadex.bdi.runtime.impl;

import jadex.bdi.runtime.Plan;
import jadex.commons.SReflect;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 *  Find and call a service.
*/
public class ServiceCallPlan extends Plan
{
	/** The service name. */
	protected String service;
	
	/** The method. */
	protected String method;
	
	/**
	 * 
	 */
	public ServiceCallPlan(String service, String method)
	{
		this.service = service;
		this.method = method;
	}
	
	public void body()
	{
//		try
//		{
		boolean	success	= false;
//		String	service	= (String)getParameter("service").getValue();
//		String	method	= (String)getParameter("method").getValue();
//		Object[]	args	= (Object[])getParameter("args").getValue();
		Object[] args = new Object[0];
		
		IIntermediateFuture<?>	services	= getServiceContainer().getRequiredServices(service);
		// Todo: implement suspendable intermediate futures.
//		while(!success && services.hasNextIntermediateResult(this))
		Collection<?>	results	= services.get(this);
//		System.out.println("received: "+results);
		for(Object proxy: results)
		{
			try
			{
//				Object	proxy	= services.getNextIntermediateResult(this);
				Method[]	meths	= SReflect.getMethods(proxy.getClass(), method);			
				Object	res	= meths[0].invoke(proxy, args);
				if(res instanceof IFuture<?>)
				{
					((IFuture<?>)res).get(this);
				}
				success	= true;
			}
			catch(Exception e)
			{
//				e.printStackTrace();
			}
		}
		
		if(!success)
			fail();
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
	}
}
