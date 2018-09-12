package jadex.bridge;

import java.util.HashMap;
import java.util.Map;

import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.ThreadLocalTransferHelper;


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
	public static final String DEFTIMEOUT = "deftimeout";
	
	/** The realtime constant. */
	public static final String REALTIME = "realtime";
	
//	/** The cause constant. */
//	public static final String CAUSE = "cause";
	
	/** The monitoring constant. */
	public static final String MONITORING = "monitoring";
	
	/** The inherit constant. */
	public static final String INHERIT = "inherit";
	
	/** The security infos constant. */
	public static final String SECURITY_INFOS = "secinfos";
	
	/** The current service calls mapped to threads. */
	protected static final ThreadLocal<ServiceCall> CURRENT = new ThreadLocal<ServiceCall>();
	
	/** The upcoming service invocations. */
	protected static final ThreadLocal<ServiceCall> NEXT = new ThreadLocal<ServiceCall>();
	
	/** The upcoming service invocations. */
	protected static final ThreadLocal<ServiceCall> LAST = new ThreadLocal<ServiceCall>();

	static
	{
		ThreadLocalTransferHelper.addThreadLocal(CURRENT);
		ThreadLocalTransferHelper.addThreadLocal(NEXT);
		ThreadLocalTransferHelper.addThreadLocal(LAST);
	}
	
	//-------- attributes --------
	
	/** The calling component. */
	public IComponentIdentifier	caller;
	
	/** The service call properties. */
	public Map<String, Object> properties;
	
	// hack for debugging concurrent modification
	public IComponentIdentifier	lastmod;
	
	//-------- constructors --------
	
//	static Set<Integer> sprops = Collections.synchronizedSet(new HashSet<Integer>());
	
	/**
	 *  Create a service call info object.
	 */
	protected ServiceCall(IComponentIdentifier caller, Map<String, Object> props)
	{
//		if(caller==null)
//		{
//			System.out.println("dflishg");
//		}
		this.caller	= caller;
		this.properties = props!=null? props: new HashMap<String, Object>();
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
	 *  Get the invocation data for the next service call.
	 */
	public static ServiceCall	getNextInvocation()
	{
		return NEXT.get();
	}
	
	/**
	 *  Get the service call instance corresponding
	 *  to the current execution context.
	 *  @return The service call instance or null.
	 */
	public static ServiceCall	getCurrentInvocation()
	{
		return CURRENT.get();
	}
	
	/**
	 *  Get the last service call instance corresponding
	 *  to the current execution context.
	 *  @return The service call instance or null.
	 */
	public static ServiceCall	getLastInvocation()
	{
		return LAST.get();
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
	public static ServiceCall getOrCreateNextInvocation()
	{
		return getOrCreateNextInvocation(null);
	}
	
//	/**
//	 *  Get the next invocation if any.
//	 */
//	public static ServiceCall getInvocation0()
//	{
//		return NEXT.get();
//	}
	
	/**
	 *  Get or create the next servicecall for the next invocation. 
	 *  @param timeout The timeout.
	 *  @param realtime The realtime flag.
	 */
	public static ServiceCall getOrCreateNextInvocation(Map<String, Object> props)
	{
		ServiceCall ret = NEXT.get();
		if(ret==null)
		{
			ret = new ServiceCall(IComponentIdentifier.LOCAL.get(), props);
			
//			if(ret.getCaller()==null)
//			{
//				System.out.println("sfljyh");
//				Thread.dumpStack();
//			}
			
//			if(ret!=null && ret.getCause()==null)
//			{
//				System.out.println(Thread.currentThread().hashCode()+": create: "+ret+", "+Thread.currentThread());
//			}

			NEXT.set(ret);
			
//			if(getCurrentInvocation()!=null)
//			{
//				ServiceCall cur = getCurrentInvocation();
//				Tuple2<String, String> cause = cur.getCause();
//				if(cause!=null)
//				{
//					ret.setCause(new Tuple2<String, String>(cause.getSecondEntity(), SUtil.createUniqueId(caller.getName(), 3)));
//				}
//			}
		}
		else if(props!=null)
		{
			
//			if(ret.getCaller()==null && props.get("method2")!=null && props.get("method2").equals("status"))
//			{
//				System.out.println("abgsdoyi: "+ret);
////				Thread.dumpStack();
//			}
			ret.lastmod	= IComponentIdentifier.LOCAL.get();
			ret.properties.putAll(props);
		}
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
		return properties.containsKey(TIMEOUT)? ((Long)properties.get(TIMEOUT)).longValue(): ((Long)properties.get(DEFTIMEOUT)).longValue();
	}
	
	/**
	 *  Test if the user has set a timeout.
	 *  @return True, if the user has set a timeout.
	 */
	public boolean hasUserTimeout()
	{
		return properties.containsKey(TIMEOUT);
	}
	
	/**
	 *  Set the timeout.
	 *  @param to The timeout.
	 */
	public void setTimeout(long to)
	{
//		if(((String)properties.get("method")).indexOf("service")!=-1)
//			System.out.println("sdfjbsdfjk");
		lastmod	= IComponentIdentifier.LOCAL.get();
		properties.put(TIMEOUT, Long.valueOf(to));
	}
	
	/**
	 *  Get the realtime flag.
	 *  @return True, if the timeout is a real time (i.e. system time)
	 *    instead of platform time. 
	 */
//	public Boolean	getRealtime()
//	{
//		return (Boolean)properties.get(REALTIME);
//	}
	
	/**
	 *  Set the realtime property.
	 */
//	public void setRealtime(Boolean realtime)
//	{
//		lastmod	= IComponentIdentifier.LOCAL.get();
//		properties.put(REALTIME, realtime);
//	}
	
	/**
	 *  Get the realtime flag.
	 *  @return True, if the timeout is a real time (i.e. system time)
	 *    instead of platform time. 
	 */
//	public boolean isRealtime()
//	{
//		return getRealtime().booleanValue();
//	}
	
	/**
	 *  Test if a call is remote.
	 */
	public boolean isRemoteCall(IComponentIdentifier callee)
	{
		IComponentIdentifier platform = callee.getRoot();
		return caller==null? false: !caller.getRoot().equals(platform);
	}
	
//	/**
//	 *  Get the cause.
//	 *  @return The cause.
//	 */
//	public Cause getCause()
//	{
////		if(properties.get(CAUSE)!=null && !(properties.get(CAUSE) instanceof Cause))
////		{
////			System.out.println("sdmyb");
////		}
//		return (Cause)properties.get(CAUSE);
//	}
//	
//	/**
//	 *  Set the cause.
//	 *  @param cause The cause.
//	 */
//	public void setCause(Cause cause)
//	{
//		lastmod	= IComponentIdentifier.LOCAL.get();
//		properties.put(CAUSE, cause);
//	}
	
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
//		if(TIMEOUT.equals(name))
//		{
//			if(properties.get("method")!=null && ((String)properties.get("method")).indexOf("service")!=-1)
//				System.out.println("setting tout: "+val);
//			else if(properties.get("method")==null)
//				System.out.println("setting unknown tout: "+val);
//		}
		lastmod	= IComponentIdentifier.LOCAL.get();
		this.properties.put(name, val);
	}
	
	/**
	 *  Remove a property.
	 *  @param name The property name.
	 */
	public void removeProperty(String name)
	{
		lastmod	= IComponentIdentifier.LOCAL.get();
		this.properties.remove(name);
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

	/** 
	 *  Get the string represntation.
	 */
	public String toString()
	{
		return "ServiceCall(caller=" + caller + ", properties=" + properties+ ")";
	}
}
