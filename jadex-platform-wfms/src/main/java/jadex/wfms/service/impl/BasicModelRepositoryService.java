package jadex.wfms.service.impl;

import jadex.bridge.ILoadableComponentModel;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;
import jadex.service.library.ILibraryService;
import jadex.wfms.listeners.IProcessRepositoryListener;
import jadex.wfms.listeners.ProcessRepositoryEvent;
import jadex.wfms.service.IExecutionService;
import jadex.wfms.service.IModelRepositoryService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Basic Model Repository Service implementation
 *
 */
public class BasicModelRepositoryService implements IModelRepositoryService
{
	private static final String LIBRARY_STATE_PATH = System.getProperty("java.io.tmpdir").concat(File.separator).concat("wfms_lib_state.db");
	private static final String REPOSITORY_PATH = System.getProperty("java.io.tmpdir").concat(File.separator).concat("wfms_repository.db");
	
	/** The wfms. */
	protected IServiceContainer wfms;
	
	/** The imports */
	private String[] imports;
	
	/** The models. */
	private Map models;
	
	/** The model paths */
	private Map modelPaths;
	
	/** The process repository listeners */
	private Set listeners;
	
	public BasicModelRepositoryService(IServiceContainer wfms)
	{
		this.wfms = wfms;
		// TODO: Hack! Needs proper imports...
		this.imports = new String[0];
		this.listeners = Collections.synchronizedSet(new HashSet());
		this.models = Collections.synchronizedMap(new HashMap());
		this.modelPaths = Collections.synchronizedMap(new HashMap());
		
		try
		{
			readRepository();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 *  Start the service.
	 */
	public void start()
	{
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public void shutdown(IResultListener listener)
	{
	}
	
	/**
	 *  Add a process model.
	 *  @param client The client.
	 *  @param name The name of the model.
	 *  @param path The path to the model.
	 */
	public void addProcessModel(String filename)
	{
		String modelName = loadProcessModel(filename);
		synchronized (models)
		{
			if (modelName != null)
			{
				writeRepository();
				fireModelAddedEvent(modelName);
			}
		}
	}
	
	/**
	 *  Remove a process model.
	 *  @param name The name of the model.
	 */
	public void removeProcessModel(String name)
	{
		synchronized (models)
		{
			if (models.containsKey(name))
			{
				models.remove(name);
				modelPaths.remove(name);
				writeRepository();
				fireModelRemovedEvent(name);
			}
		}
	}
	
	/**
	 * Returns a potentially incomplete set of loadable models
	 * 
	 * @return set of model paths
	 */
	public Set getLoadableModels()
	{
		synchronized (models)
		{
			Set knownPaths = new HashSet(modelPaths.values());
			Set modelSet = new HashSet();
			List urls = ((ILibraryService) wfms.getService(ILibraryService.class)).getURLs();
			for (Iterator it = urls.iterator(); it.hasNext(); )
			{
				URL url = (URL) it.next();
				File dir = new File(url.getFile());
				if (dir.isDirectory())
					modelSet.addAll(searchDirectory(dir, false));
			}
			modelSet.removeAll(knownPaths);
			return modelSet;
		}
	}
	
	/**
	 *  Get a process model of a specific name.
	 *  @param name The model name.
	 *  @return The process model.
	 */
	public ILoadableComponentModel getProcessModel(String name)
	{
		return (ILoadableComponentModel)models.get(name);
	}
	
	/**
	 * Gets all available models.
	 * @return names of all models
	 */
	public Collection getModelNames()
	{
		synchronized (models)
		{
			return new HashSet(models.keySet());
		}
	}
	
	/**
	 *  Get the imports.
	 */
	public String[] getImports()
	{
		return imports;
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
			
			synchronized(models)
			{
				for (Iterator it = models.keySet().iterator(); it.hasNext(); )
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
	
	private String loadProcessModel(String filename)
	{
		IExecutionService ex = (IExecutionService)wfms.getService(IExecutionService.class);
		ILoadableComponentModel model = ex.loadModel(filename, imports);
		String modelName = model.getName();
		if (modelName == null)
		{
			modelName = model.getFilename();
			modelName = modelName.substring(Math.max(modelName.lastIndexOf('/'), modelName.lastIndexOf(File.separator)) + 1);
		}
		synchronized (models)
		{
			if (!models.containsKey(modelName))
			{
				models.put(modelName, model);
				modelPaths.put(modelName, filename);
				return modelName;
			}
		}
		return null;
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
						ret.add(dir.getName().concat(File.separator).concat((String) it.next()));
					else
						ret.add(it.next());
				}
			}
			else if ((content[i].getName().endsWith(".bpmn")) || (content[i].getName().endsWith(".gpmn")))
			{
				if (prependDir)
					ret.add(dir.getName().concat(File.separator).concat(content[i].getName()));
				else
					ret.add(content[i].getName());
			}
		}
		
		return ret;
	}
	
	private void readRepository() throws IOException
	{
		try
		{
			File file = new File(LIBRARY_STATE_PATH);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			while (line != null)
			{
				try
				{
					((ILibraryService) wfms.getService(ILibraryService.class)).addPath(line);
				}
				catch (Exception e)
				{
				}
				line = reader.readLine();
			}
			
			reader.close();
		}
		catch (Exception e)
		{
		}
		
		File file = new File(REPOSITORY_PATH);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		String line = reader.readLine();
		while (line != null)
		{
			try
			{
				loadProcessModel(line);
			}
			catch (Exception e)
			{
			}
			line = reader.readLine();
		}
		reader.close();
		
		writeRepository();
	}
	
	private void writeRepository()
	{
		try
		{
			File tmpFile = File.createTempFile("wfms_lib_state", null);
			PrintWriter writer = new PrintWriter(tmpFile);
			
			List urls = ((ILibraryService) wfms.getService(ILibraryService.class)).getURLs();
			for (Iterator it = urls.iterator(); it.hasNext(); )
			{
				URL url = (URL) it.next();
				if ((new File(url.getPath()).isDirectory()))
					writer.println(url.getPath());
			}
			writer.close();
			tmpFile.renameTo(new File(LIBRARY_STATE_PATH));
		}
		catch (IOException e)
		{
		}
		
		boolean done = false;
		while (!done)
		{
			try
			{
				File tmpFile = File.createTempFile("wfms_repository", null);
				PrintWriter writer = new PrintWriter(tmpFile);
				synchronized(models)
				{
					for (Iterator it = modelPaths.values().iterator(); it.hasNext(); )
					{
						String path = (String) it.next();
						writer.println(path);
					}
				}
				writer.close();
				File file = new File(REPOSITORY_PATH);
				tmpFile.renameTo(file);
				done = true;
			}
			catch (IOException e)
			{
			}
		}
	}
}
