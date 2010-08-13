package jadex.base.service.remote;

import jadex.bridge.Argument;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IRemoteServiceManagementService;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.CollectionResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;
import jadex.service.CacheServiceContainer;
import jadex.service.IServiceContainer;
import jadex.service.SServiceProvider;

/**
 *  A proxy agent is a pseudo component that mirrors services of a remote platform (or component).
 */
public class ProxyAgent extends MicroAgent
{
	/**
	 *  Get the service container.
	 *  @return The service container.
	 */
	public IServiceContainer createServiceContainer()
	{
//		System.out.println("Proxy for: "+getRemotePlatformIdentifier()+" "
//			+SUtil.arrayToString(getRemotePlatformIdentifier().getAddresses()));
		return new CacheServiceContainer(new RemoteServiceContainer(
			getRemotePlatformIdentifier(), getAgentAdapter()), 25, 1*30*1000); // 30 secs cache expire
	}
	
	/**
	 *  Get the platform identifier.
	 *  @return The platform identifier.
	 */
	public IComponentIdentifier getRemotePlatformIdentifier()
	{
		return (IComponentIdentifier)getArgument("platform");
	}
	
	/**
	 *  Get the remote, i.e. virtual children of a component. 
	 */
	public IFuture getVirtualChildren(final IComponentIdentifier cid)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(getServiceProvider(), IRemoteServiceManagementService.class)
			.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IRemoteServiceManagementService rms = (IRemoteServiceManagementService)result;
				
				rms.getServiceProxy(cid, IComponentManagementService.class).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						final IComponentManagementService rcms = (IComponentManagementService)result;
						rcms.getChildren(cid).addResultListener(new IResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
//								System.out.println("Found children: "+SUtil.arrayToString(result));
								
								IComponentIdentifier[] tmp = (IComponentIdentifier[])result;
								
								CollectionResultListener crl = new CollectionResultListener(tmp.length, false, new DelegationResultListener(ret));
								for(int i=0; i<tmp.length; i++)
								{
									rcms.getComponentDescription(tmp[i]).addResultListener(crl);
								}
							}
							
							public void exceptionOccurred(Object source, Exception exception)
							{
								System.out.println("Children exception: "+getComponentIdentifier());
								ret.setException(exception);
							}
						});
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						System.out.println("No remote cms found: "+getComponentIdentifier());
						ret.setException(exception);
					}
				});
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				System.out.println("No rms found: "+getComponentIdentifier());
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the remote component description. 
	 */
	public IFuture getRemoteComponentDescription(final IComponentIdentifier cid)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(getServiceProvider(), IRemoteServiceManagementService.class)
			.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IRemoteServiceManagementService rms = (IRemoteServiceManagementService)result;
				
				rms.getServiceProxy(cid, IComponentManagementService.class).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						final IComponentManagementService rcms = (IComponentManagementService)result;
						rcms.getComponentDescription(cid).addResultListener(new DelegationResultListener(ret));
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						ret.setException(exception);
					}
				});
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	/**
	 *  Get the declared services of a remote component.
	 */
	public IFuture getRemoteServices(final IComponentIdentifier cid)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(getServiceProvider(), IRemoteServiceManagementService.class)
			.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IRemoteServiceManagementService rms = (IRemoteServiceManagementService)result;
				rms.getDeclaredServiceProxies(cid).addResultListener(new DelegationResultListener(ret));
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the remote, i.e. virtual children of a component. 
	 * /
	public IFuture getVirtualChildren(final IComponentIdentifier cid)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(getServiceProvider(), IComponentManagementService.class)
			.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{	
				final IComponentManagementService cms = (IComponentManagementService)result;
				
				SServiceProvider.getService(getServiceProvider(), IRemoteServiceManagementService.class)
					.addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						IRemoteServiceManagementService rms = (IRemoteServiceManagementService)result;
						
						rms.getExternalAccessProxy(cid, IExternalAccess.class).addResultListener(new IResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
								IExternalAccess exta = (IExternalAccess)result;
								exta.getChildren().addResultListener(new IResultListener()
								{
									public void resultAvailable(Object source, Object result)
									{
										Collection extas = (Collection)result;
										CollectionResultListener crl = new CollectionResultListener(extas.size(), new DelegationResultListener(ret));
										for(int i=0; i<extas.size(); i++)
										{
											IExternalAccess tmp = (IExternalAccess)result;
											cms.getComponentDescription(tmp.getComponentIdentifier()).addResultListener(crl);
										}
									}
									
									public void exceptionOccurred(Object source, Exception exception)
									{
										ret.setException(exception);
									}
								});
							}
							
							public void exceptionOccurred(Object source, Exception exception)
							{
								ret.setException(exception);
							}
						});
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						ret.setException(exception);
					}
				});
			}
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}*/
	
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static Object getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agent represents a proxy for a remote component.", 
			new String[0], new IArgument[]{
			new Argument("platform", "The component id of the remote platform", "jadex.bridge.IComponentIdentifier", 
				new ComponentIdentifier("remote", new String[]{"tcp-mtp://127.0.0.1:11000", "nio-mtp://127.0.0.1:11001"}))}, 
			null, null, null);
	}

}