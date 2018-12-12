package jadex.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;

import jadex.base.test.Testcase;
import jadex.bridge.GlobalResourceIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ImmediateComponentStep;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.RemoteChangeListenerHandler;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IInternalRequiredServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.bridge.service.types.filetransfer.BunchFileData;
import jadex.bridge.service.types.filetransfer.FileData;
import jadex.bridge.service.types.filetransfer.IFileTransferService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.remote.ServiceOutputConnection;
import jadex.commons.ChangeEvent;
import jadex.commons.IAsyncFilter;
import jadex.commons.IChangeListener;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.collection.MultiCollection;
import jadex.commons.collection.SCollection;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.transformation.annotations.Classname;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;

/**
 *  Helper class for GUI code to be executed on remote
 *  devices (e.g. android.).
 */
public class SRemoteGui
{
	//-------- methods --------
	
	/**
	 *  Check if a component is necessary.
	 *  @param target The target component identifier.
	 *  @return The 
	 */
	public static boolean isComponentStepNecessary(IComponentIdentifier target)
	{
//		return true;
		IComponentIdentifier cid = IComponentIdentifier.LOCAL.get();
		return cid==null? true: !cid.equals(target);
	}
	
	/**
	 *  Get the service infos for a component.
	 *  @param ea	The component access.
	 *  @return	The provided and required service infos.
	 */
	public static IFuture<Object[]>	getServiceInfos(IExternalAccess ea)
	{
//		if(ea==null)
//		{
//			System.err.println("ea is null in remote gui!!!");
//		}
		return ea.scheduleStep(new ImmediateComponentStep<Object[]>()
		{
			@Classname("getServiceInfos")
			public IFuture<Object[]> execute(final IInternalAccess ia)
			{
				final Future<Object[]>	ret	= new Future<Object[]>();
				try
				{
					final RequiredServiceInfo[]	ris	= ia.getFeature0(IRequiredServicesFeature.class)==null? null: ((IInternalRequiredServicesFeature)ia.getFeature(IRequiredServicesFeature.class)).getServiceInfos();
					ProvidedServiceInfo[]	pis	= null;
					IServiceIdentifier[]	sis	= null;
					
					ServiceQuery<IService>	query	= new ServiceQuery<IService>((Class<IService>)null).setProvider(ia.getId());
					Collection<IService>	result	= ia.getFeature0(IRequiredServicesFeature.class)==null? null: (ia.getFeature(IRequiredServicesFeature.class)).searchLocalServices(query);
					if(result!=null)
					{
						pis	= new ProvidedServiceInfo[result.size()];
						sis	= new IServiceIdentifier[result.size()];

						Iterator<IService>	it	= result.iterator();
						for(int i=0; i<pis.length; i++)
						{
							IService	service	= it.next();
							// todo: implementation?
							sis[i] = service.getServiceId();
							pis[i]	= new ProvidedServiceInfo(service.getServiceId().getServiceName(), 
//								service.getId().getServiceType(), null, null);
								sis[i].getServiceType().getType(ia.getClassLoader(), ia.getModel().getAllImports()), null, sis[i].getScope(), null, null);
						}
						
						ret.setResult(new Object[]{pis, ris, sis});
					}
				}
				catch(Exception e)
				{
					// Protect remote platform from execution errors.
//					Thread.dumpStack();
//					e.printStackTrace();
					ret.setException(e);
				}
				return ret;
			}
		});		
	}
	
//	/**
//	 *  Remove a service.
//	 *  @param cms	The cms
//	 *  @param container	The service provider.
//	 *  @param sid	The service to remove.
//	 */
//	@Deprecated
//	public static IFuture<Void> removeService(IComponentManagementService cms,
//		IExternalAccess container, final IServiceIdentifier sid)
//	{
////		final Future<Void>	ret	= new Future<Void>();
////		cms.getExternalAccess(sid.getProviderId())
////			.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
////		{
////			public void customResultAvailable(IExternalAccess exta)
////			{
////				exta.scheduleStep(new IComponentStep<Void>()
////				{
////					@Classname("removeService")
////					public IFuture<Void> execute(IInternalAccess ia)
////					{
////						ia.getServiceContainer().removeService(sid);
////						return IFuture.DONE;
////					}
////				})
////				.addResultListener(new DelegationResultListener<Void>(ret));
////			}
////		});
////		return ret;
//		
//		throw new UnsupportedOperationException();
//	}
	
//	/**
//	 *  Install the remote listener.
//	 *  @param cid	The remote component id.
//	 */
//	public static IFuture<Void>	installRemoteCMSListener(final IExternalAccess access, final IComponentIdentifier cid, final IRemoteChangeListener rcl0, final String id0)
//	{
//		final Future<Void>	ret	= new Future<Void>();
//		
//		try
//		{
//			access.searchService( new ServiceQuery<>( IComponentManagementService.class, ServiceScope.PLATFORM))
//				.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
//			{
//				public void customResultAvailable(IComponentManagementService	cms)
//				{
//	//				IComponentManagementService	cms	= (IComponentManagementService)result;
//					cms.getExternalAccess(cid).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
//					{
//						public void customResultAvailable(IExternalAccess exta)
//						{
//	//						IExternalAccess	exta	= (IExternalAccess)result;
//							final IComponentIdentifier	icid	= cid;	// internal reference to cid, because java compiler stores final references in outmost object (grrr.)
//							final String	id	= id0;
//							final IRemoteChangeListener	rcl	= rcl0;
//							exta.scheduleStep(new IComponentStep<Void>()
//							{
//								@Classname("installListener")
//								public IFuture<Void> execute(IInternalAccess ia)
//								{
//									final Future<Void>	ret	= new Future<Void>();
//									try
//									{
//										ia.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( IComponentManagementService.class, ServiceScope.PLATFORM))
//											.addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
//										{
//											public void customResultAvailable(IComponentManagementService cms)
//											{
//												RemoteCMSListener	rcmsl	= new RemoteCMSListener(icid, id, cms, rcl);
//												cms.addComponentListener(null, rcmsl);
//												ret.setResult(null);
//											}
//										}));
//									}
//									catch(Exception e)
//									{
//										// Protect remote platform from execution errors.
//										Thread.dumpStack();
//										e.printStackTrace();
//										ret.setException(e);
//									}
//									return ret;
//								}
//							}).addResultListener(new DelegationResultListener<Void>(ret));
//						}
//					});
//				}
//			});
//		}
//		catch(Exception e)
//		{
//			ret.setException(e);
//		}
//		
//		return ret;
//	}
//
//	/**
//	 *  Deregister the remote listener.
//	 */
//	public static IFuture<Void>	deregisterRemoteCMSListener(final IExternalAccess access, final IComponentIdentifier cid, final String id0)
//	{
//		final Future<Void>	ret	= new Future<Void>();
//		access.searchService( new ServiceQuery<>( IComponentManagementService.class, ServiceScope.PLATFORM))
//			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
//		{
//			public void customResultAvailable(IComponentManagementService cms)
//			{
//				cms.getExternalAccess(cid).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
//				{
//					public void customResultAvailable(IExternalAccess exta)
//					{
//						final String	id	= id0;
//						exta.scheduleStep(new IComponentStep<Void>()
//						{
//							@Classname("deregisterListener")
//							public IFuture<Void> execute(IInternalAccess ia)
//							{
//								final Future<Void>	ret	= new Future<Void>();
//								try
//								{
//									ia.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( IComponentManagementService.class, ServiceScope.PLATFORM))
//										.addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
//									{
//										public void customResultAvailable(IComponentManagementService cms)
//										{
//	//										System.out.println("Removing listener: "+id);
//											try
//											{
//												if(cms!=null)
//												{
//													cms.removeComponentListener(null, new RemoteCMSListener(cid, id, cms, null));
//												}
//											}
//											catch(RuntimeException e)
//											{
//			//									System.out.println("Listener already removed: "+id);
//											}
//											ret.setResult(null);
//										}
//									}));
//								}
//								catch(Exception e)
//								{
//									// Protect remote platform from execution errors.
//									Thread.dumpStack();
//									e.printStackTrace();
//									ret.setException(e);
//								}
//								return ret;
//							}
//						}).addResultListener(new DelegationResultListener<Void>(ret));
//					}
//				});
//			}
//		});
//		return ret;
//	}
	
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
				try
				{
					// Test, if model can be loaded.
					SComponentFactory.loadModel(ia.getExternalAccess(), name, rid)
						.addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IModelInfo, Tuple2<String, String>>(ret)
					{
						public void customResultAvailable(IModelInfo result)
						{
							String	model	= SUtil.convertPathToRelative(name);
							String	ridurl	= SUtil.convertPathToRelative(rid.getLocalIdentifier().getUri().toString());
							ret.setResult(new Tuple2<String, String>(model, ridurl));
						}
						public void exceptionOccurred(Exception exception)
						{
							ret.setResult(null);
						}
					}));
				}
				catch(Exception e)
				{
					// Protect remote platform from execution errors.
					Thread.dumpStack();
					e.printStackTrace();
					ret.setException(e);
				}
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
				try
				{
					// What to do if ridurl is null, use library service?
					if(ridurl==null && globalrid==null)
					{
						ret.setResult(ia.getModel().getResourceIdentifier());
					}
					else
					{
						URL	url	= SUtil.toURL(ridurl);
						LocalResourceIdentifier lid = url==null? null: new LocalResourceIdentifier(ia.getId().getRoot(), url);
						ret.setResult(new ResourceIdentifier(lid, globalrid!=null? new GlobalResourceIdentifier(globalrid, null, null): null));
					}
				}
				catch(Exception e)
				{
					// Protect remote platform from execution errors.
					Thread.dumpStack();
					e.printStackTrace();
					ret.setException(e);
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
				try
				{
					return new Future<FileData>(new FileData(new File(SUtil.convertPathToRelative(path))));
				}
				catch(Exception e)
				{
					// Protect remote platform from execution errors.
					Thread.dumpStack();
					e.printStackTrace();
					return new Future<FileData>(e);
				}

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
				final Future<Tuple2<URL, IResourceIdentifier>>	ret	= new Future<Tuple2<URL, IResourceIdentifier>>();
				try
				{
					final URL	url	= SUtil.toURL(filename);
					ILibraryService	ls	= ia.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ILibraryService.class));
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
										try
										{
											URL u1 = rid.getLocalIdentifier().getUri().toURL();
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
										catch(Exception e)
										{
											System.out.println("URL problem: "+rid.getLocalIdentifier());
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
				catch(Exception e)
				{
					// Protect remote platform from execution errors.
					Thread.dumpStack();
					e.printStackTrace();
					ret.setException(e);
				}
				
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
				try
				{
					ILibraryService	ls	= ia.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ILibraryService.class));
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
				catch(Exception e)
				{
					// Protect remote platform from execution errors.
					Thread.dumpStack();
					e.printStackTrace();
					ret.setException(e);
				}
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
				try
				{
					int ret = SUtil.indexOfFilename(toremove, filenames);
					return new Future<Integer>(Integer.valueOf(ret));
				}
				catch(Exception e)
				{
					// Protect remote platform from execution errors.
					Thread.dumpStack();
					e.printStackTrace();
					return new Future<Integer>(e);
				}
			}
		});
	}
	
	/**
	 *  Check remote files for existence.
	 *  @param files	The files to check.
	 *  @return All checked files that exist.
	 */
	public static	IIntermediateFuture<FileData>	checkExistence(final String[] files, IExternalAccess exta)
	{
		return (IIntermediateFuture<FileData>)exta.scheduleStep(new IComponentStep<Collection<FileData>>()
		{
			@Classname("checkExistence")
			public IIntermediateFuture<FileData> execute(IInternalAccess ia)
			{
				IntermediateFuture<FileData>	ret	= new IntermediateFuture<FileData>();
				for(int i=0; i<files.length; i++)
				{
					try
					{
						File f = new File(files[i]);
						if(f.exists())
						{
							ret.addIntermediateResult(new FileData(new File(files[i]).getAbsoluteFile()));
						}
					}
					catch(Exception e)
					{
						// Protect remote platform from execution errors.
						System.err.println("File: "+files[i]);
						Thread.dumpStack();
						e.printStackTrace();
					}
				}
					
				return ret;
			}
		});
	}
	
	/**
	 *  Convert the given paths to relative paths.
	 *  @param paths	The paths
	 * 	@return	The relative paths.
	 */
	public static IIntermediateFuture<String>	convertPathsToRelative(final String[] paths, IExternalAccess exta)
	{
		Object ret = exta.scheduleStep(new IComponentStep<Collection<String>>()
		{
			@Classname("convertPathToRelative")
			public IIntermediateFuture<String> execute(IInternalAccess ia)
			{
				IntermediateFuture<String>	ret	= new IntermediateFuture<String>();
				try
				{
					for(String path: paths)
					{
						ret.addIntermediateResult(SUtil.convertPathToRelative(path));
					}
					ret.setFinished();
				}
				catch(Exception e)
				{
					// Protect remote platform from execution errors.
					Thread.dumpStack();
					e.printStackTrace();
					ret.setException(e);
				}

				return ret;
			}
		});
		
		if(!(ret instanceof IIntermediateFuture))
			System.out.println("hrrrrrr");
		
		return (IIntermediateFuture<String>)ret;
	}
	
	/**
	 *  List files in a directory matching a filter (if any).
	 *  @param dir	The directory.
	 *  @param filter	The filter or null for all files.
	 */
	public static IIntermediateFuture<FileData>	listFiles(final FileData dir, final IAsyncFilter filter, IExternalAccess exta)
	{
		IIntermediateFuture<FileData> ret = null;
		if(!isComponentStepNecessary(exta.getId()))
		{
//			System.out.println("direct listFiles");
			ret = listFiles(dir, filter);
		}
		else
		{
//			System.out.println("stepped listFiles");
			ret = (IIntermediateFuture<FileData>)exta.scheduleStep(new IComponentStep<Collection<FileData>>()
			{
				@Classname("listFiles")
				public IIntermediateFuture<FileData> execute(IInternalAccess ia)
				{
					return  listFiles(dir, filter);
				}
				
				// For debugging intermediate future bug. Used in MicroAgentInterpreter
//				public String toString()
//				{
//					return "ListFiles("+dir+")";
//				}
			});
		}
		return ret;
	}
	
	/**
	 *  List files in a directory matching a filter (if any).
	 *  @param dir	The directory.
	 *  @param filter	The filter or null for all files.
	 */
	public static IIntermediateFuture<FileData>	listFiles(final FileData dir, final IAsyncFilter filter)
	{
//		return (IIntermediateFuture<FileData>)exta.scheduleStep(new IComponentStep<Collection<FileData>>()
//		{
//			@Classname("listFiles")
//			public IIntermediateFuture<FileData> execute(IInternalAccess ia)
//			{
				IntermediateFuture<FileData>	ret	= new IntermediateFuture<FileData>();
				try
				{
					File f = new File(dir.getPath());
					final File[] files = f.listFiles();
					if(files!=null)
					{
						final CollectionResultListener<FileData> lis = new CollectionResultListener<FileData>(files.length, true, new DelegationResultListener<Collection<FileData>>(ret));
						for(final File file: files)
						{
							if(filter==null)
							{
								lis.resultAvailable(new FileData(file));
							}
							else
							{
								filter.filter(file).addResultListener(new IResultListener<Boolean>()
								{
									public void resultAvailable(Boolean result)
									{
										if(result.booleanValue())
										{
											lis.resultAvailable(new FileData(file));
										}
										else
										{
											lis.exceptionOccurred(null);
										}
									}
									
									public void exceptionOccurred(Exception exception)
									{
										lis.exceptionOccurred(null);
									}
								});
							}
						}
					}
					else
					{
						ret.setResult(null);
					}
				}
				catch(Exception e)
				{
					// Protect remote platform from execution errors.
					Thread.dumpStack();
					e.printStackTrace();
					ret.setException(e);
				}

				return ret;
//			}
//			
//			// For debugging intermediate future bug. Used in MicroAgentInterpreter
//			public String toString()
//			{
//				return "ListFiles("+dir+")";
//			}
//		});
	}
	
