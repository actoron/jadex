package jadex.base.service.remote;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.collection.LRU;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IResultSelector;
import jadex.service.ISearchManager;
import jadex.service.IService;
import jadex.service.IVisitDecider;
import jadex.service.SServiceProvider;

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
	public IFuture execute(final IExternalAccess component, Map waitingcalls)
	{
		final Future ret = new Future();
			
		SServiceProvider.getServiceUpwards(component.getServiceProvider(), IComponentManagementService.class)
			.addResultListener(component.createResultListener(new IResultListener()
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
										ServiceProxyInfo pi = getProxyInfo(component.getComponentIdentifier(), tmp);
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
		}));
		
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
	protected ServiceProxyInfo getProxyInfo(IComponentIdentifier rms, IService service)
	{
		ServiceProxyInfo ret;
		
		// This construct ensures
		// a) fast access to existing proxyinfos in the map
		// b) creation is performed only once by ordering threads 
		// via synchronized block and rechecking if proxy was already created.
		
		ret = (ServiceProxyInfo)proxyinfos.get(service);
		if(ret==null)
		{
			synchronized(proxyinfos)
			{
				ret = (ServiceProxyInfo)proxyinfos.get(service);
				if(ret==null)
				{
					ret = createProxyInfo(rms, service);
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Create a proxy info for a service. 
	 */
	protected ServiceProxyInfo createProxyInfo(IComponentIdentifier rms, IService service)
	{
		Class type = service.getServiceIdentifier().getServiceType();
		ServiceProxyInfo ret = new ServiceProxyInfo(rms, service.getServiceIdentifier());
	
		System.out.println("Creating proxy for: "+type);
		Method[] methods = type.getMethods();
		for(int i=0; i<methods.length; i++)
		{
			Class rt = methods[i].getReturnType();
			Class[] ar = methods[i].getParameterTypes();
			
			if(void.class.equals(rt))
			{
//				System.out.println("Warning, void method call will be executed asynchronously: "+type+" "+methods[i].getName());
			}
			else if(!(rt.isAssignableFrom(IFuture.class)))
			{
				if(ar.length>0)
				{
//					System.out.println("Warning, service method is blocking: "+type+" "+methods[i].getName());
				}
				else
				{
					// Invoke method to get constant return value.
					try
					{
						System.out.println("Calling for caching: "+methods[i]);
						Object val = methods[i].invoke(service, new Object[0]);
						ret.putCache(methods[i].getName(), val);
					}
					catch(Exception e)
					{
						System.out.println("Warning, constant service method threw exception: "+type+" "+methods[i]);
//						e.printStackTrace();
					}
				}
			}
		}
		
		proxyinfos.put(service, ret);
		return ret;
	}
}
