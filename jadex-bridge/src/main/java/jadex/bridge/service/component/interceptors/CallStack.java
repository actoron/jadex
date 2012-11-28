package jadex.bridge.service.component.interceptors;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ServiceCall;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  Helper class to deal with service calls
 */
public abstract class CallStack	extends ServiceCall
{
	//-------- constants --------
	
	/** The call stack. */
	protected static final ThreadLocal<List<ServiceCall>>	CALLSTACK	= new ThreadLocal<List<ServiceCall>>();
	
	//-------- constructors --------
	
	/**
	 *  Dummy constructor as this class only contains static methods.
	 */
	public CallStack()
	{
//		super(null, 0, false, null);
		super(null, null);
		throw new RuntimeException("Class should not be instantiated.");
	}
	
	//-------- methods --------

	/**
	 *  Push a service call to the call stack.
	 *  @param caller	The calling component. 
	 *  @param timeout	The timeout value.
	 *  @param realtime	The flag to indicate real time timeouts.
	 *  @param adapter	The adapter of the caller (for back-scheduling of result).
	 */
//	protected static void	push(IComponentIdentifier caller, long timeout, boolean realtime)
	protected static void	push(IComponentIdentifier caller, Map<String, Object> props)
	{
		ServiceCall	previous	= ServiceCall.getCurrentInvocation();
		if(previous!=null)
		{
			List<ServiceCall>	stack	= CALLSTACK.get();
			if(stack==null)
			{
				stack	= new ArrayList<ServiceCall>();
				CALLSTACK.set(stack);
			}
			stack.add(previous);
		}
//		ServiceCall.CALLS.set(ServiceCall.createServiceCall(caller, timeout, realtime? Boolean.TRUE: Boolean.FALSE, null));
		ServiceCall.CALLS.set(ServiceCall.createServiceCall(caller, props));
	}
	
	/**
	 *  Pop a service call from the call stack.
	 */
	protected static void	pop()
	{
		ServiceCall	previous	= null;
		List<ServiceCall>	stack	= CALLSTACK.get();
		if(stack!=null && !stack.isEmpty())
		{
			previous	= stack.remove(stack.size()-1);
		}
		ServiceCall.CALLS.set(previous);
	}
	
	/**
	 * 
	 */
	public static ServiceCall getInvocation()
	{
		return INVOCATIONS.get();
	}
	
	/**
	 * 
	 */
	public static void removeInvocation()
	{
		INVOCATIONS.set(null);
	}
}