//	/**
//	 *  List files in a directory matching a filter (if any).
//	 *  @param dir	The directory.
//	 *  @param filter	The filter or null for all files.
//	 */
//	public static IFuture<Collection<FileData>>	listFiles(final FileData dir, final IRemoteFilter filter, IExternalAccess exta)
//	{
//		return exta.scheduleStep(new IComponentStep<Collection<FileData>>()
//		{
//			@Classname("listFiles")
//			public IFuture<Collection<FileData>> execute(IInternalAccess ia)
//			{
//				Future<Collection<FileData>>	ret	= new Future<Collection<FileData>>();
//				try
//				{
//					File f = new File(dir.getPath());
//					final File[] files = f.listFiles();
//					if(files!=null)
//					{
//						final CollectionResultListener<FileData> lis = new CollectionResultListener<FileData>(files.length, true, new DelegationResultListener<Collection<FileData>>(ret));
//						for(final File file: files)
//						{
//							if(filter==null)
//							{
//								lis.resultAvailable(new FileData(file));
//							}
//							else
//							{
//								filter.filter(file).addResultListener(new IResultListener<Boolean>()
//								{
//									public void resultAvailable(Boolean result)
//									{
//										if(result.booleanValue())
//										{
//											lis.resultAvailable(new FileData(file));
//										}
//										else
//										{
//											lis.exceptionOccurred(null);
//										}
//									}
//									
//									public void exceptionOccurred(Exception exception)
//									{
//										lis.exceptionOccurred(null);
//									}
//								});
//							}
//						}
//					}
//					else
//					{
//						ret.setResult(null);
//					}
//				}
//				catch(Exception e)
//				{
//					// Protect remote platform from execution errors.
//					Thread.dumpStack();
//					e.printStackTrace();
//					ret.setException(e);
//				}
//
//				return ret;
//			}
//			
//			// For debugging intermediate future bug. Used in MicroAgentInterpreter
//			public String toString()
//			{
//				return "ListFiles("+dir+")";
//			}
//		});
//	}

	/**
	 *  List files in a directory matching a filter (if any).
	 *  @param dir	The directory.
	 *  @param filter	The filter or null for all files.
	 */
	public static ISubscriptionIntermediateFuture<FileData>	listJarFileEntries(final FileData file, final IAsyncFilter filter, IExternalAccess exta)
	{
		ISubscriptionIntermediateFuture<FileData> ret = null;
		if(!isComponentStepNecessary(exta.getId()))
		{
//			System.out.println("direct listJarFileEntries");
			ret = listJarFileEntries(file, filter);
		}
		else
		{
//			System.out.println("stepped listJarFileEntries");
			ret = (ISubscriptionIntermediateFuture<FileData>)exta.scheduleStep(new IComponentStep<Collection<FileData>>()
			{
				@Classname("listJarFileEntries")
				public ISubscriptionIntermediateFuture<FileData> execute(IInternalAccess ia)
				{
					return  listJarFileEntries(file, filter);
				}
				
//				// For debugging intermediate future bug. Used in MicroAgentInterpreter
//				public String toString()
//				{
//					return "ListJarFileEntries("+file+")";
//				}
			});
		}
		return ret;
	}
	
	/**
	 *	List files of a remote jar file
	 */
	public static ISubscriptionIntermediateFuture<FileData>	listJarFileEntries(final FileData file, final IAsyncFilter filter)
	{
		final long start = System.currentTimeMillis();
		
//		IntermediateFuture<FileData> ret = new IntermediateFuture<FileData>();
//		exta.scheduleStep(new IComponentStep<Collection<FileData>>()
//		{
//			@Classname("listJarFileEntries")
//			public IIntermediateFuture<FileData> execute(IInternalAccess ia)
//			{
				final SubscriptionIntermediateFuture<FileData> ret = new SubscriptionIntermediateFuture<FileData>();
				try
				{
					final String name = file instanceof RemoteJarFile? ((RemoteJarFile)file).getRelativePath(): null;
					final boolean dir = file instanceof RemoteJarFile? ((RemoteJarFile)file).isDirectory(): false;

					final JarAsDirectory jad = name!=null? new JarAsDirectory(file.getPath(), name, dir, true): new JarAsDirectory(file.getPath());
					jad.refresh();
									
//					final Map<String, Collection<FileData>> rjfentries = new LinkedHashMap<String, Collection<FileData>>();
					MultiCollection zipentries = jad.createEntries();
					
//					final int size = zipentries.size();
					
					final List<Tuple2<String, RemoteJarFile>> ires = new ArrayList<Tuple2<String, RemoteJarFile>>(); 
//					final List<RemoteJarFile> ires = new ArrayList<RemoteJarFile>(); 
					
					final CounterResultListener<Tuple2<String, RemoteJarFile>> lis = new CounterResultListener<Tuple2<String, RemoteJarFile>>(-1, 
						true, new ExceptionDelegationResultListener<Void, Collection<FileData>>(ret)
					{
						public void customResultAvailable(Void result)
						{
							long dur = System.currentTimeMillis()-start;
//							System.out.println("Needed for listJarFileEntries: "+dur/1000);
							
//							for(Tuple2<String, RemoteJarFile> tmp: ires)
//							{
//								Collection<FileData> dir = rjfentries.get(tmp.getFirstEntity());
//								if(dir==null)
//								{
//									dir	= new ArrayList<FileData>();
//									rjfentries.put(tmp.getFirstEntity(), dir);
//								}
//								dir.add(tmp.getSecondEntity());
//							}
//							
//							RemoteJarFile rjf = new RemoteJarFile(jad.getName(), jad.getAbsolutePath(), true, 
//								FileData.getDisplayName(jad), rjfentries, "/", jad.getLastModified(), File.separatorChar, SUtil.getPrefixLength(jad), jad.length());
//							Collection<FileData> files = rjf.listFiles();
//							System.out.println("size is: "+files.size());
							
							BunchFileData dat = new BunchFileData(ires);
//							BunchFileData dat = new BunchFileData((Collection)ires);
							ret.addIntermediateResult(dat);
							ret.setFinished();
						}
						
					})//);
					{
						public void resultAvailable(Tuple2<String, RemoteJarFile> result)
						{	
							ires.add(result);
							if(ires.size()%500==0)
							{
								System.out.println("sending: "+ires.size());
								BunchFileData dat = new BunchFileData((Collection)ires);
								ret.addIntermediateResult(dat);
								ires.clear();
							}
							super.resultAvailable(result);
						}
						
//						public void customExceptionOccurred(Exception exception) 
//						{
//							if(cnt%1000==0)
//								System.out.println("cnt: "+cnt+"/"+size);
//							cnt++;
//							super.exceptionOccurred(exception);
//						}
					};
	
//						for(Iterator<?> it=zipentries.keySet().iterator(); it.hasNext(); )
//						{
//						final String name = "/";//(String)it.next();
						Collection<?> childs = (Collection<?>)zipentries.get(name==null? "/": name);
						lis.setNumber(childs==null? 0: childs.size());
//						System.out.println("childs: "+childs);
						
						if(childs!=null)
						{
							for(Iterator<?> it2=childs.iterator(); it2.hasNext(); )
							{
								ZipEntry entry = (ZipEntry)it2.next();
								String ename = entry.getName();
	//							System.out.println("eq: "+name+" "+ename);
								int	slash = ename.lastIndexOf("/", ename.length()-2);
								ename = ename.substring(slash!=-1? slash+1: 0, ename.endsWith("/")? ename.length()-1: ename.length());
								
	//							System.out.println("ename: "+ename+" "+entry.getName()+" "+(cnt++)+"/"+size);
								
	//							final RemoteJarFile tmp = new RemoteJarFile(ename, "jar:file:"+jad.getJarPath()+"!/"+entry.getName(), 
	//								entry.isDirectory(), ename, rjfentries, entry.getName(), entry.getTime(), File.separatorChar, SUtil.getPrefixLength(jad), jad.length());
//								String path = "jar:file:"+jad.getJarPath()+"!/"+entry.getName();
//								path = jarifyPath(jad.getJarPath(), null);
//								path=path+entry.getName();
								
//								System.out.println("name is: "+path);
								
								final RemoteJarFile tmp = new RemoteJarFile(ename, jad.getJarPath(), 
									entry.isDirectory(), ename, null, entry.getName(), entry.getTime(), File.separatorChar, SUtil.getPrefixLength(jad), jad.length());
								
//								System.out.println("entry: "+tmp.getFilename()+" "+tmp.isDirectory());
								
								if(filter!=null)
								{
									filter.filter(jad.getFile(entry.getName())).addResultListener(new IResultListener<Boolean>()
									{
										public void resultAvailable(Boolean result)
										{
											if(result.booleanValue())
											{
												lis.resultAvailable(new Tuple2<String, RemoteJarFile>(name, tmp));
											}
											else
											{
												lis.exceptionOccurred(null);
											}
										}
										
										public void exceptionOccurred(Exception exception)
										{
											lis.exceptionOccurred(null);
										}
									});
								}
								else
								{
									lis.resultAvailable(new Tuple2<String, RemoteJarFile>(name, tmp));							
								}
								
	//							break;
							}
						}
//						break;
//					}
				}
				catch(Exception e)
				{
					// Protect remote platform from execution errors.
					Thread.dumpStack();
					e.printStackTrace();
					ret.setExceptionIfUndone(e);
				}
					
				return ret;
//			}
//		}).addResultListener(new DelegationResultListener<Collection<FileData>>(ret)
//		{
//			public void customResultAvailable(Collection<FileData> result)
//			{
//				System.out.println("fini: "+result.size());
//				super.customResultAvailable(result);
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				exception.printStackTrace();
//				super.exceptionOccurred(exception);
//			}
//		});
//		
//		return ret;
	}
	
	
	
	/**
	 *  Check if a component model can be started as test case.
	 */
	public static IFuture<Boolean>	isTestcase(final String model, IExternalAccess access, final IResourceIdentifier rid)
	{
		return access.scheduleStep(new ImmediateComponentStep<Boolean>()
		{
			@Classname("isTestcase")
			public IFuture<Boolean> execute(final IInternalAccess ia)
			{
				final Future<Boolean>	ret	= new Future<Boolean>();
				try
				{
					final IExternalAccess access	= ia.getExternalAccess();
					SComponentFactory.isLoadable(access, model, rid)
						.addResultListener(new DelegationResultListener<Boolean>(ret)
					{
						public void customResultAvailable(Boolean result)
						{
							if(result.booleanValue())
							{
								SComponentFactory.isStartable(access, model, rid)
									.addResultListener(new DelegationResultListener<Boolean>(ret)
								{
									public void customResultAvailable(Boolean result)
									{
										if(result.booleanValue())
										{
											SComponentFactory.loadModel(access, model, rid)
												.addResultListener(new ExceptionDelegationResultListener<IModelInfo, Boolean>(ret)
											{
												public void customResultAvailable(final IModelInfo model)
												{
													if(model!=null && model.getReport()==null)
													{
														ILibraryService	ls	= ia.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ILibraryService.class));
														ls.getClassLoader(model.getResourceIdentifier())
															.addResultListener(new ExceptionDelegationResultListener<ClassLoader, Boolean>(ret)
														{
															public void customResultAvailable(ClassLoader cl)
															{
																IArgument[]	results	= model.getResults();
																boolean	istest	= false;
																for(int i=0; !istest && i<results.length; i++)
																{
//																			if(results[i].getName().equals("testresults") && results[i].getClazz()!=null
//																				&& "jadex.base.test.Testcase".equals(results[i].getClazz().getTypeName()))
//																			{	
//																				istest	= true;
//																			}
																	if(results[i].getName().equals("testresults") && results[i].getClazz()!=null
																		&& Testcase.class.equals(results[i].getClazz().getType(cl, model.getAllImports())))
																	{
																		istest	= true;
																	}
																}
																ret.setResult(istest? Boolean.TRUE: Boolean.FALSE);
															}
														});
													}
													else
													{
														ret.setResult(Boolean.FALSE);
													}
												}
											});
										}
										else
										{
											ret.setResult(Boolean.FALSE);
										}
									}
								});
							}
							else
							{
								ret.setResult(Boolean.FALSE);
							}
						}
					});
				}
				catch(Exception e)
				{
					// Protect remote platform from execution errors.
					Thread.dumpStack();
					e.printStackTrace();
					ret.setException(e);
				}
				return ret;
			}
		});
	}
	
	public static IFuture<Map<String, Object>>	parseArgs(final Map<String, String> rawargs, final IResourceIdentifier modelrid, IExternalAccess exta)
	{
		return exta.scheduleStep(new IComponentStep<Map<String, Object>>()
		{
			@Classname("parseArgs")
			public IFuture<Map<String, Object>> execute(IInternalAccess ia)
			{
//				System.out.println("b: "+ia.getComponentIdentifier().getName());
				final Future<Map<String, Object>> ret = new Future<Map<String, Object>>();
				try
				{
					ILibraryService	ls	= ia.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ILibraryService.class));
					ls.getClassLoader(modelrid).addResultListener(new ExceptionDelegationResultListener<ClassLoader, Map<String, Object>>(ret)
					{
						public void customResultAvailable(ClassLoader cl)
						{
							Map<String, Object> args = SCollection.createHashMap();
							String errortext = null;
							for(String argname: rawargs.keySet())
							{
								String argval = rawargs.get(argname);
								if(argval.length()>0)
								{
									Object arg = null;
									try
									{
										arg = new JavaCCExpressionParser().parseExpression(argval, null, null, cl).getValue(null);
									}
									catch(Exception e)
									{
										if(errortext==null)
											errortext = "Error within argument expressions:\n";
										errortext += argname+" "+e.getMessage()+"\n";
									}
									args.put(argname, arg);
									
								}
							}
							if(errortext==null)
							{
								ret.setResult(args);
							}
							else
							{
								ret.setException(new RuntimeException(errortext));
							}
						}
					});
				}
				catch(Exception e)
				{
					// Protect remote platform from execution errors.
					Thread.dumpStack();
					e.printStackTrace();
					ret.setException(e);
				}
				return ret;
			}
		});
	}
	
	/**
	 *  Add a remote url via the library service.
	 *  Needs to schedule on target platform to recreate url.
	 */
	public static IFuture<Tuple2<URL, IResourceIdentifier>> addRemoteURL(final IResourceIdentifier parid, final String filename, final boolean tl, IExternalAccess exta)
	{
		return exta.scheduleStep(new IComponentStep<Tuple2<URL, IResourceIdentifier>>()
		{
			@Classname("addurl")
			public IFuture<Tuple2<URL, IResourceIdentifier>> execute(IInternalAccess ia)
			{
				final Future<Tuple2<URL, IResourceIdentifier>>	ret	= new Future<Tuple2<URL, IResourceIdentifier>>();
				try
				{
					final URL	url	= SUtil.toURL(filename);
					ILibraryService	ls	= ia.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ILibraryService.class));
					if(!tl)
					{
						// todo: workspace=true?
						ls.addURL(parid, url).addResultListener(new ExceptionDelegationResultListener<IResourceIdentifier, Tuple2<URL, IResourceIdentifier>>(ret)
						{
							public void customResultAvailable(IResourceIdentifier rid)
							{
								ret.setResult(new Tuple2<URL, IResourceIdentifier>(url, rid));
							}
							public void exceptionOccurred(Exception exception)
							{
//									exception.printStackTrace();
								super.exceptionOccurred(exception);
							}
						});
					}
					else
					{
						ls.addTopLevelURL(url).addResultListener(new ExceptionDelegationResultListener<Void, Tuple2<URL, IResourceIdentifier>>(ret)
						{
							public void customResultAvailable(Void result)
							{
								ret.setResult(new Tuple2<URL, IResourceIdentifier>(url, null));
							}
							public void exceptionOccurred(Exception exception)
							{
//									exception.printStackTrace();
								super.exceptionOccurred(exception);
							}
						});
					}
				}
				catch(Exception e)
				{
					// Protect remote platform from execution errors.
					Thread.dumpStack();
					e.printStackTrace();
					ret.setException(e);
				}
				
				return ret;
			}
		});
	}
	
	/**
	 *  Copy a file between two platforms.
	 *  Intermediate results represent status messages.
	 */
	public static IIntermediateFuture<String> copy(final String source, final IExternalAccess sourceaccess, final String target, final IFileTransferService targetds) 
	{
		return (IIntermediateFuture<String>)sourceaccess.scheduleStep(new IComponentStep<Collection<String>>()
		{
			@Classname("copyFromSource")
			public IIntermediateFuture<String> execute(final IInternalAccess ia)
			{
				final IntermediateFuture<String> ret = new IntermediateFuture<String>();
				try
				{
					try
					{
						final File	sourcefile	= new File(source);
						FileInputStream fis = new FileInputStream(sourcefile);
						ServiceOutputConnection soc = new ServiceOutputConnection();
						soc.writeFromInputStream(fis, sourceaccess);
						
						ITerminableIntermediateFuture<Long> fut = targetds.uploadFile(soc.getInputConnection(), target, sourcefile.getName());
						fut.addResultListener(new IIntermediateResultListener<Long>()
						{
							long	lasttime	= System.currentTimeMillis();
							public void intermediateResultAvailable(final Long result)
							{
								long	curtime	= System.currentTimeMillis();
								if(curtime-lasttime>1000)
								{
									lasttime	= curtime;
									double done = ((int)((result/(double)sourcefile.length())*10000))/100.0;
									DecimalFormat fm = new DecimalFormat("#0.00");
									String txt = "Copy "+fm.format(done)+"% done ("+SUtil.bytesToString(result)+" / "+SUtil.bytesToString(sourcefile.length())+")";
									ret.addIntermediateResult(txt);
								}
							}
							
							public void finished()
							{
								ret.setFinished();
							}
							
							public void resultAvailable(Collection<Long> result)
							{
								finished();
							}
							
							public void exceptionOccurred(final Exception exception)
							{
								ret.setException(exception);
							}
						});
					}
					catch(Exception ex)
					{
						ret.setException(ex);
					}
				}
				catch(Exception e)
				{
					// Protect remote platform from execution errors.
					Thread.dumpStack();
					e.printStackTrace();
					ret.setException(e);
				}
				
				return ret;
			}
		});
	}

	
	/**
	 *  Compare a model to a path.
	 */
	public static IFuture<Boolean>	matchModel(final String path, final String model, IExternalAccess exta)
	{
		return exta.scheduleStep(new ImmediateComponentStep<Boolean>()
		{
			@Classname("matchModel")
			public IFuture<Boolean> execute(IInternalAccess ia)
			{
				boolean	match	= false;
				try
				{
					File	pathfile	= SUtil.urlToFile(path);
					File	modelfile	= SUtil.urlToFile(model);
					match	= pathfile!=null && modelfile!=null && modelfile.getCanonicalPath().startsWith(pathfile.getCanonicalPath());
				}
				catch(IOException e)
				{
				}
				return new Future<Boolean>(Boolean.valueOf(match));
			}
		});
	}
	
	/**
	 *  Log a warning on a component.
	 */
	public static IFuture<Void>	logWarning(final String msg, IExternalAccess exta)
	{
		return exta.scheduleStep(new ImmediateComponentStep<Void>()
		{
			@Classname("logWarning")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				ia.getLogger().warning(msg);
				return IFuture.DONE;
			}
		});
	}

	/**
	 *  Redirect some text to the remote input stream.
	 */
	public static void redirectInput(IExternalAccess access, final String txt)
	{
		access.scheduleStep(new ImmediateComponentStep<Void>()
		{
			@Classname("redir")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				try
				{
					SUtil.getOutForSystemIn().write(txt.getBytes(Charset.defaultCharset().name()));
				}
				catch(IOException e)
				{
				}
				return IFuture.DONE;
			}
		});		
	}
	
	public static void addConsoleListener(IExternalAccess platformaccess, final String id, final IRemoteChangeListener rcl)
	{
		platformaccess.scheduleStep(new ImmediateComponentStep<Void>()
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
		platformaccess.scheduleStep(new ImmediateComponentStep<Void>()
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
			instance.getExternalAccess().scheduleStep(new ImmediateComponentStep<Void>()
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
