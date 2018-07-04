package jadex.bridge.service.component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import jadex.base.Starter;
import jadex.bridge.Cause;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Timeout;
import jadex.bridge.service.component.interceptors.CallAccess;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  Context for service invocations.
 *  Contains all method call information. 
 *  
 *  Invariants that must hold before, during and after a service call for the NEXT/CUR/LAST service calls
 *  			caller 										callee
 *  
 *  before		next = null	|| user defined						
 *  			cur = actual call
 *  
 *  during		next = null	(set in ServiceInvocContext)	cur = next
 *  			cur = cur (same as in before)				(set in MethodInvocationInterceptor)
 *  
 *  after		next = null
 *  			cur = cur (same as in before)
 *  			last = next
 *  			(set in MethodInvocationInterceptor)
 */
public class ServiceInvocationContext
{
	public static final ThreadLocal<ServiceInvocationContext> SICS = new ThreadLocal<ServiceInvocationContext>();
	
	//-------- profiling --------

	/** Enable call profiling. */
	public static final boolean	PROFILING	= false;
	
	/** Print every 10 seconds. */
	public static final long	PRINT_DELAY	= 10000;
	
	/** Service calls per method, calculated separately per platform. */
	protected static Map<IComponentIdentifier, Map<Method, Integer>>	calls	= PROFILING ? new HashMap<IComponentIdentifier, Map<Method, Integer>>() : null;
	
	static
	{
		if(PROFILING)
		{
			final Timer	timer	= new Timer(true);
			final Runnable	run	= new Runnable()
			{
				public void run()
				{
					IComponentIdentifier[]	platforms;
					synchronized(calls)
					{
						platforms	= calls.keySet().toArray(new IComponentIdentifier[calls.size()]);
					}
					for(IComponentIdentifier platform: platforms)
					{
						Map<Method, Integer>	pcalls;
						synchronized(calls)
						{
							pcalls	= calls.get(platform);
						}
						StringBuffer	out	= new StringBuffer("Calls of platform ").append(platform).append("\n");
						synchronized(pcalls)
						{
							for(Method m: pcalls.keySet())
							{
								out.append("\t").append(pcalls.get(m)).append(":\t")
									.append(m.getDeclaringClass().getSimpleName()).append(".").append(m.getName()).append(SUtil.arrayToString(m.getParameterTypes()))
									.append("\n");
							}
						}
						System.out.println(out);
					}
					
					final Runnable	run	= this;
					timer.schedule(new TimerTask()
					{
						public void run()
						{
							run.run();
						}
					}, PRINT_DELAY);
				}
			};
			
			timer.schedule(new TimerTask()
			{
				public void run()
				{
					run.run();
				}
			}, PRINT_DELAY);
		}
	}
	
	
	//-------- attributes --------
	
	/** The origin (proxy object). */
	protected Object proxy;
	
	
	/** The object. */
	protected List<Object> object;
	
	/** The method to be called. */
	protected List<Method> method;
	
	/** The invocation arguments. */
	protected List<List<Object>> arguments;
	
	/** The call result. */
	protected List<Object> result;
	

	/** The service interceptors. */
	protected IServiceInvocationInterceptor[] interceptors;

	/** The stack of used interceptors. */
	protected List<Integer> used;
	
	/** The next service call (will be current during call and last after call). */
	protected ServiceCall	nextcall;
	
	/** The current service call (to be reestablished after call). */
	protected ServiceCall	currentcall;
	
	/** The caller component. */
	protected IComponentIdentifier caller;
	
	/** The platform identifier. */
	protected IComponentIdentifier platform;
	
//	/** The flag if local timeouts should be realtime. */
//	protected boolean realtime;
	
	/** The creation (root) cause. */
	protected Cause cause;
	
	protected IServiceIdentifier sid;

//	public Exception ex;
	
	//-------- constructors --------
	
