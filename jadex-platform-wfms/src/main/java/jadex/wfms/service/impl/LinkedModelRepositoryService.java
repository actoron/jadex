package jadex.wfms.service.impl;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.library.ILibraryService;
import jadex.bridge.service.library.ILibraryServiceListener;
import jadex.bridge.service.library.LibraryService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.wfms.client.ClientInfo;
import jadex.wfms.client.ProcessResource;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IExecutionService;
import jadex.wfms.service.IModelRepositoryService;
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
public class LinkedModelRepositoryService implements IModelRepositoryService
{
	/** Component access. */
	@ServiceComponent
	protected IInternalAccess ia;
	
	/** The imports */
	//private Set imports;
	
	/** The process repository listeners */
	protected Map<IComponentIdentifier, Set<IProcessRepositoryListener>> pListeners;
	
	/** URL entries */
	protected Map urlEntries;
	
	/** Model reference counter */
	protected Map modelRefCount;
	
	/** Resource directory */
	protected File resourceDir;
	
	/** Library service listener */
	protected ILibraryServiceListener liblistener;
	
	public LinkedModelRepositoryService()
	{
		this.urlEntries = new HashMap();
		this.modelRefCount = new HashMap();
	}
	
	/**
	 *  Start the service.
	 */
	@ServiceStart
	public IFuture startService()
	{
		System.out.println("Starting Repository Service (Execution Component)");
		final Future ret = new Future();
			
		SServiceProvider.getService(ia.getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				final IExternalAccess exta = ia.getExternalAccess();
				liblistener = new ILibraryServiceListener()
				{
					public IFuture urlRemoved(final URL url)
					{
						return exta.scheduleStep(new IComponentStep()
						{
							public Object execute(IInternalAccess ia)
							{
								Set modelSet = (Set) urlEntries.remove(url);
								if (modelSet != null)
								{
									for (Iterator it = modelSet.iterator(); it.hasNext(); )
									{
										String path = (String) it.next();
										removeModel(path);
									}
								}
								return null;
							}
						});
					}
					
					public IFuture urlAdded(final URL url)
					{
						return exta.scheduleStep(new IComponentStep()
						{
							public Object execute(IInternalAccess ia)
							{
								File dir = new File(url.getFile());
								Set modelSet = new HashSet();
								if (dir.isDirectory())
									modelSet = searchDirectory(dir, false);
								else if (dir.getName().endsWith(".jar"))
									modelSet = searchJar(dir);
								for (Iterator it = modelSet.iterator(); it.hasNext(); )
									addModel((String) it.next());
								urlEntries.put(url, modelSet);
								
								return null;
							}
						});
					}
				};
				
				((ILibraryService)result).addLibraryServiceListener(liblistener);
				
				getLoadableModels().addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						Set loadableModels = (Set) result;
						for (Iterator it = loadableModels.iterator(); it.hasNext(); )
						{
							String path = (String) it.next();
							addModel(path);
						}
						
						resourceDir = new File(System.getProperty("user.home") + File.separator + ".jadexwfms");
						if (!resourceDir.exists())
							resourceDir.mkdir();
						if (!resourceDir.isDirectory())
						{
							ret.setException(new RuntimeException("Resource directory blocked " + resourceDir));
							return;
						}
						
						File[] files = resourceDir.listFiles();
						for (int i = 0; i < files.length; ++i)
							if (files[i].isFile() && files[i].getName().endsWith(".jar"))
							{
								final File file = files[i];
								SServiceProvider.getService(ia.getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(ia.createResultListener(new DefaultResultListener()
								{
									
									public void resultAvailable(Object result)
									{
										((ILibraryService) result).addURL(LibraryService.toURL(file));
									}
								}));
							}
						
						ret.setResult(null);
					};
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Stops the service.
	 */
	@ServiceShutdown
	public IFuture stopService()
	{
		final Future ret = new Future();
		SServiceProvider.getService(ia.getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				ILibraryService libservice = (ILibraryService) result;
				libservice.removeLibraryServiceListener(liblistener);
				ret.setResult(null);
			}
		}));
		return ret;
	}
	
	/**
	 *  Add a process model resource.
	 *  @param url The URL of the model resource.
	 */
	public IFuture addProcessResource(final ProcessResource resource)
	{
		final File file = new File(resourceDir + File.separator + resource.getFileName());
		if (file.exists())
			return IFuture.DONE;
		
		final Future ret = new Future();
		try
		{
			file.createNewFile();
			RandomAccessFile raFile = new RandomAccessFile(file, "rw");
			MappedByteBuffer mbb = raFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, resource.getContent().length);
			mbb.put(resource.getContent());
			raFile.close();
			SServiceProvider.getService(ia.getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					ILibraryService ls = (ILibraryService) result;
					try
					{
						ls.addURL(file.toURI().toURL());
					}	
					catch (MalformedURLException e)
					{
						e.printStackTrace();
					}
					ret.setResult(null);
				}
			}));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return IFuture.DONE;
		}
		return ret;
	}
	
	/**
	 *  Remove a process model resource.
	 *  @param url The URL of the model resource.
	 */
	public IFuture removeProcessResource(final String resourceName)
	{
		final Future ret = new Future();
		SServiceProvider.getService(ia.getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				ILibraryService ls = (ILibraryService) result;
				File file = new File(resourceDir + File.separator + resourceName);
				if (file.exists())
				{
					try
					{
						ls.removeURL(file.toURI().toURL());
					}
					catch (MalformedURLException e)
					{
						e.printStackTrace();
					}
					file.delete();
				}
				ret.setResult(null);
			}
		}));
		return ret;
	}
	
	/**
	 * Returns a potentially incomplete set of loadable models
	 * 
	 * @return set of model paths
	 */
	public IFuture getLoadableModels()
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
	}
	
	private Set searchDirectory(File dir, boolean prependDir)
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
	 *  Get a process model of a specific name.
	 *  @param name The model name.
	 *  @return The process model.
	 */
	public IFuture getProcessModel(String name)
	{
		return loadProcessModel(name);
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
	public IFuture getModelNames()
	{
		return new Future(new HashSet(modelRefCount.keySet()));
	}
	
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
	public IFuture addProcessRepositoryListener(IComponentIdentifier client, IProcessRepositoryListener listener)
	{
		Map<IComponentIdentifier, Set<IProcessRepositoryListener>> listeners = getListeners();
		Set<IProcessRepositoryListener> lisset = listeners.get(client);
		if (lisset == null)
		{
			lisset = new HashSet<IProcessRepositoryListener>();
			listeners.put(client, lisset);
		}
		lisset.add(listener);
		
		for (Iterator it = modelRefCount.keySet().iterator(); it.hasNext(); )
			listener.processModelAdded(new ProcessRepositoryEvent((String) it.next()));
		return IFuture.DONE;
	}
	
	/**
	 * Removes a process repository listener.
	 * 
	 * @param listener the listener
	 */
	public IFuture removeProcessRepositoryListener(IComponentIdentifier client, IProcessRepositoryListener listener)
	{
		Set<IProcessRepositoryListener> lisset = getListeners().get(client);
		if (lisset != null)
			lisset.remove(listener);
		return IFuture.DONE;
	}
	
	protected Map<IComponentIdentifier, Set<IProcessRepositoryListener>> getListeners()
	{
		if (pListeners == null)
		{
			pListeners = new HashMap<IComponentIdentifier, Set<IProcessRepositoryListener>>();
			SServiceProvider.getService(ia.getServiceContainer(), IAAAService.class, RequiredServiceInfo.SCOPE_GLOBAL).addResultListener(ia.createResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object result)
				{
					IAAAService as = (IAAAService) result;
					as.addAuthenticationListener(new IAuthenticationListener()
					{
						final IExternalAccess exta = ia.getExternalAccess();
						public IFuture deauthenticated(final IComponentIdentifier client, ClientInfo info)
						{
							return exta.scheduleStep(new IComponentStep()
							{
								
								public Object execute(IInternalAccess ia)
								{
									pListeners.remove(client);
									return null;
								}
							});
						}
						
						public IFuture authenticated(IComponentIdentifier client, ClientInfo info)
						{
							return IFuture.DONE;
						}
					});
				}
				
				@Override
				public void exceptionOccurred(Exception exception)
				{
					super.exceptionOccurred(exception);
				}
			}));
			
		}
		return pListeners;
	}
	
	private void fireModelAddedEvent(String modelName)
	{
		Map<IComponentIdentifier, Set<IProcessRepositoryListener>> listeners = getListeners();
		for (Iterator<Set<IProcessRepositoryListener>> it = listeners.values().iterator(); it.hasNext(); )
		{
			final Set<IProcessRepositoryListener> listset = it.next();
			IProcessRepositoryListener[] ls = (IProcessRepositoryListener[]) listset.toArray(new IProcessRepositoryListener[0]);
			for (int i = 0; i < ls.length; ++i)
			{
				final IProcessRepositoryListener listener = ls[i];
				listener.processModelAdded(new ProcessRepositoryEvent(modelName)).addResultListener(ia.createResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
					}
					
					public void exceptionOccurred(Exception exception)
					{
						listset.remove(listener);
					}
				}));
			}
		}
	}
	
	private void fireModelRemovedEvent(String modelName)
	{
		Map<IComponentIdentifier, Set<IProcessRepositoryListener>> listeners = getListeners();
		for (Iterator<Set<IProcessRepositoryListener>> it = listeners.values().iterator(); it.hasNext(); )
		{
			final Set<IProcessRepositoryListener> listset = it.next();
			IProcessRepositoryListener[] ls = (IProcessRepositoryListener[]) listset.toArray(new IProcessRepositoryListener[0]);
			for (int i = 0; i < ls.length; ++i)
			{
				final IProcessRepositoryListener listener = ls[i];
				listener.processModelRemoved(new ProcessRepositoryEvent(modelName)).addResultListener(ia.createResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
					}
					
					public void exceptionOccurred(Exception exception)
					{
						listset.remove(listener);
					}
				}));
			}
		}
	}
	
	private IFuture loadProcessModel(final String filename)
	{
		final Future ret = new Future();
		SServiceProvider.getService(ia.getServiceContainer(), IExecutionService.class, RequiredServiceInfo.SCOPE_APPLICATION).addResultListener(ia.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				IExecutionService ex = (IExecutionService) result;
				//TODO: Imports?
				ex.loadModel(filename, null).addResultListener(new DelegationResultListener(ret));
			}
		}));
		return ret;
	}
	
	
	
	private void addModel(String path)
	{
		try
		{
			Integer refcount = (Integer) modelRefCount.get(path);
			if (refcount == null)
				refcount = new Integer(0);
			refcount = new Integer(refcount.intValue() + 1);
			modelRefCount.put(path, refcount);
			fireModelAddedEvent(path);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void removeModel(String path)
	{
		Integer refcount = (Integer) modelRefCount.get(path);
		if ((refcount != null))
		{
			refcount = new Integer(refcount.intValue() - 1);
			if (refcount.intValue() <= 0)
			{
				modelRefCount.remove(path);
				fireModelRemovedEvent(path);
			}
		}
	}
}
