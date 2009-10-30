package jadex.wfms.service.repository;

import jadex.adapter.base.SComponentFactory;
import jadex.bridge.ILoadableComponentModel;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Basic Model Repository Service implementation
 *
 */
public class BasicModelRepositoryService implements IModelRepositoryService
{
	/** The wfms. */
	protected IServiceContainer container;
	
	/** The imports */
	private String[] imports;
	
	/** Map from BPMN model name to model resource */
//	private Map bpmnModels;
	
	/** Map from GPMN model name to model resource */
//	private Map gpmnModels;
	
	/** The models. */
	protected Map models;
	
	/** The model loader */
//	private BpmnModelLoader loader;
	
	
	public BasicModelRepositoryService(IServiceContainer container, String[] imports)
	{
		this.container = container;
		this.imports = imports;
		this.models = new HashMap();
//		this.loader = new BpmnModelLoader();
//		bpmnModels = new HashMap();
//		gpmnModels = new HashMap();
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
	 * Adds a BPMN model.
	 * @param name name of the model
	 * @param path path to the model
	 * /
	public synchronized void addBpmnModel(String name, String path)
	{
		MBpmnModel model;
		try
		{
			model = loader.loadBpmnModel(path, imports);
		} catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
		bpmnModels.put(name, model);
	}*/
	
	/**
	 * Removes a BPMN model.
	 * @param name name of the model
	 * /
	public synchronized void removeBpmnModel(String name)
	{
		bpmnModels.remove(name);
	}*/
	
	/**
	 * Adds a GPMN model.
	 * @param name name of the model
	 * @param path path to the model
	 * /
	public synchronized void addGpmnModel(String name, String path)
	{
		gpmnModels.put(name, path);
	}*/
	
	/**
	 * Removes a GPMN model.
	 * @param name name of the model
	 * /
	public synchronized void removeGpmnModel(String name)
	{
		gpmnModels.remove(name);
	}*/
	
	/**
	 * Gets a BPMN model.
	 * @param name name of the model
	 * @return the model
	 * /
	public synchronized MBpmnModel getBpmnModel(String name)
	{
		return (MBpmnModel) bpmnModels.get(name);
	}*/
	
	/**
	 * Gets all available BPMN models.
	 * @return names of all BPMN models
	 * /
	public synchronized Set getBpmnModelNames()
	{
		return new HashSet(bpmnModels.keySet());
	}*/
	
	/**
	 * Gets a GPMN model.
	 * @param name name of the model
	 * @return the model
	 * /
	public synchronized MGpmnModel getGpmnModel(String name)
	{
		MGpmnModel model = null;
		try
		{
			model = GpmnXMLReader.read(getGpmnModelPath(name), null, null);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return model;
	}
	
	/**
	 * Gets a GPMN model path.
	 * @param name name of the model
	 * @return path to the model
	 * /
	public synchronized String getGpmnModelPath(String name)
	{
		return (String) gpmnModels.get(name);
	}*/
	
	/**
	 * Gets all available GPMN models.
	 * @return names of all GPMN models
	 * /
	public synchronized Set getGpmnModelNames()
	{
		return new HashSet(gpmnModels.keySet());
	}*/
	
	/**
	 *  Add a process model.
	 *  @param client The client.
	 *  @param name The name of the model.
	 *  @param path The path to the model.
	 */
	public void addProcessModel(String filename)
	{
		// todo check access
		
		ILoadableComponentModel model = SComponentFactory.loadModel(container, filename);
		String modelName = model.getName();
		if (modelName == null)
		{
			modelName = model.getFilename();
			modelName = modelName.substring(Math.max(modelName.lastIndexOf('/'), modelName.lastIndexOf(File.separator)) + 1);
		}
		models.put(modelName, model);
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
		return models.keySet();
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
}
