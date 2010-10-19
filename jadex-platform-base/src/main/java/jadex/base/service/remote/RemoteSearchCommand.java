package jadex.base.service.remote;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.LRU;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.IResultSelector;
import jadex.commons.service.ISearchManager;
import jadex.commons.service.IService;
import jadex.commons.service.IVisitDecider;
import jadex.commons.service.SServiceProvider;
import jadex.micro.IMicroExternalAccess;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  Command for performing a remote service search.
 */
public class RemoteSearchCommand implements IRemoteCommand
{
	//-------- attributes --------

	/** The cache of proxy infos. */
	protected static Map proxyinfos = Collections.synchronizedMap(new LRU(200));
	
	/** The providerid (i.e. the component to start with searching). */
	protected Object providerid;
	
	/** The serach manager. */
	protected ISearchManager manager;
	
	/** The visit decider. */
	protected IVisitDecider decider;
	
	/** The result selector. */
	protected IResultSelector selector;
	
	/** The callid. */
	protected String callid;
	
	//-------- constructors --------
	
	/**
	 *  Create a new remote search command.
	 */
	public RemoteSearchCommand()
	{
	}

	/**
	 *  Create a new remote search command.
	 */
	public RemoteSearchCommand(Object providerid, ISearchManager manager, 
		IVisitDecider decider, IResultSelector selector, String callid)
	{
		this.providerid = providerid;
		this.manager = manager;
		this.decider = decider;
		this.selector = selector;
		this.callid = callid;
	}

	//-------- methods --------
	