	/**
	 *  Create a new context.
	 */
	public ServiceInvocationContext(Object proxy, Method method, 
		IServiceInvocationInterceptor[] interceptors, IComponentIdentifier platform, 
		IServiceIdentifier sid, Cause crcause)
	{
//		this.ex = new RuntimeException();
		this.sid = sid;
		
		this.platform = platform;
		this.proxy = proxy;
		this.object = new ArrayList<Object>();
		this.method = new ArrayList<Method>();
		this.arguments = new ArrayList<List<Object>>();
		this.result = new ArrayList<Object>();
		this.cause = crcause;
		
		this.used = new ArrayList<Integer>();
		this.interceptors = interceptors;
		
		this.caller = ServiceCall.getOrCreateNextInvocation().getCaller();
//		IComponentIdentifier caller2 = IComponentIdentifier.LOCAL.get();
//		if(caller!=null && (!caller.equals(caller2)))
//			System.out.println("Caller different: "+caller+" "+caller2);
			
		// Is next call defined by user?
		this.nextcall = CallAccess.getNextInvocation();
		this.currentcall = CallAccess.getCurrentInvocation();
		// Delete next invocation to ensure that data is erased before decoupling
		// Problem: how to ensure that results are set in lastcall
		CallAccess.resetNextInvocation();
		
//		if(caller!=null && caller.toString().startsWith("rms@") && method.getName().equals("getExternalAccess") && call==null)
//		{
//			System.out.println("hierskdfj");
//		}

		if(nextcall==null)
		{
	//		Map<String, Object> props = call!=null ? new HashMap<String, Object>(call.getProperties()) : new HashMap<String, Object>();
			Map<String, Object> props = null;
			
			Boolean inh = currentcall!=null? (Boolean)currentcall.getProperty(ServiceCall.INHERIT): null;
			if(inh!=null && inh.booleanValue())
			{
				try
				{
					props = new HashMap<String, Object>(currentcall.getProperties());
				}
				catch(ConcurrentModificationException e)
				{
					throw new RuntimeException("Dreck: "+this
						+"\nlocal: "+IComponentIdentifier.LOCAL.get()
						+"\ncurrentcall: "+currentcall
						+"\ncause: "+currentcall.getCause()
						+"\nmethod: "+method
						+"\n: lastmod"+currentcall.lastmod, e);
				}
				props.remove(ServiceCall.CAUSE); // remove cause as it has to be adapted
			}
			else
			{
				props = new HashMap<String, Object>();
			}
//			props.put("method", method.getName());
			this.nextcall = CallAccess.createServiceCall(caller, props);
		}
//		else
//		{
//			this.call.setProperty("method", method.getName());
//		}
		
		// If not user supplied a custom timeout value set to what the interface says
		if(!nextcall.getProperties().containsKey(ServiceCall.TIMEOUT))
		{
//			if(method.getName().indexOf("service")!=-1)
//				System.out.println("ggo");
			// Only set defined timeouts. Otherwise the default timeout is used
			long to = BasicService.getMethodTimeout(proxy.getClass().getInterfaces(), method, isRemoteCall());
			if(Timeout.UNSET!=to)
			{
				nextcall.setProperty(ServiceCall.TIMEOUT, Long.valueOf(to));			
			}
			else 
			{
				nextcall.setProperty(ServiceCall.DEFTIMEOUT, isRemoteCall()? Starter.getLocalDefaultTimeout(sid.getProviderId())
					: Starter.getLocalDefaultTimeout(sid.getProviderId()));
			}
		}
		if(!nextcall.getProperties().containsKey(ServiceCall.REALTIME))
		{
			nextcall.setProperty(ServiceCall.REALTIME, Starter.isRealtimeTimeout(sid.getProviderId())? Boolean.TRUE : Boolean.FALSE);
		}
		
		// Init the cause of the next call based on the last one
		if(this.nextcall.getCause()==null)
		{
//			String target = SUtil.createUniqueId(caller!=null? caller.getName(): "unknown", 3);
			String target = sid.toString();
			if(currentcall!=null && currentcall.getCause()!=null)
			{
				this.nextcall.setCause(new Cause(currentcall.getCause(), target));
//				if(method.getName().indexOf("test")!=-1 && lastcall!=null)
//					System.out.println("Creating new cause based on: "+lastcall.getCause());
//				this.call.setCause(new Tuple2<String, String>(cause.getSecondEntity(), SUtil.createUniqueId(caller!=null? caller.getName(): "unknown", 3)));
			}
			else
			{
				// Create cause with novel chain id as origin is component itself
				Cause newc = new Cause(cause);
//				newc.setChainId(newc.createUniqueId());
//				newc.setOrigin(cause.getTargetId());
				// This is on receiver side, i.e. must set the caller as origin
				newc.setOrigin(caller!=null? caller.getName(): sid.getProviderId().getName());
				this.nextcall.setCause(new Cause(newc, target));
				
//				if(method.getName().indexOf("createCompo")!=-1)
//					System.out.println("herer: "+cause);
			}
		}
	}
	
