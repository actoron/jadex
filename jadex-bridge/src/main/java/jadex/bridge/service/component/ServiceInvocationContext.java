package jadex.bridge.service.component;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 *  Context for service invocations.
 *  Contains all method call information. 
 */
public class ServiceInvocationContext
{
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
	protected List object;
	
	/** The method to be called. */
	protected List method;
	
	/** The invocation arguments. */
	protected List arguments;
	
	/** The call result. */
	protected List result;
	

	/** The service interceptors. */
	protected IServiceInvocationInterceptor[] interceptors;

	/** The stack of used interceptors. */
	protected List used;
	
	/** The caller component. */
	protected IComponentIdentifier caller;
	
	/** The caller component adapter. */
	protected IComponentAdapter calleradapter;
	
	/** The platform identifier. */
	protected IComponentIdentifier platform;
	
	//-------- constructors --------
	
	/**
	 *  Create a new context.
	 */
	public ServiceInvocationContext(Object proxy, IServiceInvocationInterceptor[] interceptors, IComponentIdentifier platform)
	{
		this.platform = platform;
		this.proxy = proxy;
		this.object = new ArrayList();
		this.method = new ArrayList();
		this.arguments = new ArrayList();
		this.result = new ArrayList();
		
		this.used = new ArrayList();
		this.interceptors = interceptors;
		
		this.caller = IComponentIdentifier.LOCAL.get();
		this.calleradapter	= IComponentAdapter.LOCAL.get();
	}
	
	/**
	 *  Create a copied context.
	 */
	public ServiceInvocationContext(ServiceInvocationContext context)
	{
		this.platform = context.platform;
		this.proxy = context.proxy;
		this.object = new ArrayList(context.object);
		this.method = new ArrayList(context.method);
		this.arguments = new ArrayList(context.arguments);
		this.result = new ArrayList(context.result);
		
		this.used = new ArrayList(context.used);
		this.interceptors = context.interceptors;
		
		this.caller = context.caller;
		this.calleradapter = context.calleradapter;
	}
	
	/**
	 *  Clone a service invocation context.
	 */
	public ServiceInvocationContext	clone()
	{
		return new ServiceInvocationContext(this); 
	}

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
	public List getArguments()
	{
		return (List)arguments.get(used.size()-1);
	}
	
	/**
	 *  Get the args.
	 *  @return the args.
	 */
	public Object[] getArgumentArray()
	{
		List args = (List)arguments.get(used.size()-1);
		return args!=null? args.toArray(): new Object[0];
	}
	
	/**
	 *  Set the arguments.
	 *  @param args The arguments to set.
	 */
	public void setArguments(List args)
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
//		if(getMethod().getName().indexOf("subsc")!=-1)
//			System.out.println("gotta");
		this.result.set(used.size()-1, result);
	}

	/**
	 *  Invoke the next interceptor.
	 */
	public IFuture<Void> invoke(Object object, final Method method, List args)
	{
		final Future<Void> ret = new Future<Void>();
		
//		if(method.getName().equals("testResultReferences"))
//			System.out.println("invoke: "+caller);
		
		push(object, method, args, null);
		
		final IServiceInvocationInterceptor interceptor = getNextInterceptor();

//		if(method.getName().equals("getResult"))
//			System.out.println("getResult: "+used.get(used.size()-1)+" "+interceptor+" "+Thread.currentThread());
		
		if(interceptor!=null)
		{
//			if(method.getName().equals("isValid"))
//				System.out.println("interceptor1: "+interceptor);
			interceptor.execute(this).addResultListener(new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
//					if(method.getName().equals("getResult"))
//						System.out.println("interceptor2: "+interceptor);

					pop();
					ret.setResult(null);
				}
				
				public void exceptionOccurred(Exception exception)
				{
//					if(method.getName().equals("isValid"))
//						System.out.println("interceptor(ex): "+interceptor);

					pop();
					ret.setException(exception);
				}
				
				public String toString()
				{
					return "ServiceInvocationContext$1(method="+method.getName()+", result="+result+")";
				}

			});
		}
		else
		{
			System.out.println("No interceptor: "+method.getName());
			ret.setException(new RuntimeException("No interceptor found: "+method.getName()));
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
				used.add(new Integer(i));
				if(interceptors[i].isApplicable(this))
				{
					ret = interceptors[i];
					break;
				}
				else
				{
					used.remove(new Integer(i));
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
	protected void push(Object o, Method m, List args, Object res)
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
				pcalls.put(m, new Integer(cnt==null ? 0 : cnt.intValue()+1));
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
		return caller==null? false: !caller.getRoot().equals(platform);
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
			ret = ((ServiceInfo)object).getDomainService();
		}
		return ret;
	}
	
	/**
	 *  Get the caller adapter.
	 */
	public IComponentAdapter	getCallerAdapter()
	{
		return this.calleradapter;
	}
	
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
}


