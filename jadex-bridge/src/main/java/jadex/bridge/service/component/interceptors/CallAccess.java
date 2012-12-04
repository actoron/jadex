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
	public static ServiceCall	createServiceCall(IComponentIdentifier caller, Map<String, Object> props)
	{
		return ServiceCall.createServiceCall(caller, props);
	}

	/**
	 *  Set the current service call.
	 *  @param call	The service call.
	 */
	public static void	setServiceCall(ServiceCall call)
	{
		ServiceCall.CALLS.set(call);
	}

	/**
	 *  Remove the current service call.
	 */
	public static void	resetServiceCall()
	{
		ServiceCall.CALLS.set(null);
	}
	
	/**
	 *  Get the invocation data for the next service call.
	 */
	public static ServiceCall	getNextInvocation()
	{
		return INVOCATIONS.get();
	}

	/**
	 *  Reset the invocation data for the next service call.
	 */
	public static void	resetNextInvocation()
	{
		INVOCATIONS.set(null);
	}
}