	/**
	 *  Create a copied context.
	 */
	public ServiceInvocationContext(ServiceInvocationContext context)
	{
		this.sid = context.sid;
//		this.ex= context.ex;
		
		this.nextcall	= context.nextcall;
		this.currentcall = context.currentcall;
//		this.realtime	= context.realtime;
		this.platform = context.platform;
		this.proxy = context.proxy;
		this.object = new ArrayList<Object>(context.object);
		this.method = new ArrayList<Method>(context.method);
		this.arguments = new ArrayList<List<Object>>(context.arguments);
		this.result = new ArrayList<Object>(context.result);
		
		this.used = new ArrayList<Integer>(context.used);
		this.interceptors = context.interceptors;
		
		this.caller = context.caller;
//		this.calleradapter = context.calleradapter;
		this.cause = context.cause;
	}
	
//	/**
//	 *  Clone a service invocation context.
//	 */
//	public ServiceInvocationContext	clone()
//	{
//		return new ServiceInvocationContext(this); 
//	}

	//-------- methods --------
	
	/**
	 *  Get the proxy.
	 *  @return The proxy.
	 */
	public Object getProxy()
	{
		return proxy;
	}

	/**
	 *  Set the proxy.
	 *  @param proxy The proxy to set.
	 */
	public void setProxy(Object proxy)
	{
		this.proxy = proxy;
	}
	
	/**
	 *  Get the object.
	 *  @return the object.
	 */
	public Object getObject()
	{
		return object.get(used.size()-1);
	}

	/**
	 *  Set the object.
	 *  @param object The object to set.
	 */
	public void setObject(Object object)
	{
		this.object.set(used.size()-1, object);
	}

	/**
	 *  Get the method.
	 *  @return the method.
	 */
	public Method getMethod()
	{
		return (Method)method.get(used.size()-1);
	}

	/**
	 *  Set the method.
	 *  @param method The method to set.
	 */
	public void setMethod(Method method)
	{
		this.method.set(used.size()-1, method);
	}

	/**
	 *  Get the args.
	 *  @return the args.
	 */
	public List<Object> getArguments()
	{
		return arguments.get(used.size()-1);
	}
	
	/**
	 *  Get the args.
	 *  @return the args.
	 */
	public Object[] getArgumentArray()
	{
		List<Object> args = arguments.get(used.size()-1);
		return args!=null? args.toArray(): new Object[0];
	}
	
	/**
	 *  Set the arguments.
	 *  @param args The arguments to set.
	 */
	public void setArguments(List<Object> args)
	{
		this.arguments.set(used.size()-1, args);
	}

	/**
	 *  Get the result.
	 *  @return the result.
	 */
	public Object getResult()
	{
		return result.get(used.size()-1);
	}

	/**
	 *  Set the result.
	 *  @param result The result to set.
	 */
	public void setResult(Object result)
	{
		assert getMethod()==null || result==null || result instanceof Throwable || SReflect.isSupertype(getMethod().getReturnType(), result.getClass()) : "Incompatible types: "+getMethod()+", "+result.getClass();
		
//		if(getMethod().getName().indexOf("start")!=-1)
//			System.out.println("gotta");
//		if(getObject() instanceof BasicService && ((BasicService)getObject()).getInterfaceType().getName().indexOf("Peer")!=-1)
//			System.out.println("hhhhhhhhhhhhhhhhhh");
//		if(getMethod().getName().indexOf("start")!=-1 && sid.getServiceType().getTypeName().indexOf("Peer")!=-1)
//			System.out.println("call method start end");
//		if(getMethod().getName().indexOf("init")!=-1 && sid.getServiceType().getTypeName().indexOf("Peer")!=-1)
//			System.out.println("call method init");
		
//		if(SReflect.isSupertype(IFuture.class, getMethod().getReturnType()) && !(result instanceof Future))
//			System.out.println("haeaeaeaeaeae");
		
		this.result.set(used.size()-1, result);
	}

