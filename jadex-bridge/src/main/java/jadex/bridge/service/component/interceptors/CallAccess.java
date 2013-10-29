package jadex.bridge.service.component.interceptors;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ServiceCall;

import java.util.Map;

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
		ServiceCall.CALLS.set(call);
	}

	/**
	 *  Remove the current service call.
	 */
	public static void	resetCurrentInvocation()
	{
//		LAST.set(ServiceCall.CALLS.get());
		ServiceCall.CALLS.set(null);
	}
	
	/**
	 *  Reset the invocation data for the next service call.
	 */
	public static void	setNextInvocation(ServiceCall call)
	{
		NEXT.set(call);
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
}
