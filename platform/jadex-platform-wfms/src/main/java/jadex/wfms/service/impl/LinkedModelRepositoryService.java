package jadex.wfms.service.impl;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.library.ILibraryServiceListener;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.wfms.client.ClientInfo;
import jadex.wfms.client.ProcessResource;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IExecutionService;
import jadex.wfms.service.IModelRepositoryService;
import jadex.wfms.service.ProcessResourceInfo;
import jadex.wfms.service.listeners.IAuthenticationListener;
import jadex.wfms.service.listeners.IProcessRepositoryListener;
import jadex.wfms.service.listeners.ProcessRepositoryEvent;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Basic Model Repository Service implementation
 *
 */
@Service
public class LinkedModelRepositoryService implements IModelRepositoryService
{
	/** Component access. */
	@ServiceComponent
	protected IInternalAccess ia;
	
	/** The imports */
	//private Set imports;
	
	/** The process repository listeners */
	protected MultiCollection processlisteners;
	//protected Map<IComponentIdentifier, Set<IProcessRepositoryListener>> pListeners;
	
	/** URL entries */
	//protected Map urlentries;
	
	/** Model reference counter */
	//protected Map modelrefcount;
	
	/** Resource directory */
	protected File resourcedir;
	
	/** Known resources resource identifier -> process resource information*/
	protected MultiCollection resources;
	
	/** Library service listener */
	protected ILibraryServiceListener liblistener;
	
	/** Authentication listener */
	protected IAuthenticationListener authlistener;
	
	/** AAA Service */
	protected IAAAService aaaservice;
	
	/**	Resource directory files. */
	protected Map<IResourceIdentifier, File> resourcefiles;
	
	/**
	 *  Creates a new linked model repository.
	 */
	public LinkedModelRepositoryService()
	{
		//this.urlentries = new HashMap();
		//this.modelrefcount = new HashMap();
		resources = new MultiCollection();
		resourcefiles = new HashMap<IResourceIdentifier, File>();
		this.processlisteners = new MultiCollection();
	}
	