	/**
	 *  Invoke the next interceptor.
	 */
	public IFuture<Void> invoke(Object object, final Method method, List<Object> args)
	{
		IFuture<Void> ret;
		
		//if(method.getName().indexOf("Void")!=-1)
		//	System.out.println("invoke: "+caller);
		
		push(object, method, args, null);
		
		final IServiceInvocationInterceptor interceptor = getNextInterceptor();

//		if(method.getName().equals("ma1"))
//			System.out.println("ma1: "+used.get(used.size()-1)+" "+interceptor+" "+Thread.currentThread());
		
		if(interceptor!=null)
		{
//			if(method.getName().equals("shutdownService") && sid.toString().indexOf("Context")!=-1 && sid.getProviderId().getParent()==null)
//			if(method.getName().indexOf("performSteps")!=-1)
//				System.out.println("invoke before: "+method.getName()+" "+interceptor+", "+platform.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IClockService.class)).getTime());
			IFuture<Void>	fut	= interceptor.execute(this);
			if(fut.isDone())
			{
				pop();
				ret	= fut;
			}
			else
			{
				final Future<Void>	fret	= new Future<Void>();
				ret	= fret;
				fut.addResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
	//					if(sid.getProviderId().getParent()==null)// && method.getName().indexOf("getChildren")!=-1)
	//						System.out.println("invoke after: "+method.getName()+" "+interceptor);
	
	//					if(method.getName().indexOf("getResults")!=-1)
	//						System.out.println("invoke after: "+method.getName()+" "+interceptor+" "+getResult());
						
						pop();
						fret.setResult(null);
					}
					
					public void exceptionOccurred(Exception exception)
					{
	//					if(sid.getProviderId().getParent()==null)
	//						System.out.println("invoke after: "+method.getName()+" "+interceptor);
	
	//					if(method.getName().equals("isValid"))
	//						System.out.println("interceptor(ex): "+interceptor);
	
						pop();
						fret.setException(exception);
					}
					
					public String toString()
					{
						return "ServiceInvocationContext$1(method="+method.getName()+", result="+result+")";
					}
	
				});
			}
		}
		else
		{
			System.out.println("No interceptor: "+method.getName());
			ret	= new Future<Void>(new RuntimeException("No interceptor found: "+method.getName()));
		}

		return ret;
	}
	
	/**
	 *  Get the next interceptor.
	 */
	public IServiceInvocationInterceptor getNextInterceptor()
	{
		IServiceInvocationInterceptor ret = null;
		
		if(interceptors!=null)
		{
			int start = used.size()==0? -1: (Integer)used.get(used.size()-1);
			for(int i=start+1; i<interceptors.length; i++)
			{
				// add before to allow isApplicable fetch context values.
				used.add(Integer.valueOf(i));
				if(interceptors[i].isApplicable(this))
				{
					ret = interceptors[i];
					break;
				}
				else
				{
					used.remove(Integer.valueOf(i));
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Invoke the next interceptor.
	 */
	public IFuture<Void> invoke()
	{
		return invoke(getObject(), getMethod(), getArguments());
	}

	/**
	 *  Push saves and copies the current set of values.
	 */
	protected void push(Object o, Method m, List<Object> args, Object res)
	{
		// profile on first invoke
		if(PROFILING && method.isEmpty())
		{
//			System.out.println("invoke from "+IComponentIdentifier.LOCAL.get()+": "+m);
			
			IComponentIdentifier	pf	= IComponentIdentifier.LOCAL.get();
			pf	= pf!=null ? pf.getRoot() : platform;
			
			Map<Method, Integer>	pcalls;
			synchronized(calls)
			{
				pcalls	= calls.get(pf);
				if(pcalls==null)
				{
					pcalls	= new HashMap<Method, Integer>();
					calls.put(pf, pcalls);
				}
			}
			synchronized(pcalls)
			{
				Integer	cnt	= pcalls.get(m);
				pcalls.put(m, Integer.valueOf(cnt==null ? 0 : cnt.intValue()+1));
			}
		}
		
		object.add(o);
		method.add(m);
		arguments.add(args);
		result.add(res);
	}
	
	/**
	 *  Pop delete the top most set of values.
	 */
	protected void pop()
	{
		// Keep last results
		if(used.size()>1)
		{
			used.remove(used.size()-1);
			object.remove(object.size()-1);
			method.remove(method.size()-1);
			arguments.remove(arguments.size()-1);
			Object res = this.result.remove(this.result.size()-1);
			result.set(result.size()-1, res);
		}
	}
	
	/**
	 *  Test if a call is remote.
	 */
	public boolean isRemoteCall()
	{
		return caller==null? false: !caller.getRoot().equals(platform) 
			|| (caller.getLocalName().equals("rms") && caller.getRoot().equals(platform)); // Hack? Shouldn't be caller be set to the remote component?
	}
	
//	/**
//	 *  Test if this call is local.
//	 *  @return True, if it is a local call. 
//	 */
//	public boolean isLocalCall()
//	{
//		return !Proxy.isProxyClass(getObject().getClass());
//	}
	
//	/**
//	 *  Test if a call is remote.
//	 *  @param sic The service invocation context.
//	 */
//	public boolean isRemoteCall()
//	{
//		Object target = getObject();
////		if(Proxy.isProxyClass(target.getClass()))
////			System.out.println("blubb "+Proxy.getInvocationHandler(target).getClass().getName());
//		// todo: remove string based remote check! RemoteMethodInvocationHandler is in package jadex.platform.service.remote
//		return Proxy.isProxyClass(target.getClass()) && Proxy.getInvocationHandler(target).getClass().getName().indexOf("Remote")!=-1;
//	}
	
//	/**
//	 * 
//	 */
//	public void copy(ServiceInvocationContext sic)
//	{
//		setObjectStack(sic.getObjectStack());
//		setMethodStack(sic.getMethodStack());
//		setArgumentStack(sic.getArgumentStack());
//		setResultStack(sic.getResultStack());
//		
//	}
	
	/**
	 *  Get the real target object.
	 *  Returns domain service in case of service info.
	 */
	public Object getTargetObject()
	{
		Object ret = getObject();
		if(ret instanceof ServiceInfo)
		{
			ret = ((ServiceInfo)ret).getDomainService();
		}
		return ret;
	}
	
	/**
	 *  Get the caller adapter.
	 */
//	public IComponentAdapter	getCallerAdapter()
//	{
//		return this.calleradapter;
//	}
	
	/**
	 *  Get the caller.
	 *  @return The caller.
	 */
	public IComponentIdentifier getCaller()
	{
		return caller;
	}

	/**
	 *  String representation.
	 */
	public String toString()
	{
		return "ServiceInvocationContext(method="+method+", caller="+caller+")";
	}

	/**
	 *  Get the service call.
	 *  @return The service call.
	 */
	public ServiceCall	getNextServiceCall()
	{
		return nextcall;
	}
	
	/**
	 *  Get the last service call.
	 *  @return The last service call.
	 */
	public ServiceCall	getCurrentServiceCall()
	{
		return currentcall;
	}

	/**
	 *  Set the lastcall. 
	 *  @param currentcall The lastcall to set.
	 */
	public void setNextCall(ServiceCall call)
	{
		this.nextcall = call;
	}

	/**
	 *  Get the service id.
	 */
	public IServiceIdentifier getServiceIdentifier()
	{
		return sid;
	}
	
	/**
	 *  Set the service id.
	 */
	public void setServiceIdentifier(IServiceIdentifier sid)
	{
		this.sid = sid;
	}
}


