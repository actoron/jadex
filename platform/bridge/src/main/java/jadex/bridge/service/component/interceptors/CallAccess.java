package jadex.bridge.service.component.interceptors;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ServiceCall;

/**
 *  Helper class to deal with service calls
 */
public abstract class CallAccess	extends ServiceCall
{
	//-------- constructors --------
	
	/**
	 *  Dummy constructor as this class only contains static methods.
	 */
	protected CallAccess()
	{
		super(null, null);
		throw new RuntimeException("Class should not be instantiated.");
	}
	
	//-------- methods --------

	/**
	 *  Create a service call.
	 *  @param caller	The calling component. 
	 *  @param props	The properties.
	 */
	public static ServiceCall createServiceCall(IComponentIdentifier caller, Map<String, Object> props)
	{
		return ServiceCall.createServiceCall(caller, props);
	}

	/**
	 *  Set the current service call.
	 *  @param call	The service call.
	 */
	public static void	setCurrentInvocation(ServiceCall call)
	{
//		if(call!=null && call.getCause()==null)
//		{
//			System.out.println(Thread.currentThread().hashCode()+": set: "+call+", "+Thread.currentThread());
//		}
		ServiceCall.CURRENT.set(call);
	}

	/**
	 *  Remove the current service call.
	 */
	public static void	resetCurrentInvocation()
	{
//		LAST.set(ServiceCall.CALLS.get());
		ServiceCall.CURRENT.set(null);
	}
	
	/**
	 *  Reset the invocation data for the next service call.
	 */
	public static void	setNextInvocation(ServiceCall call)
	{
//		if((""+call).indexOf("ServiceCallAgent")!=-1)
//		{
//			System.out.println(call.hashCode()+": set next: "+call+", "+IComponentIdentifier.LOCAL.get());
//		}

		NEXT.set(call);
//		if(call!=null)
//			call.setProperty("nextstack1", jadex.commons.SUtil.getExceptionStacktrace(new RuntimeException()));
	}

	/**
	 *  Reset the invocation data for the next service call.
	 */
	public static void	resetNextInvocation()
	{
		NEXT.set(null);
	}
	
	/**
	 *  Reset the invocation data for the last service call.
	 */
	public static void	setLastInvocation(ServiceCall call)
	{
		LAST.set(call);
	}

	/**
	 *  Reset the invocation data for the last service call.
	 */
	public static void	resetLastInvocation()
	{
		LAST.set(null);
	}
	
	/**
	 * 
	 */
	public static void roll()
	{
		LAST.set(CURRENT.get());
		CURRENT.set(NEXT.get());
		NEXT.set(null);
	}
}
