package jadex.base.gui;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.xml.annotation.XMLClassname;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Helper class for GUI code to be executed on remote
 *  devices (e.g. android.).
 */
public class SRemoteGui
{
	//-------- methods --------
	
	/**
	 *  Get the service infos for a component.
	 *  @param ea	The component access.
	 *  @return	The provided and required service infos.
	 */
	public static IFuture<Object[]>	getServiceInfos(IExternalAccess ea)
	{
		return ea.scheduleImmediate(new IComponentStep<Object[]>()
		{
			@XMLClassname("getServiceInfos")
			public IFuture<Object[]> execute(IInternalAccess ia)
			{
				final Future<Object[]>	ret	= new Future<Object[]>();
				final RequiredServiceInfo[]	ris	= ia.getServiceContainer().getRequiredServiceInfos();
//				final IServiceIdentifier[] sid
				IIntermediateFuture<IService>	ds	= SServiceProvider.getDeclaredServices(ia.getServiceContainer());
				ds.addResultListener(new ExceptionDelegationResultListener<Collection<IService>, Object[]>(ret)
				{
					public void customResultAvailable(Collection<IService> result)
					{
						ProvidedServiceInfo[]	pis	= new ProvidedServiceInfo[result.size()];
						IServiceIdentifier[]	sis	= new IServiceIdentifier[result.size()];
						Iterator<IService>	it	= result.iterator();
						for(int i=0; i<pis.length; i++)
						{
							IService	service	= it.next();
							// todo: implementation?
							sis[i] = service.getServiceIdentifier();
							pis[i]	= new ProvidedServiceInfo(service.getServiceIdentifier().getServiceName(), 
//								service.getServiceIdentifier().getServiceType(), null, null);
								sis[i].getServiceType().getType(), null, null);
						}
						
						ret.setResult(new Object[]{pis, ris, sis});
					}
				});
				return ret;
			}
		});		
	}
	/**
	 *  Install the remote listener.
	 *  @param cid	The remote component id.
	 */
	public static IFuture<Void>	installRemoteCMSListener(final IExternalAccess access, final IComponentIdentifier cid, final IRemoteChangeListener rcl0, final String id0)
	{
		final Future<Void>	ret	= new Future<Void>();
		SServiceProvider.getService(access.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
		{
			public void customResultAvailable(IComponentManagementService	cms)
			{
//				IComponentManagementService	cms	= (IComponentManagementService)result;
				cms.getExternalAccess(cid).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
				{
					public void customResultAvailable(IExternalAccess exta)
					{
//						IExternalAccess	exta	= (IExternalAccess)result;
						final IComponentIdentifier	icid	= cid;	// internal reference to cid, because java compiler stores final references in outmost object (grrr.)
						final String	id	= id0;
						final IRemoteChangeListener	rcl	= rcl0;
						exta.scheduleStep(new IComponentStep<Void>()
						{
							@XMLClassname("installListener")
							public IFuture<Void> execute(IInternalAccess ia)
							{
								final Future<Void>	ret	= new Future<Void>();
								SServiceProvider.getService(ia.getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
									.addResultListener(ia.createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
								{
									public void customResultAvailable(IComponentManagementService cms)
									{
										RemoteCMSListener	rcmsl	= new RemoteCMSListener(icid, id, cms, rcl);
										cms.addComponentListener(null, rcmsl);
										ret.setResult(null);
									}
								}));
								return ret;
							}
						}).addResultListener(new DelegationResultListener<Void>(ret));
					}
				});
			}
		});
		return ret;
	}

	/**
	 *  Deregister the remote listener.
	 */
	public static IFuture<Void>	deregisterRemoteCMSListener(final IExternalAccess access, final IComponentIdentifier cid, final String id0)
	{
		final Future<Void>	ret	= new Future<Void>();
		SServiceProvider.getService(access.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				cms.getExternalAccess(cid).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
				{
					public void customResultAvailable(IExternalAccess exta)
					{
						final String	id	= id0;
						exta.scheduleStep(new IComponentStep<Void>()
						{
							@XMLClassname("deregisterListener")
							public IFuture<Void> execute(IInternalAccess ia)
							{
								final Future<Void>	ret	= new Future<Void>();
								SServiceProvider.getService(ia.getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
									.addResultListener(ia.createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
								{
									public void customResultAvailable(IComponentManagementService cms)
									{
//										System.out.println("Removing listener: "+id);
										try
										{
											cms.removeComponentListener(null, new RemoteCMSListener(cid, id, cms, null));
										}
										catch(RuntimeException e)
										{
		//									System.out.println("Listener already removed: "+id);
										}
										ret.setResult(null);
									}
								}));
								return ret;
							}
						}).addResultListener(new DelegationResultListener<Void>(ret));
					}
				});
			}
		});
		return ret;
	}
}
