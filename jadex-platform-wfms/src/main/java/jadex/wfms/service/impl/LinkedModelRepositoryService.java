package jadex.wfms.service.impl;

import jadex.bridge.ILoadableComponentModel;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IService;
import jadex.service.IServiceContainer;
import jadex.service.library.ILibraryService;
import jadex.service.library.ILibraryServiceListener;
import jadex.wfms.listeners.IProcessRepositoryListener;
import jadex.wfms.listeners.ProcessRepositoryEvent;
import jadex.wfms.service.IExecutionService;
import jadex.wfms.service.IModelRepositoryService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
public class LinkedModelRepositoryService implements IModelRepositoryService, IService
{
	/** The wfms. */
	protected IServiceContainer wfms;
	
	/** The imports */
	private Set imports;
	
	/** The process repository listeners */
	private Set listeners;
	
	/** URL entries */
	private Map urlEntries;
	
	/** Model reference counter */
	private Map modelRefCount;
	
	public LinkedModelRepositoryService(IServiceContainer wfms)
	{
		this.wfms = wfms;
		// TODO: Hack! Needs proper imports...
		this.imports = Collections.synchronizedSet(new HashSet());
		this.listeners = Collections.synchronizedSet(new HashSet());
		this.urlEntries = Collections.synchronizedMap(new HashMap());
		this.modelRefCount = Collections.synchronizedMap(new HashMap());
	}
	
	/**
	 *  Start the service.
	 */
	public void startService()
	{
		synchronized (modelRefCount)
		{
			Set loadableModels = getLoadableModels();
			for (Iterator it = loadableModels.iterator(); it.hasNext(); )
			{
				String path = (String) it.next();
				addModel(path);
			}
			
			((ILibraryService) wfms.getService(ILibraryService.class)).addLibraryServiceListener(new ILibraryServiceListener()
			{
				public void urlRemoved(URL url)
				{
					synchronized (modelRefCount)
					{
						Set modelSet = (Set) urlEntries.remove(url);
						for (Iterator it = modelSet.iterator(); it.hasNext(); )
						{
							String path = (String) it.next();
							removeModel(path);
						}
					}
				}
				
				public void urlAdded(URL url)
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
				}
			});
		}
		
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public void shutdownService(IResultListener listener)
	{
		if(listener!=null)
			listener.resultAvailable(this, null);
	}
	
	/**
	 *  Add a process model.
	 *  @param client The client.
	 *  @param name The name of the model.
	 *  @param path The path to the model.
	 */
	public void addProcessModel(String filename)
	{
		/*String modelName = loadProcessModel(filename);
		synchronized (model)
		{
			if (modelName != null)
			{
				writeRepository();
				fireModelAddedEvent(modelName);
			}
		}*/
	}
	
	/**
	 *  Remove a process model.
	 *  @param name The name of the model.
	 */
	public void removeProcessModel(String name)
	{
		/*synchronized (models)
		{
			if (models.containsKey(name))
			{
				models.remove(name);
				modelPaths.remove(name);
				writeRepository();
				fireModelRemovedEvent(name);
			}
		}*/
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
			List urls = ((ILibraryService) wfms.getService(ILibraryService.class)).getURLs();
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
	
	/**
	 *  Get a process model of a specific name.
	 *  @param name The model name.
	 *  @return The process model.
	 */
	public ILoadableComponentModel getProcessModel(String name)
	{
		//return (ILoadableComponentModel)modelRepository.get(name);
		return (ILoadableComponentModel)loadProcessModel(name);
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
	public void addProcessRepositoryListener(IProcessRepositoryListener listener)
	{
		synchronized (listeners)
		{
			listeners.add(listener);
			
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
	public void removeProcessRepositoryListener(IProcessRepositoryListener listener)
	{
		listeners.remove(listener);
	}
	
	private void fireModelAddedEvent(String modelName)
	{
		synchronized (listeners)
		{
			for (Iterator it = listeners.iterator(); it.hasNext(); )
			{
				IProcessRepositoryListener listener = (IProcessRepositoryListener) it.next();
				listener.processModelAdded(new ProcessRepositoryEvent(modelName));
			}
		}
	}
	
	private void fireModelRemovedEvent(String modelName)
	{
		synchronized (listeners)
		{
			for (Iterator it = listeners.iterator(); it.hasNext(); )
			{
				IProcessRepositoryListener listener = (IProcessRepositoryListener) it.next();
				listener.processModelRemoved(new ProcessRepositoryEvent(modelName));
			}
		}
	}
	
	private ILoadableComponentModel loadProcessModel(String filename)
	{
		IExecutionService ex = (IExecutionService)wfms.getService(IExecutionService.class);
		return ex.loadModel(filename, getImports());
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
