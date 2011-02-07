package jadex.base.service.remote;

import jadex.bridge.Argument;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IRemoteServiceManagementService;
import jadex.commons.collection.LRU;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.service.IServiceContainer;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.clock.IClockService;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

import java.util.Collection;

/**
 *  A proxy agent is a pseudo component that mirrors services of a remote platform (or component).
 */
public class ProxyAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The refresh delay. */
	protected long delay;
	
	/** The cached children. */
	protected LRU children;
	
	//-------- methods --------
	
	/**
	 *  Get the service container.
	 *  @return The service container.
	 */
	public IServiceContainer createServiceContainer()
	{
		this.delay = ((Number)getArgument("delay")).longValue();
		int cachesize = ((Number)getArgument("cachesize")).intValue();
		this.children = new LRU(cachesize);
		
//		System.out.println("proxy delay: "+delay);
		
//		System.out.println("Proxy for: "+getRemotePlatformIdentifier()
//			+", "+SUtil.arrayToString(getRemotePlatformIdentifier().getAddresses())
//			+", "+delay+", "+cachesize);
//		return new CacheServiceContainer(new RemoteServiceContainer(
//			getRemotePlatformIdentifier(), getAgentAdapter()), 25, 1*30*1000); // 30 secs cache expire
		return new RemoteServiceContainer(getRemotePlatformIdentifier(), getAgentAdapter());
	}
	
	/**
	 *  Get the platform identifier.
	 *  @return The platform identifier.
	 */
	public IComponentIdentifier getRemotePlatformIdentifier()
	{
		return (IComponentIdentifier)getArgument("component");
	}
	
	/**
	 *  Test if the cached children are still valid.
	 */
	protected IFuture isInvalid(final IComponentIdentifier cid)
	{
		final Future ret = new Future();
		
		final Object[] entry = (Object[])children.get(cid);
		
		if(delay==0)
		{
			ret.setResult(Boolean.FALSE);
		}
		else if(entry==null)
		{
			ret.setResult(Boolean.TRUE);
		}
		else
		{
			SServiceProvider.getService(getServiceProvider(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(createResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					IClockService cs = (IClockService)result;
					long time = cs.getTime();
					
					long lastaccess = ((Long)entry[0]).longValue();
//					System.out.println("here: "+cid+" "+(time>lastaccess+delay)+" "+time+" "+lastaccess+" "+delay);
					ret.setResult(time>lastaccess+delay? Boolean.TRUE: Boolean.FALSE);
				}
				
				public void exceptionOccurred(Exception exception)
				{
					ret.setException(exception);
				}
			}));
		}
		
		return ret;
	}
	
	/**
	 *  Get the remote, i.e. virtual children of a component. 
	 */
	public IFuture getVirtualChildren(final IComponentIdentifier cid, boolean force)
	{
		final Future ret = new Future();
		
		if(force)
		{
			searchVirtualChildren(cid).addResultListener(createResultListener(new DelegationResultListener(ret)));
		}
		else
		{
			isInvalid(cid).addResultListener(createResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					if(((Boolean)result).booleanValue())
					{
	//					System.out.println("search children");
						searchVirtualChildren(cid).addResultListener(createResultListener(new DelegationResultListener(ret)));
					}
					else
					{
	//					System.out.println("cached children");
						ret.setResult(((Object[])children.get(cid))[1]);
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
					ret.setException(exception);
				}
			}));
		}
		
		return ret;
	}
	
	/**
	 *  Search the virtual children via a remote call.
	 */
	protected IFuture searchVirtualChildren(final IComponentIdentifier cid)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(getServiceProvider(), IRemoteServiceManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				IRemoteServiceManagementService rms = (IRemoteServiceManagementService)result;
				
				rms.getServiceProxy(cid, IComponentManagementService.class).addResultListener(createResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
//						try
//						{
//							IComponentManagementService rcms = (IComponentManagementService)result;
//						}
//						catch(Exception e)
//						{
//							System.out.println("heer: "+SUtil.arrayToString(result.getClass().getInterfaces()));
//						}
						
						final IComponentManagementService rcms = (IComponentManagementService)result;
						rcms.getChildren(cid).addResultListener(createResultListener(new IResultListener()
						{
							public void resultAvailable(Object result)
							{
	//							System.out.println("Found children: "+SUtil.arrayToString(result));
								IComponentIdentifier[] tmp = (IComponentIdentifier[])result;
								
								CollectionResultListener crl = new CollectionResultListener(tmp.length, false, new DelegationResultListener(ret)
								{
									public void customResultAvailable(Object result)
									{
										final Collection vcs = (Collection)result; 
										SServiceProvider.getService(getServiceProvider(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
											.addResultListener(createResultListener(new IResultListener()
										{
											public void resultAvailable(Object result)
											{
												IClockService cs = (IClockService)result;
												long lastaccess = cs.getTime();
//												System.out.println("cs of "+cid+" "+vcs);
												children.put(cid, new Object[]{new Long(lastaccess), vcs});
												ret.setResult(vcs);
											}
											
											public void exceptionOccurred(Exception exception)
											{
												ret.setException(exception);
											}
										}));
									}
								});

								for(int i=0; i<tmp.length; i++)
								{
									rcms.getComponentDescription(tmp[i]).addResultListener(createResultListener(crl));
								}
							}
							
							public void exceptionOccurred(Exception exception)
							{
	//							System.out.println("Children exception: "+getComponentIdentifier());
								ret.setException(exception);
							}
						}));
					}
					
					public void exceptionOccurred(Exception exception)
					{
	//					System.out.println("No remote cms found: "+getComponentIdentifier());
						ret.setException(exception);
					}
				}));
			}
			
			public void exceptionOccurred(Exception exception)
			{
	//			System.out.println("No rms found: "+getComponentIdentifier());
				ret.setException(exception);
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Get the remote component description. 
	 */
	public IFuture getRemoteComponentDescription(final IComponentIdentifier cid)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(getServiceProvider(), IRemoteServiceManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				IRemoteServiceManagementService rms = (IRemoteServiceManagementService)result;
				
				rms.getServiceProxy(cid, IComponentManagementService.class).addResultListener(createResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						final IComponentManagementService rcms = (IComponentManagementService)result;
						rcms.getComponentDescription(cid).addResultListener(createResultListener(new DelegationResultListener(ret)));
					}
					
					public void exceptionOccurred(Exception exception)
					{
						ret.setException(exception);
					}
				}));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Get the declared services of a remote component.
	 */
	public IFuture getRemoteServices(final IComponentIdentifier cid)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(getServiceProvider(), IRemoteServiceManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				IRemoteServiceManagementService rms = (IRemoteServiceManagementService)result;
				rms.getDeclaredServiceProxies(cid).addResultListener(createResultListener(new DelegationResultListener(ret)));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		}));
		
		return ret;
	}
	
	//-------- static methods --------
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static Object getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agent represents a proxy for a remote component.", 
			new String[0], new IArgument[]{
			new Argument("component", "The component id of the remote component/platform", "jadex.bridge.IComponentIdentifier", 
				new ComponentIdentifier("remote", new String[]{"tcp-mtp://127.0.0.1:11000", "nio-mtp://127.0.0.1:11001"})), 
			new Argument("delay", "The cache delay, determines the time how long a virtual children search is valid", "long", new Long(10000)), 
			new Argument("cachesize", "The maximum number of entries in the cache.", "int", new Integer(1000))}, 
			null, null, null);
	}
	
}