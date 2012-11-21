package jadex.base;

import jadex.bridge.GlobalResourceIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.RemoteChangeListenerHandler;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.deployment.FileData;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.transformation.annotations.Classname;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
					ret.setResult(new ResourceIdentifier(lid, globalrid!=null? new GlobalResourceIdentifier(globalrid, null, null): null));
				}
				
				return ret;
			}
		});
	}
	
	/**
	 *  Get the file info of a remote path.
	 */
	public static IFuture<FileData>	getFileData(IExternalAccess platformaccess, final String path)
	{
		return platformaccess.scheduleStep(new IComponentStep<FileData>()
		{
			@Classname("getRemoteFile")
			public IFuture<FileData> execute(IInternalAccess ia)
			{
				return new Future<FileData>(new FileData(new File(SUtil.convertPathToRelative(path))));
			}
		});		
	}
	
	/**
	 *  Add a URL to the lib service.
	 */
	public static IFuture<Tuple2<URL, IResourceIdentifier>>	addURL(IExternalAccess access, final String filename)
	{
		return access.scheduleStep(new IComponentStep<Tuple2<URL, IResourceIdentifier>>()
		{
			@Classname("addurl")
			public IFuture<Tuple2<URL, IResourceIdentifier>> execute(IInternalAccess ia)
			{
				final URL	url	= SUtil.toURL(filename);
				final Future<Tuple2<URL, IResourceIdentifier>>	ret	= new Future<Tuple2<URL, IResourceIdentifier>>();
				ia.getServiceContainer().searchService(ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new ExceptionDelegationResultListener<ILibraryService, Tuple2<URL, IResourceIdentifier>>(ret)
				{
					public void customResultAvailable(final ILibraryService ls)
					{
						ls.getAllResourceIdentifiers().addResultListener(new ExceptionDelegationResultListener<List<IResourceIdentifier>, Tuple2<URL, IResourceIdentifier>>(ret)
						{
							public void customResultAvailable(List<IResourceIdentifier> rids)
							{
//								System.out.println("rids are: "+rids);
								
								// this ugly piece of code checks if test-classes are added
								// in this case it searched if the original package was also added
								// and if yes it is added as dependency to the test-package
								// this makes the necessary classes available for the test case
								
								String suftc = "test-classes";
								String s2 = url.toString();
								if(s2.endsWith(suftc))
									s2 = s2 + "/";
								suftc = "test-classes/";
								
								IResourceIdentifier tmp = null;
								if(s2.endsWith(suftc) && url.getProtocol().equals("file"))
								{
									String st2 = s2.substring(0, s2.lastIndexOf(suftc));
									for(IResourceIdentifier rid: rids)
									{
										if(rid.getLocalIdentifier()!=null)
										{
											URL u1 = rid.getLocalIdentifier().getUrl();
											String s1 = u1.toString();
											String sufc = "classes";
											if(s1.endsWith(sufc))
												s1 = s1 + "/";
											sufc = "classes/";
											
											if(s1.endsWith(sufc) && u1.getProtocol().equals("file"))
											{
												String st1 = s1.substring(0, s1.lastIndexOf(sufc));
												if(st1.equals(st2))
												{
													tmp = rid;
//														System.out.println("url: "+u1.getPath());
													break;
												}
											}
										}
									}
								}
								final IResourceIdentifier deprid = tmp;
								
								// todo: workspace=true?
								ls.addURL(null, url).addResultListener(new ExceptionDelegationResultListener<IResourceIdentifier, Tuple2<URL, IResourceIdentifier>>(ret)
								{
									public void customResultAvailable(IResourceIdentifier rid)
									{
										if(deprid!=null)
										{
											ls.addResourceIdentifier(rid, deprid, true).addResultListener(new ExceptionDelegationResultListener<IResourceIdentifier, Tuple2<URL, IResourceIdentifier>>(ret)
											{
												public void customResultAvailable(IResourceIdentifier rid)
												{
													ret.setResult(new Tuple2<URL, IResourceIdentifier>(url, rid));
												}
											});
										}
										else
										{
											ret.setResult(new Tuple2<URL, IResourceIdentifier>(url, rid));
										}
									}
									public void exceptionOccurred(Exception exception)
									{
//										exception.printStackTrace();
										super.exceptionOccurred(exception);
									}
								});
							}
						});
					}
				});
				
				return ret;
			}
		});
	}
	
	/**
	 *  Remove a URL from the lib service.
	 */
	public static IFuture<Void>	removeURL(IExternalAccess access, final String path)
	{
		return access.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("removeURL")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final Future<Void>	ret	= new Future<Void>();
				ia.getServiceContainer().searchService(ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new ExceptionDelegationResultListener<ILibraryService, Void>(ret)
				{
					public void customResultAvailable(ILibraryService ls)
					{
						try
						{
							ls.removeURL(null, SUtil.toURL(path));
							ret.setResult(null);
						}
						catch(Exception ex)
						{
							ret.setException(ex);
						}
					}
				});
				return ret;
			}
		});
	}
	
	/**
	 *  Find an entry in a list of URLs.
	 *  Tests canonical paths on the remote system.
	 */
	public static IFuture<Integer>	findChild(IExternalAccess access, final String toremove, final List<String> filenames)
	{
		return access.scheduleStep(new IComponentStep<Integer>()
		{
			@Classname("findchild")
			public IFuture<Integer> execute(IInternalAccess ia)
			{
				int ret = SUtil.indexOfFilename(toremove, filenames);
				return new Future<Integer>(new Integer(ret));
			}
		});
	}
	public static void redirectInput(IExternalAccess access, final String txt)
	{
		access.scheduleImmediate(new IComponentStep<Void>()
		{
			@Classname("redir")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				SServiceProvider.getService(ia.getServiceContainer(), IDaemonThreadPoolService.class)
					.addResultListener(ia.createResultListener(new IResultListener<IDaemonThreadPoolService>()
				{
					public void resultAvailable(IDaemonThreadPoolService tp)
					{
						proceed(tp);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						proceed(null);
					}
					
					protected void proceed(IThreadPool tp)
					{
						try
						{
							SUtil.getOutForSystemIn(tp).write(txt.getBytes());
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}));
				return IFuture.DONE;
			}
		});		
	}
	
	public static void addConsoleListener(IExternalAccess platformaccess, final String id, final IRemoteChangeListener rcl)
	{
		platformaccess.scheduleImmediate(new IComponentStep<Void>()
		{
			@Classname("installListener")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				ConsoleListener	cl	= new ConsoleListener(id, ia, rcl);
				SUtil.addSystemOutListener(cl);
				SUtil.addSystemErrListener(cl);
				return IFuture.DONE;
			}
		});
	}
	
	public static void removeConsoleListener(IExternalAccess platformaccess, final String id)
	{
		platformaccess.scheduleImmediate(new IComponentStep<Void>()
		{
			@Classname("removeListener")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				ConsoleListener	cl	= new ConsoleListener(id, ia, null);
				SUtil.removeSystemOutListener(cl);
				SUtil.removeSystemErrListener(cl);
				return IFuture.DONE;
			}
		});
	}

	//-------- helper classes --------
	
	public static class	ConsoleListener	extends RemoteChangeListenerHandler	implements IChangeListener
	{
		//-------- constants --------
		
		/** The limit of characters sent in one event. */
		public static final int LIMIT	= 1;//4096;
		
		//-------- constructors --------
		
		/**
		 *  Create a console listener.
		 */
		public ConsoleListener(String id, IInternalAccess instance, IRemoteChangeListener rcl)
		{
			super(id, instance, rcl);
		}
		
		//-------- IChangeListener interface --------
		
		/**
		 *  Called when a change occurs.
		 *  @param event The event.
		 */
		public void changeOccurred(final ChangeEvent event)
		{
			instance.getExternalAccess().scheduleImmediate(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					// Merge new output with last output, if not yet sent.
					boolean	merged	= false;
					ArrayList	list	= (ArrayList)occurred.get(event.getType()); 
					if(list!=null && !list.isEmpty())
					{
						String	val	= (String)list.get(list.size()-1);
						if(val.length()<LIMIT)
						{
							val	+= "\n"+event.getValue();
							list.set(list.size()-1, val);
							merged	= true;
						}
					}
					
					if(!merged)
						occurrenceAppeared(event.getType(), event.getValue());
					
					return IFuture.DONE;
				}
			});
		}
		
		//-------- RemoteChangeListenerHandler methods --------
		
		/**
		 *  Remove local listeners.
		 */
		protected void dispose()
		{
			super.dispose();
			
			SUtil.removeSystemOutListener(this);
			SUtil.removeSystemErrListener(this);
		}
	}
}
