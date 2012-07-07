package jadex.bridge;

/**
 *  Information about a current service call.
 */
public class ServiceCall
{
	//-------- constants --------
	
	/** The current service calls mapped to threads. */
	protected static ThreadLocal<ServiceCall>	CALLS	= new ThreadLocal<ServiceCall>();
	
	//-------- attributes --------
	
	/** The calling component. */
	protected IComponentIdentifier	caller;
	
	/** The timeout value (if any). */
	protected long	timeout;
	
	/** The flag indicating if the timeout is given as system time (true)
	 *  or platform time (false). */
	protected boolean	realtime;
	
	//-------- constructors --------
	
	/**
	 *  Create a service call info object.
	 */
	protected ServiceCall(IComponentIdentifier caller, long timeout, boolean realtime)
	{
		this.caller	= caller;
		this.timeout	= timeout;
		this.realtime	= realtime;
	}
	
	/**
	 *  Create a service call.
	 */
	protected static ServiceCall	createServiceCall(IComponentIdentifier caller, long timeout, boolean realtime)
	{
		return new ServiceCall(caller, timeout, realtime);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the service call instance corresponding
	 *  to the current execution context.
	 *  @return The service call instance or null.
	 */
	public static ServiceCall	getInstance()
	{
		return CALLS.get();
	}
	
	/**
	 *  Get the caller component.
	 *  @return The caller component.
	 */
	public IComponentIdentifier	getCaller()
	{
		return caller;
	}
	
	/**
	 *  Get the timeout value.
	 *  @return The timeout value or -1.
	 */
	public long	getTimeout()
	{
		return timeout;
	}
	
	/**
	 *  Get the realtime flag.
	 *  @return True, if the timeout is a real time (i.e. system time)
	 *    instead of platform time. 
	 */
	public boolean	isRealtime()
	{
		return realtime;
	}
}
