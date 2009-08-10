package jadex.wfms;

import jadex.bpmn.BpmnExecutor;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.runtime.BpmnInstance;
import jadex.wfms.service.IModelRepositoryService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 *  Basic Wfms implementation.
 */
public class BasicWfms implements IWfms
{
	/** Running BPMN processes */
	private List bpmnProcesses;
	
	/** Wfms services */
	private Map services;
	
	/** Initializes the Wfms */
	public BasicWfms()
	{
		services = new HashMap();
		bpmnProcesses = new LinkedList();
	}
	
	/**
	 * Starts a BPMN process
	 * @param name name of the BPMN model
	 * @param stepmode if true, the process will start in step mode
	 */
	public synchronized void startBpmnProcess(String name, boolean stepmode)
	{
		IModelRepositoryService mr = (IModelRepositoryService) getService(IModelRepositoryService.class);
		MBpmnModel model = mr.getBpmnModel(name);
		BpmnInstance instance = new BpmnInstance(model);
		instance.setWfms(this);
		new BpmnExecutor(instance, stepmode);
		bpmnProcesses.add(instance);
	}
	
	/**
	 *  Add a service to the Wfms.
	 *  @param type type of service
	 *  @param service The service.
	 */
	public synchronized void addService(Class type, Object service)
	{
		services.put(type, service);
	}
	
	/**
	 *  Removes a service from the Wfms.
	 *  @param type type of service
	 */
	public synchronized void removeService(Class type)
	{
		services.remove(type);
	}
	
	/**
	 *  Get a Wfms-service.
	 *  @param type The service interface/type.
	 *  @return The corresponding Wfms-service.
	 */
	public synchronized Object getService(Class type)
	{
		return services.get(type);
	}
}
