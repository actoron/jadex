package jadex.wfms.service.impl;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.library.ILibraryService;
import jadex.bridge.service.library.ILibraryServiceListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ThreadSuspendable;
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
import java.util.Collection;
import java.util.Collections;
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
public class LinkedModelRepositoryService extends BasicService implements IModelRepositoryService
{
	/** The wfms. */
	protected IServiceProvider provider;
	
	/** The imports */
	//private Set imports;
	
	/** The process repository listeners */
	private Map<IComponentIdentifier, Set<IProcessRepositoryListener>> pListeners;
	
	/** URL entries */
	private Map urlEntries;
	
	/** Model reference counter */
	private Map modelRefCount;
	
	/** Resource directory */
	protected File resourceDir;
	
	public LinkedModelRepositoryService(IServiceProvider provider)
	{
		//super(BasicService.createServiceIdentifier(provider.getId(), LinkedModelRepositoryService.class));
		super(provider.getId(), IModelRepositoryService.class, null);

		this.provider = provider;
		// TODO: Hack! Needs proper imports...
		//this.imports = Collections.synchronizedSet(new HashSet());
		this.urlEntries = Collections.synchronizedMap(new HashMap());
		this.modelRefCount = Collections.synchronizedMap(new HashMap());
	}
	
	/**
	 *  Start the service.
	 */
	public IFuture startService()
	{
		synchronized (modelRefCount)
		{
			Set loadableModels = getLoadableModels();
			for (Iterator it = loadableModels.iterator(); it.hasNext(); )
			{
				String path = (String) it.next();
				addModel(path);
			}
			
			((ILibraryService)SServiceProvider.getService(provider, ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.get(new ThreadSuspendable())).addLibraryServiceListener(new ILibraryServiceListener()
			{
				public IFuture urlRemoved(URL url)
				{
					synchronized (modelRefCount)
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
					}
					return new Future(null);
				}
				
				public IFuture urlAdded(URL url)
				{
					synchronized (modelRefCount)
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
					}
					return new Future(null);
				}
			});
		}
		
		resourceDir = new File(System.getProperty("user.home") + File.separator + ".jadexwfms");
		if (!resourceDir.exists())
			resourceDir.mkdir();
		if (!resourceDir.isDirectory())
			throw new RuntimeException("Resource directory blocked " + resourceDir);
		
		File[] files = resourceDir.listFiles();
		for (int i = 0; i < files.length; ++i)
			if (files[i].isFile() && files[i].getName().endsWith(".jar"))
			{
				final File file = files[i];
				SServiceProvider.getService(provider, ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener()
				{
					
					public void resultAvailable(Object result)
					{
						try
						{
							((ILibraryService) result).addURL(file.toURI().toURL());
						}	
						catch (MalformedURLException e)
						{
							e.printStackTrace();
						}
					}
				});
			}
		
		return super.startService();
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public void shutdownService(IResultListener listener)
	{
		if(listener!=null)
			listener.resultAvailable(null);
	}
	