	/**
	 *  Execute the command.
	 *  @param lrms The local remote management service.
	 *  @return An optional result command that will be 
	 *  sent back to the command origin. 
	 */
	public IFuture execute(final IMicroExternalAccess component, Map waitingcalls)
	{
		final Future ret = new Future();
			
		SServiceProvider.getServiceUpwards(component.getServiceProvider(), IComponentManagementService.class)
			.addResultListener(new IResultListener()
//			.addResultListener(component.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				cms.getExternalAccess((IComponentIdentifier)providerid).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						IExternalAccess exta = (IExternalAccess)result;
						
						// start serach on target component
//						System.out.println("rem search start: "+manager+" "+decider+" "+selector);
						exta.getServiceProvider().getServices(manager, decider, selector, new ArrayList())
							.addResultListener(new IResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
//								System.out.println("rem search end: "+manager+" "+decider+" "+selector+" "+result);
								// Create proxy info(s) for service(s)
								Object content = null;
								if(result instanceof Collection)
								{
									List res = new ArrayList();
									for(Iterator it=((Collection)result).iterator(); it.hasNext(); )
									{
										IService tmp = (IService)it.next();
										ProxyInfo pi = getProxyInfo(component.getComponentIdentifier(), tmp);
										res.add(pi);
									}
									content = res;
								}
								else //if(result instanceof Object[])
								{
									IService tmp = (IService)result;
									content = getProxyInfo(component.getComponentIdentifier(), tmp);
								}
								
								ret.setResult(new RemoteSearchResultCommand(content, null , callid));
							}
							
							public void exceptionOccurred(Object source, Exception exception)
							{
								ret.setResult(new RemoteSearchResultCommand(null, exception, callid));
							}
						});
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						ret.setResult(new RemoteSearchResultCommand(null, exception, callid));
					}
				});
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setResult(new RemoteSearchResultCommand(null, exception, callid));
			}
		});
		
		return ret;
	}

	/**
	 *  Get the providerid.
	 *  @return the providerid.
	 */
	public Object getProviderId()
	{
		return providerid;
	}

	/**
	 *  Set the providerid.
	 *  @param providerid The providerid to set.
	 */
	public void setProviderId(Object providerid)
	{
		this.providerid = providerid;
	}

	/**
	 *  Get the manager.
	 *  @return the manager.
	 */
	public ISearchManager getSearchManager()
	{
		return manager;
	}

	/**
	 *  Set the manager.
	 *  @param manager The manager to set.
	 */
	public void setSearchManager(ISearchManager manager)
	{
		this.manager = manager;
	}

	/**
	 *  Get the decider.
	 *  @return the decider.
	 */
	public IVisitDecider getVisitDecider()
	{
		return decider;
	}

	/**
	 *  Set the decider.
	 *  @param decider The decider to set.
	 */
	public void setVisitDecider(IVisitDecider decider)
	{
		this.decider = decider;
	}

	/**
	 *  Get the selector.
	 *  @return the selector.
	 */
	public IResultSelector getResultSelector()
	{
		return selector;
	}

	/**
	 *  Set the selector.
	 *  @param selector The selector to set.
	 */
	public void setResultSelector(IResultSelector selector)
	{
		this.selector = selector;
	}

	/**
	 *  Get the callid.
	 *  @return the callid.
	 */
	public String getCallId()
	{
		return callid;
	}

	/**
	 *  Set the callid.
	 *  @param callid The callid to set.
	 */
	public void setCallId(String callid)
	{
		this.callid = callid;
	}
	
	/**
	 *  Get a proxy info for a service. 
	 */
	public static ProxyInfo getProxyInfo(IComponentIdentifier rms, IService service)
	{
		ProxyInfo ret;
		
		// This construct ensures
		// a) fast access to existing proxyinfos in the map
		// b) creation is performed only once by ordering threads 
		// via synchronized block and rechecking if proxy was already created.
		
		ret = (ProxyInfo)proxyinfos.get(service);
		if(ret==null)
		{
			synchronized(proxyinfos)
			{
				ret = (ProxyInfo)proxyinfos.get(service);
				if(ret==null)
				{
					ret = createProxyInfo(rms, service);
					proxyinfos.put(service, ret);
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Create a proxy info for a service. 
	 */
	public static ProxyInfo createProxyInfo(IComponentIdentifier rms, IService service)
	{
		ProxyInfo ret = new ProxyInfo(rms, service.getServiceIdentifier());
		fillProxyInfo(ret, service, service.getServiceIdentifier().getServiceType(), service.getPropertyMap());
		return ret;
//		System.out.println("Creating proxy for: "+type);
	}
	
	/**
	 *  Fill a proxy with method information.
	 */
	public static void fillProxyInfo(ProxyInfo pi, final Object target, Class targetclass, Map properties)
	{
		// Check for excluded and synchronous methods.
		Object ex = properties.get(RemoteServiceManagementService.REMOTE_EXCLUDED);
		if(ex!=null)
		{
			for(Iterator it = SReflect.getIterator(ex); it.hasNext(); )
			{
				MethodInfo[] mis = getMethodInfo(it.next(), targetclass, false);
				for(int i=0; i<mis.length; i++)
				{
					pi.addExcludedMethod(mis[i]);
				}
			}
		}
		Object syn = properties.get(RemoteServiceManagementService.REMOTE_SYNCHRONOUS);
		if(syn!=null)
		{
			for(Iterator it = SReflect.getIterator(syn); it.hasNext(); )
			{
				MethodInfo[] mis = getMethodInfo(it.next(), targetclass, false);
				for(int i=0; i<mis.length; i++)
				{
					pi.addSynchronousMethod(mis[i]);
				}
			}
		}
		Object un = properties.get(RemoteServiceManagementService.REMOTE_UNCACHED);
		if(un!=null)
		{
			for(Iterator it = SReflect.getIterator(un); it.hasNext(); )
			{
				MethodInfo[] mis = getMethodInfo(it.next(), targetclass, false);
				for(int i=0; i<mis.length; i++)
				{
					pi.addUncachedMethod(mis[i]);
				}
			}
		}
		Object mr = properties.get(RemoteServiceManagementService.REMOTE_METHODREPLACEMENT);
		if(mr!=null)
		{
			for(Iterator it = SReflect.getIterator(mr); it.hasNext(); )
			{
				Object[] tmp = (Object[])it.next();
				MethodInfo[] mis = getMethodInfo(tmp[0], targetclass, false);
				for(int i=0; i<mis.length; i++)
				{
					pi.addMethodReplacement(mis[i], (IMethodReplacement)tmp[1]);
				}
			}
		}
		
		// Add default replacement for equals() and hashCode().
		Method	equals	= SReflect.getMethod(Object.class, "equals", new Class[]{Object.class});
		if(pi.getMethodReplacement(equals)==null)
		{
			MethodInfo[] mis = getMethodInfo(equals, targetclass, false);
			for(int i=0; i<mis.length; i++)
			{
				pi.addMethodReplacement(mis[i], new DefaultEqualsMethodReplacement());
			}
		}
		Method	hashcode = SReflect.getMethod(Object.class, "hashCode", new Class[0]);
		if(pi.getMethodReplacement(hashcode)==null)
		{
			MethodInfo[] mis = getMethodInfo(hashcode, targetclass, true);
			for(int i=0; i<mis.length; i++)
			{
				pi.addMethodReplacement(mis[i], new DefaultHashcodeMethodReplacement());
			}
		}
		// Add getClass as excluded. Otherwise the target class must be present on
		// the computer which only uses the proxy.
		Method getclass = SReflect.getMethod(Object.class, "getClass", new Class[0]);
		if(pi.getMethodReplacement(getclass)==null)
		{
			pi.addExcludedMethod(new MethodInfo(getclass));
		}
		
		// Check methods and possibly cache constant calls.
		Method[] methods = targetclass.getMethods();
		methods	= (Method[])SUtil.joinArrays(methods, Object.class.getMethods());
		for(int i=0; i<methods.length; i++)
		{
			// only cache when not excluded, not cached and not replaced
			if(!pi.isUncached(methods[i]) && !pi.isExcluded(methods[i]) && !pi.isReplaced(methods[i])) 
			{
				Class rt = methods[i].getReturnType();
				Class[] ar = methods[i].getParameterTypes();
				
				if(void.class.equals(rt))
				{
//					System.out.println("Warning, void method call will be executed asynchronously: "+type+" "+methods[i].getName());
				}
				else if(!(rt.isAssignableFrom(IFuture.class)))
				{
					if(ar.length>0)
					{
//						System.out.println("Warning, service method is blocking: "+type+" "+methods[i].getName());
					}
					else
					{
						// Invoke method to get constant return value.
						try
						{
//							System.out.println("Calling for caching: "+methods[i]);
							Object val = methods[i].invoke(target, new Object[0]);
							pi.putCache(methods[i].getName(), val);
						}
						catch(Exception e)
						{
							System.out.println("Warning, constant service method threw exception: "+targetclass+" "+methods[i]);
	//						e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	/**
	 *  Get method.
	 */
	public static MethodInfo[] getMethodInfo(Object tmp, Class targetclass, boolean noargs)
	{
		MethodInfo[] ret;
		
		if(tmp instanceof String)
		{
			if(noargs)
			{
				Method	method	= SReflect.getMethod(targetclass, (String)tmp, new Class[0]);
				if(method==null)
					method	= SReflect.getMethod(Object.class, (String)tmp, new Class[0]);
				
				if(method!=null)
				{
					ret = new MethodInfo[]{new MethodInfo(method)};
				}
				else
				{
					throw new RuntimeException("Method not found: "+tmp);
				}
			}
			else
			{
				Method[] ms = SReflect.getMethods(targetclass, (String)tmp);
				if(ms.length==0)
				{
					ms = SReflect.getMethods(Object.class, (String)tmp);
				}
				
				if(ms.length==1)
				{
					ret = new MethodInfo[]{new MethodInfo(ms[0])};
				}
				else if(ms.length>1)
				{
					// Exclude all if more than one fits?!
					ret = new MethodInfo[ms.length];
					for(int i=0; i<ret.length; i++)
						ret[i] = new MethodInfo(ms[i]);
					
					// Check if the methods are equal = same signature (e.g. defined in different interfaces)
//					boolean eq = true;
//					Method m0 = ms[0];
//					for(int i=1; i<ms.length && eq; i++)
//					{
//						if(!hasEqualSignature(m0, ms[i]))
//							eq = false;
//					}
//					if(!eq)
//						throw new RuntimeException("More than one method with the name availble: "+tmp);
//					else
//						ret = new MethodInfo(m0);
				}
				else
				{
					throw new RuntimeException("Method not found: "+tmp);
				}
			}
		}
		else
		{
			ret = new MethodInfo[]{new MethodInfo((Method)tmp)};
		}
		
		return ret;
	}

	
	
	
	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "RemoteSearchCommand(providerid=" + providerid + ", manager="
			+ manager + ", decider=" + decider + ", selector=" + selector
			+ ", callid=" + callid + ")";
	}
}