	/**
	 *  Start the service.
	 */
	@ServiceStart
	public IFuture startService()
	{
		final Future ret = new Future();
		
		ia.getServiceContainer().getRequiredService("aaa_service").addResultListener(ia.createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				aaaservice = (IAAAService) result;
				authlistener = new IAuthenticationListener()
				{
					final IExternalAccess exta = ia.getExternalAccess();
					public IFuture deauthenticated(final IComponentIdentifier client, ClientInfo info)
					{
						return exta.scheduleStep(new IComponentStep<Void>()
						{
							
							public IFuture<Void> execute(IInternalAccess ia)
							{
								processlisteners.remove(client);
								return IFuture.DONE;
							}
						});
					}
					
					public IFuture authenticated(IComponentIdentifier client, ClientInfo info)
					{
						return IFuture.DONE;
					}
				};
				
				aaaservice.addAuthenticationListener(authlistener).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						ia.getServiceContainer().getRequiredService("libservice").addResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								final IExternalAccess exta = ia.getExternalAccess();
								
								liblistener = new ILibraryServiceListener()
								{
									public IFuture<Void> resourceIdentifierRemoved(IResourceIdentifier parid, final IResourceIdentifier rid)
									{
										return exta.scheduleStep(new IComponentStep<Void>()
										{
											public IFuture<Void> execute(IInternalAccess ia)
											{
												Collection coll = (Collection) resources.remove(rid);
												if (coll != null)
												{
													for (Object obj : coll)
													{
														fireModelRemovedEvent((ProcessResourceInfo) obj);
													}
												}
												return IFuture.DONE;
											}
										});
									}
									
									public IFuture<Void> resourceIdentifierAdded(IResourceIdentifier parid, final IResourceIdentifier rid, boolean removable)
									{
										return exta.scheduleStep(new IComponentStep<Void>()
										{
											public IFuture<Void> execute(IInternalAccess ia)
											{
												URL url = rid.getLocalIdentifier().getUrl();
												File dir = new File(url.getFile());
												
												Set modelSet = new HashSet();
												if (dir.isDirectory())
												{
													modelSet = searchDirectory(dir, false);
												}
												else if (dir.getName().endsWith(".jar"))
												{
													modelSet = searchJar(dir);
												}
												
												for (Iterator it = modelSet.iterator(); it.hasNext(); )
												{
													String modelpath = (String) it.next();
													IServiceIdentifier repid = ia.getServiceContainer().getProvidedService("repository_service").getServiceIdentifier();
													IServiceIdentifier exid = ia.getServiceContainer().getProvidedService("execution_service").getServiceIdentifier();
													ProcessResourceInfo info = new ProcessResourceInfo(repid, exid, rid, modelpath);
													resources.put(rid, info);
													fireModelAddedEvent(info);
												}
												return IFuture.DONE;
											}
										});
									}
								};
								
								final ILibraryService ls = (ILibraryService) result;
								ls.addLibraryServiceListener(liblistener).addResultListener(new DelegationResultListener<Void>(ret)
								{
									public void customResultAvailable(
											Void result)
									{
										resourcedir = new File (new File(System.getProperty("user.home")).getAbsolutePath() + File.separator + ".jadex_wfms");
										if (!resourcedir.exists())
										{
											resourcedir.mkdir();
										}
										
										File[] files = resourcedir.listFiles();
										List<File> jarfiles = new ArrayList<File>();
										for (int i = 0; i < files.length; ++i)
										{
											if (files[i].isFile() && files[i].getName().endsWith(".jar"))
											{
												jarfiles.add(files[i]);
											}
										}
										
										CounterResultListener<Void> crl = new CounterResultListener<Void>(jarfiles.size(), new DelegationResultListener<Void>(ret));
										
										for (File jarfile : jarfiles)
										{
											addProcessResourceToLibService(jarfile).addResultListener(crl);
										}
									}
								});
								
								
							}
						});
					}
				}));
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Stops the service.
	 */
	@ServiceShutdown
	public IFuture stopService()
	{
		final Future ret = new Future();
		ia.getServiceContainer().getRequiredService("libservice").addResultListener(ia.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				ILibraryService libservice = (ILibraryService) result;
				libservice.removeLibraryServiceListener(liblistener);
				
				
			}
		}));
		
		aaaservice.removeAuthenticationListener(authlistener);
		
		return ret;
	}
	
	/**
	 *  Deploy a process model resource.
	 *  @param url The URL of the model resource.
	 */
	public IFuture<Void> addProcessResource(final ProcessResource resource)
	{
		System.out.println("new resource " + resource);
		final File file = new File(resourcedir + File.separator + resource.getFileName());
		final Future ret = new Future();
		if (file.exists())
		{
			ret.setException(new RuntimeException("Duplicate Resource: " + resource.getFileName()));
		}
		else
		{
			try
			{
				file.createNewFile();
				RandomAccessFile raFile = new RandomAccessFile(file, "rw");
				MappedByteBuffer mbb = raFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, resource.getContent().length);
				mbb.put(resource.getContent());
				raFile.close();
				
				addProcessResourceToLibService(file).addResultListener(new DelegationResultListener<Void>(ret));
			}
			catch (IOException e)
			{
				ret.setException(e);
			}
		}
		return ret;
	}
	
	protected IFuture<Void> addProcessResourceToLibService(final File file)
	{
		final Future<Void> ret = new Future<Void>();
		ia.getServiceContainer().getRequiredService("libservice").addResultListener(ia.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				ILibraryService ls = (ILibraryService) result;
				try
				{
					//TODO: Maybe...
					
					ls.addURL(ia.getComponentDescription().getResourceIdentifier(), file.toURI().toURL()).addResultListener(ia.createResultListener(new ExceptionDelegationResultListener<IResourceIdentifier, Void>(ret)
					{
						public void customResultAvailable(IResourceIdentifier result)
						{
							resourcefiles.put(result, file);
							ret.setResult(null);
						}
					}));
				}	
				catch (MalformedURLException e)
				{
					e.printStackTrace();
					ret.setException(e);
				}
				
			}
		}));
		return ret;
	}
	
	/**
	 *  Remove a process model resource.
	 *  @param info The process resource info.
	 */
	public IFuture removeProcessResource(final ProcessResourceInfo info)
	{
		final Future ret = new Future();
		ia.getServiceContainer().getRequiredService("libservice").addResultListener(ia.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				ILibraryService ls = (ILibraryService) result;
				ProcessResourceInfo xinfo = info;
				
				File file = resourcefiles.get(info.getResourceId());
				if (file.exists())
				{
					ls.removeResourceIdentifier(ia.getComponentDescription().getResourceIdentifier(), info.getResourceId());
					file.delete();
				}
				ret.setResult(null);
			}
		}));
		return ret;
	}
	
	/**
	 * Gets all available models.
	 * @return names of all models
	 */
	public IFuture<List<ProcessResourceInfo>> getModels()
	{
		Future<List<ProcessResourceInfo>> ret  = new Future<List<ProcessResourceInfo>>();
		List<ProcessResourceInfo> models = new ArrayList<ProcessResourceInfo>();
		for (Object obj : resources.values())
		{
			ProcessResourceInfo info = (ProcessResourceInfo) obj;
			models.add(info);
		}
		ret.setResult(models);
		return ret;
	}
	
	/**
	 * Returns a potentially incomplete set of loadable models
	 * 
	 * @return set of model paths
	 */
	/*public IFuture getLoadableModels()
	{
		final Future ret = new Future();
		SServiceProvider.getService(ia.getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				((ILibraryService) result).getURLs().addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						List urls = (List) result;
						Set modelSet = new HashSet();
						//Set knownPaths = new HashSet(modelPaths.values());
						for (Iterator it = urls.iterator(); it.hasNext(); )
						{
							URL url = (URL) it.next();
							File dir = new File(url.getFile());
							if (dir.isDirectory())
								modelSet.addAll(searchDirectory(dir, false));
						}
						//modelSet.removeAll(knownPaths);
						ret.setResult(modelSet);
					}
				}));
			};
		}));
		
		
		return ret;
	}*/
	
	/**
	 *  Recursively search a directory for workflow models.
	 * 	@param dir The directory.
	 * 	@param prependDir Flag whether to prepend the directory as path to the model path (used for recursion).
	 * 	@return Set of model paths in the directory.
	 */
	protected Set searchDirectory(File dir, boolean prependDir)
	{
		HashSet ret = new HashSet();
		File[] content = dir.listFiles();
		for (int i = 0; i < content.length; ++i)
		{
			if (content[i].isDirectory())
			{
				Set subSet = searchDirectory(content[i], true);
				for (Iterator it = subSet.iterator(); it.hasNext(); )
				{
					if (prependDir)
						ret.add(dir.getName().concat("/").concat((String) it.next()));
					else
						ret.add(it.next());
				}
			}
			else if ((content[i].getName().endsWith(".bpmn")) || (content[i].getName().endsWith(".gpmn")))
			{
				if (prependDir)
					ret.add(dir.getName().concat("/").concat(content[i].getName()));
				else
					ret.add(content[i].getName());
			}
		}
		
		return ret;
	}
	
	/**
	 *  Searches a .jar-archive for workflow models.
	 * 	@param jar The .jar-file.
	 * 	@return Set of model paths in the archive.
	 */
	private Set searchJar(File jar)
	{
		HashSet ret = new HashSet();
		try
		{
			JarFile jarFile = new JarFile(jar);
			for (Enumeration entries = jarFile.entries(); entries.hasMoreElements(); )
			{
				JarEntry entry = (JarEntry) entries.nextElement();
				if (entry.getName().endsWith(".bpmn") || entry.getName().endsWith(".gpmn"))
					ret.add(entry.getName());
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 *  Get a process model info of a specific name.
	 *  @param rid The resource identifier.
	 *  @param path The model path.
	 *  @return The process model.
	 */
	public IFuture<IModelInfo> getProcessModelInfo(ProcessResourceInfo info)
	{
		return loadProcessModelInfo(info);
	}
	
	/**
	 *  Get a process model file name of a specific name.
	 *  @param name The model name.
	 *  @return The process model file name.
	 */
	public IFuture getProcessFileName(String name)
	{
		return new Future(name);
	}
	
	/**
	 * Gets all available models.
	 * @return names of all models
	 */
	/*public IFuture getModelNames()
	{
		return new Future(new HashSet(modelrefcount.keySet()));
	}*/
	
	/**
	 *  Get the imports.
	 */
	public IFuture getImports()
	{
		return IFuture.DONE;
	}
	
	/**
	 * Adds a process repository listener.
	 * 
	 * @param listener the listener
	 */
	public IFuture<Void> addProcessRepositoryListener(IComponentIdentifier client, IProcessRepositoryListener listener)
	{
		processlisteners.put(client, listener);
		
		for (Iterator it = resources.keySet().iterator(); it.hasNext(); )
		{
			IResourceIdentifier rid = (IResourceIdentifier) it.next();
			Collection coll = resources.getCollection(rid);
			for (Object obj : coll)
			{
				ProcessResourceInfo info = (ProcessResourceInfo) obj;
				listener.processModelAdded(new ProcessRepositoryEvent(info));
			}
		}
		return IFuture.DONE;
	}
	
	/**
	 * Removes a process repository listener.
	 * 
	 * @param listener the listener
	 */
	public IFuture<Void> removeProcessRepositoryListener(IComponentIdentifier client, IProcessRepositoryListener listener)
	{
		processlisteners.remove(client, listener);
		return IFuture.DONE;
	}
	
	/**
	 *  Fire an model addition event.
	 *  @param info The removed model.
	 */
	protected void fireModelAddedEvent(ProcessResourceInfo info)
	{
		Object[] ls = processlisteners.getObjects();
		for (int i = 0; i < ls.length; ++i)
		{
			final IProcessRepositoryListener listener = (IProcessRepositoryListener) ls[i];
			listener.processModelAdded(new ProcessRepositoryEvent(info)).addResultListener(ia.createResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
				}
				
				public void exceptionOccurred(Exception exception)
				{
					Object[] keys = processlisteners.getKeys();
					for (int i = 0; i < keys.length; ++i)
						processlisteners.remove(keys[i], listener);
				}
			}));
		}
	}
	
	/**
	 *  Fire an model removal event.
	 *  @param info The removed model.
	 */
	protected void fireModelRemovedEvent(ProcessResourceInfo info)
	{
		Object[] ls = processlisteners.getObjects();
		for (int i = 0; i < ls.length; ++i)
		{
			final IProcessRepositoryListener listener = (IProcessRepositoryListener) ls[i];
			listener.processModelRemoved(new ProcessRepositoryEvent(info)).addResultListener(ia.createResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
				}
				
				public void exceptionOccurred(Exception exception)
				{
					Object[] keys = processlisteners.getKeys();
					for (int i = 0; i < keys.length; ++i)
						processlisteners.remove(keys[i], listener);
				}
			}));
		}
	}
	
	/**
	 *  Loads a workflow model.
	 *  @param info The process resource information.
	 *  @return The workflow model.
	 */
	protected IFuture<IModelInfo> loadProcessModelInfo(final ProcessResourceInfo info)
	{
		final Future<IModelInfo> ret = new Future<IModelInfo>();
		IExecutionService ex = (IExecutionService) ia.getServiceContainer().getProvidedService("execution_service");
		ex.loadModel(info).addResultListener(new DelegationResultListener(ret));
		return ret;
	}
	
	/**
	 *  Adds a model to the repository.
	 *  @param path Path of the model.
	 */
	/*protected void addModel(String path)
	{
		try
		{
			Integer refcount = (Integer) modelrefcount.get(path);
			if (refcount == null)
				refcount = Integer.valueOf(0);
			refcount = Integer.valueOf(refcount.intValue() + 1);
			modelrefcount.put(path, refcount);
			fireModelAddedEvent(path);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}*/
	
	/**
	 *  Removes a model from the repository.
	 *  @param path Path of the model.
	 */
	/*protected void removeModel(String path)
	{
		Integer refcount = (Integer) modelrefcount.get(path);
		if ((refcount != null))
		{
			refcount = Integer.valueOf(refcount.intValue() - 1);
			if (refcount.intValue() <= 0)
			{
				modelrefcount.remove(path);
				fireModelRemovedEvent(path);
			}
		}
	}*/
}