	/**
	 *  Add a process model resource.
	 *  @param url The URL of the model resource.
	 */
	public synchronized void addProcessResource(final ProcessResource resource)
	{
		final File file = new File(resourceDir + File.separator + resource.getFileName());
		if (file.exists())
			return;
		try
		{
			file.createNewFile();
			RandomAccessFile raFile = new RandomAccessFile(file, "rw");
			MappedByteBuffer mbb = raFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, resource.getContent().length);
			mbb.put(resource.getContent());
			raFile.close();
			SServiceProvider.getService(provider, ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener()
			{
				
				public void resultAvailable(Object result)
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
				}
			});
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 *  Remove a process model resource.
	 *  @param url The URL of the model resource.
	 */
	public synchronized void removeProcessResource(final String resourceName)
	{
		SServiceProvider.getService(provider, ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				ILibraryService ls = (ILibraryService) result;
				synchronized (LinkedModelRepositoryService.this)
				{
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
				}
			}
		});
	}
	
	/**
	 * Returns a potentially incomplete set of loadable models
	 * 
	 * @return set of model paths
	 */
	public Set getLoadableModels()
	{
		synchronized (modelRefCount)
		{
			//Set knownPaths = new HashSet(modelPaths.values());
			Set modelSet = new HashSet();
			List urls = (List) ((ILibraryService) SServiceProvider.getService(provider, ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(new ThreadSuspendable())).getURLs().get(new ThreadSuspendable());
			for (Iterator it = urls.iterator(); it.hasNext(); )
			{
				URL url = (URL) it.next();
				File dir = new File(url.getFile());
				if (dir.isDirectory())
					modelSet.addAll(searchDirectory(dir, false));
			}
			//modelSet.removeAll(knownPaths);
			return modelSet;
		}
		//return new HashSet();
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
		//return (ILoadableComponentModel)modelRepository.get(name);
		return loadProcessModel(name);
	}
	
	/**
	 *  Get a process model file name of a specific name.
	 *  @param name The model name.
	 *  @return The process model file name.
	 */
	public String getProcessFileName(String name)
	{
		return name;
	}
	
	/**
	 * Gets all available models.
	 * @return names of all models
	 */
	public Collection getModelNames()
	{
		synchronized (modelRefCount)
		{
			return new HashSet(modelRefCount.keySet());
		}
	}
	
	/**
	 *  Get the imports.
	 */
	public String[] getImports()
	{
		//return (String[]) imports.toArray(new String[imports.size()]);
		return null;
	}
	
	/**
	 * Adds a process repository listener.
	 * 
	 * @param listener the listener
	 */
	public void addProcessRepositoryListener(IComponentIdentifier client, IProcessRepositoryListener listener)
	{
		Map<IComponentIdentifier, Set<IProcessRepositoryListener>> listeners = getListeners();
		synchronized (getListeners())
		{
			Set<IProcessRepositoryListener> lisSet = listeners.get(client);
			if (lisSet == null)
			{
				lisSet = new HashSet<IProcessRepositoryListener>();
				listeners.put(client, lisSet);
			}
			lisSet.add(listener);
			
			synchronized(modelRefCount)
			{
				for (Iterator it = modelRefCount.keySet().iterator(); it.hasNext(); )
					listener.processModelAdded(new ProcessRepositoryEvent((String) it.next()));
			}
		}
	}
	
	/**
	 * Removes a process repository listener.
	 * 
	 * @param listener the listener
	 */
	public void removeProcessRepositoryListener(IComponentIdentifier client, IProcessRepositoryListener listener)
	{
		Set<IProcessRepositoryListener> lisSet = getListeners().get(client);
		if (lisSet != null)
			lisSet.remove(listener);
	}
	
	protected Map<IComponentIdentifier, Set<IProcessRepositoryListener>> getListeners()
	{
		synchronized(this)
		{
			if (pListeners == null)
			{
				pListeners = Collections.synchronizedMap(new HashMap<IComponentIdentifier, Set<IProcessRepositoryListener>>());
				SServiceProvider.getService(provider, IAAAService.class).addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						IAAAService as = (IAAAService) result;
						as.addAuthenticationListener(new IAuthenticationListener()
						{
							public void deauthenticated(IComponentIdentifier client, ClientInfo info)
							{
								pListeners.remove(client);
							}
							
							public void authenticated(IComponentIdentifier client, ClientInfo info)
							{
							}
						});
					}
				});
			}
		}
		return pListeners;
	}
	
	private void fireModelAddedEvent(String modelName)
	{
		Map<IComponentIdentifier, Set<IProcessRepositoryListener>> listeners = getListeners();
		synchronized (listeners)
		{
			for (Iterator<Set<IProcessRepositoryListener>> it = listeners.values().iterator(); it.hasNext(); )
				for (Iterator<IProcessRepositoryListener> it2 = it.next().iterator(); it2.hasNext(); )
				{
					IProcessRepositoryListener listener = (IProcessRepositoryListener) it2.next();
					listener.processModelAdded(new ProcessRepositoryEvent(modelName));
				}
		}
	}
	
	private void fireModelRemovedEvent(String modelName)
	{
		Map<IComponentIdentifier, Set<IProcessRepositoryListener>> listeners = getListeners();
		synchronized (listeners)
		{
			for (Iterator<Set<IProcessRepositoryListener>> it = listeners.values().iterator(); it.hasNext(); )
				for (Iterator<IProcessRepositoryListener> it2 = it.next().iterator(); it2.hasNext(); )
				{
					IProcessRepositoryListener listener = (IProcessRepositoryListener) it2.next();
					listener.processModelRemoved(new ProcessRepositoryEvent(modelName));
				}
		}
	}
	
	private IFuture loadProcessModel(String filename)
	{
		IExecutionService ex = (IExecutionService) SServiceProvider.getService(provider, IExecutionService.class).get(new ThreadSuspendable());
		return ex.loadModel(filename, getImports());
	}
	
	
	
	private void addModel(String path)
	{
		try
		{
			synchronized (modelRefCount)
			{
				Integer refcount = (Integer) modelRefCount.get(path);
				if (refcount == null)
					refcount = new Integer(0);
				refcount = new Integer(refcount.intValue() + 1);
				modelRefCount.put(path, refcount);
			}
			fireModelAddedEvent(path);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void removeModel(String path)
	{
		synchronized (modelRefCount)
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
}
