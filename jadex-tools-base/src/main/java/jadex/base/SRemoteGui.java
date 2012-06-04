package jadex.base;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.transformation.annotations.Classname;

import java.net.URL;
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
			@Classname("getServiceInfos")
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
							@Classname("installListener")
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
							@Classname("deregisterListener")
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
	
	/**
	 *  Localize a model name.
	 *  The result can be e.g. used to save settings.
	 *  @return A tuple with the relative local model name and the relative local RID URL.
	 */
	public static IFuture<Tuple2<String, String>>	localizeModel(IExternalAccess platformaccess, final String name, final IResourceIdentifier rid)
	{
		return platformaccess.scheduleStep(new IComponentStep<Tuple2<String, String>>()
		{
			@Classname("localizeModel")
			public IFuture<Tuple2<String, String>> execute(IInternalAccess ia)
			{
				final Future<Tuple2<String, String>>	ret	= new Future<Tuple2<String, String>>();
				// Test, if model can be loaded.
				SComponentFactory.loadModel(ia.getExternalAccess(), name, rid)
					.addResultListener(ia.createResultListener(new ExceptionDelegationResultListener<IModelInfo, Tuple2<String, String>>(ret)
				{
					public void customResultAvailable(IModelInfo result)
					{
						String	model	= SUtil.convertPathToRelative(name);
						String	ridurl	= SUtil.convertPathToRelative(rid.getLocalIdentifier().getUrl().toString());
						ret.setResult(new Tuple2<String, String>(model, ridurl));
					}
					public void exceptionOccurred(Exception exception)
					{
						ret.setResult(null);
					}
				}));
				return ret;
			}
		});
	}
	
	/**
	 *  Create a resource identifier.
	 *  @param ridurl	The (possibly relative) local RID URL.
	 *  @param globalrid	The global RID, if any.
	 *  @return A valid RID for the platform.
	 */
	public static IFuture<IResourceIdentifier>	createResourceIdentifier(IExternalAccess platformaccess, final String ridurl, final String globalrid)
	{
		return platformaccess.scheduleStep(new IComponentStep<IResourceIdentifier>()
		{
			@Classname("createResourceIdentifier")
			public IFuture<IResourceIdentifier> execute(IInternalAccess ia)
			{
				Future<IResourceIdentifier> ret = new Future<IResourceIdentifier>();
				
				// What to do if ridurl is null, use library service?
				if(ridurl==null && globalrid==null)
				{
					ret.setResult(ia.getModel().getResourceIdentifier());
				}
				else
				{
					URL	url	= SUtil.toURL(ridurl);
					LocalResourceIdentifier lid = url==null? null: new LocalResourceIdentifier(ia.getComponentIdentifier().getRoot(), url);
					ret.setResult(new ResourceIdentifier(lid, globalrid));
				}
				
				return ret;
			}
		});
	}
}
