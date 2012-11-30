package jadex.bridge;

import jadex.bridge.service.annotation.Timeout;

import java.util.HashMap;
import java.util.Map;


/**
 *  Information about a current service call.
 *  
 *  Similar to a ThreadLocal in Java but for service calls, i.e.
 *  between different threads and hosts available.
 */
public class ServiceCall
{
	//-------- constants --------

	/** The timeout constant. */
	public static final String TIMEOUT = Timeout.TIMEOUT;
	
	/** The realtime constant. */
	public static final String REALTIME = "realtime";
	
	/** The current service calls mapped to threads. */
	protected static ThreadLocal<ServiceCall> CALLS	= new ThreadLocal<ServiceCall>();
	
	/** The upcoming service invocations. */
	protected static ThreadLocal<ServiceCall> INVOCATIONS = new ThreadLocal<ServiceCall>();
	
	//-------- attributes --------
	
	/** The calling component. */
	protected IComponentIdentifier	caller;
	
//	/** The timeout value (if any). */
//	protected long	timeout;
//	
//	/** The flag indicating if the timeout is given as system time (true)
//	 *  or platform time (false). */
//	protected Boolean realtime;
	
	/** The service call properties. */
	protected Map<String, Object> properties;
	
	//-------- constructors --------
	
	/**
	 *  Create a service call info object.
	 */
	protected ServiceCall(IComponentIdentifier caller, Map<String, Object> props)
	{
		this.caller	= caller;
		this.properties = new HashMap<String, Object>();
		properties.put(TIMEOUT, new Long(-1)); // todo: refactor that
		
		if(props!=null)
			properties.putAll(props);
	}
	
	/**
	 *  Create a service call.
	 */
	protected static ServiceCall createServiceCall(IComponentIdentifier caller, 
		long timeout, Boolean realtime, Map<String, Object> props)
	{
		if(props==null)
			props = new HashMap<String, Object>();
		props.put(TIMEOUT, new Long(timeout));
		if(realtime!=null)
			props.put(REALTIME, realtime);
		return createServiceCall(caller, props);
	}
	
	/**
	 *  Create a service call.
	 */
	protected static ServiceCall createServiceCall(IComponentIdentifier caller, Map<String, Object> props)
	{
		return new ServiceCall(caller, props);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the service call instance corresponding
	 *  to the current execution context.
	 *  @return The service call instance or null.
	 */
	public static ServiceCall	getCurrentInvocation()
	{
		return CALLS.get();
	}
	
//	/**
//	 *  Set the properties of the next invocation.
//	 *  @param timeout The timeout.
//	 *  @param realtime The realtime flag.
//	 */
//	public static ServiceCall setInvocationProperties(long timeout, Boolean realtime)
//	{
//		ServiceCall ret = new ServiceCall(IComponentIdentifier.LOCAL.get(), timeout, realtime);
//		INVOCATIONS.set(ret);
//		return ret;
//	}
	
	/**
	 *  Set the properties of the next invocation.
	 *  @param timeout The timeout.
	 *  @param realtime The realtime flag.
	 */
	public static ServiceCall createInvocation()
	{
		return createInvocation(null);
	}
	
	/**
	 *  Set the properties of the next invocation.
	 *  @param timeout The timeout.
	 *  @param realtime The realtime flag.
	 */
	public static ServiceCall createInvocation(Map<String, Object> props)
	{
		ServiceCall ret = new ServiceCall(IComponentIdentifier.LOCAL.get(), props);
		INVOCATIONS.set(ret);
		return ret;
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
		return ((Long)properties.get(TIMEOUT)).longValue();
	}
	
	/**
	 *  Set the timeout.
	 *  @param to The timeout.
	 */
	public void setTimeout(long to)
	{
		properties.put(TIMEOUT, new Long(to));
	}
	
	/**
	 *  Get the realtime flag.
	 *  @return True, if the timeout is a real time (i.e. system time)
	 *    instead of platform time. 
	 */
	public Boolean	getRealtime()
	{
		return (Boolean)properties.get(REALTIME);
	}
	
	/**
	 *  Set the realtime property.
	 */
	public void setRealtime(Boolean realtime)
	{
		properties.put(REALTIME, realtime);
	}
	
	/**
	 *  Get the realtime flag.
	 *  @return True, if the timeout is a real time (i.e. system time)
	 *    instead of platform time. 
	 */
	public boolean isRealtime()
	{
		return getRealtime().booleanValue();
	}
	
	/**
	 *  Get a property.
	 *  @param name The property name.
	 *  @return The property.
	 */
	public Object getProperty(String name)
	{
		return properties.get(name);
	}
	
	/**
	 *  Set a property.
	 *  @param name The property name.
	 *  @param val The property value.
	 */
	public void setProperty(String name, Object val)
	{
		this.properties.put(name, val);
	}
	
	/**
	 *  Get a property.
	 *  @param name The property name.
	 *  @return The property.
	 */
	public Map<String, Object> getProperties()
	{
		return properties;
	}
}
