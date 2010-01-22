package jadex.wfms.service.impl;

import jadex.bridge.ILoadableComponentModel;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;
import jadex.service.PropertyServiceContainer;
import jadex.service.library.ILibraryService;
import jadex.wfms.listeners.IProcessRepositoryListener;
import jadex.wfms.listeners.ProcessRepositoryEvent;
import jadex.wfms.service.IExecutionService;
import jadex.wfms.service.IModelRepositoryService;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Basic Model Repository Service implementation
 *
 */
public class BasicModelRepositoryService implements IModelRepositoryService
{
	/** The wfms. */
	protected IServiceContainer wfms;
	
	/** The imports */
	private String[] imports;
	
	/** The models. */
	protected Map models;
	
	/** The process repository listeners */
	private Set listeners;
	
	public BasicModelRepositoryService(IServiceContainer wfms)
	{
		this(wfms, null);
	}
	
	public BasicModelRepositoryService(IServiceContainer wfms, String[] models)
	{
		this.wfms = wfms;
		// TODO: Hack! Needs proper imports...
		this.imports = new String[0];
		this.listeners = Collections.synchronizedSet(new HashSet());
		this.models = Collections.synchronizedMap(new HashMap());
		
		if (models != null)
		{
			for (int i = 0; i < models.length; ++i)
			{
				addProcessModel(models[i]);
			}
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
				fireModelRemovedEvent(name);
			}
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
	 *  Remove a process model.
	 *  @param client The client.
	 *  @param name The name of the model.
	 *  @param path The path to the model.
	 */
//	public void removeProcessModel(IClient client, String name);
	
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
}
