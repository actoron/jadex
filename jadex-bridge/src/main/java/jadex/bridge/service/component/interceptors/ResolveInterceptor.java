package jadex.bridge.service.component.interceptors;

import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IService;
import jadex.bridge.service.component.ServiceInfo;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.future.IFuture;

import java.util.HashSet;
import java.util.Set;

/**
 *  The resolve interceptor is responsible for determining
 *  the object on which the method invocation is finally performed.
 * 
 *  Checks whether the object is a ServiceInfo. In this case
 *  it delegates method calls of I(Internal)Service to 
 *  the automatically created BasicService instance and all
 *  other calls to the domain object.
 *  
 *  // todo: much annotation stuff and injection of objects to the pojo.
 */
public class ResolveInterceptor extends AbstractApplicableInterceptor
{
	//-------- constants --------
	
	/** The static map of subinterceptors (method -> interceptor). */
	protected static Set SERVICEMETHODS;
	
	static
	{
		try
		{
			SERVICEMETHODS = new HashSet();
			SERVICEMETHODS.add(IInternalService.class.getMethod("startService", new Class[0]));
			SERVICEMETHODS.add(IInternalService.class.getMethod("shutdownService", new Class[0]));
			SERVICEMETHODS.add(IService.class.getMethod("getServiceIdentifier", new Class[0]));
			SERVICEMETHODS.add(IInternalService.class.getMethod("getPropertyMap", new Class[0]));
			SERVICEMETHODS.add(IInternalService.class.getMethod("isValid", new Class[0]));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture execute(final ServiceInvocationContext sic)
	{
		Object service = sic.getObject();
		if(service instanceof ServiceInfo)
		{
			ServiceInfo si = (ServiceInfo)service;
			
			// Determine which object to call based on method type
			// todo: call always domain object first when it overrides some reserved/annotated method
			
			if(SERVICEMETHODS.contains(sic.getMethod()))
			{
				sic.setObject(si.getManagementService());
			}
			else
			{
				sic.setObject(si.getDomainService());
			}
		}
		
		return sic.invoke();
	}
}
