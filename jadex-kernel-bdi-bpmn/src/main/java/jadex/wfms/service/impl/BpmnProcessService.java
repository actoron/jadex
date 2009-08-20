package jadex.wfms.service.impl;

import java.util.HashMap;
import java.util.Map;

import jadex.bpmn.BpmnExecutor;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.runtime.BpmnInstance;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.wfms.IWfms;
import jadex.wfms.service.IBpmnProcessService;
import jadex.wfms.service.IModelRepositoryService;

public class BpmnProcessService implements IBpmnProcessService
{
	/** The WFMS */
	private IWfms wfms;
	
	/** Running Bpmn process instances */
	private Map processes;
	
	/** Counter for instance names */
	private long instanceCounter;
	
	public BpmnProcessService(IWfms wfms)
	{
		this.instanceCounter = 0;
		this.processes = new HashMap();
		this.wfms = wfms;
	}
	
	/**
	 * Starts a BPMN process
	 * @param name name of the BPMN model
	 * @param stepmode if true, the process will start in step mode
	 * @return instance name
	 */
	public synchronized String startProcess(String name, boolean stepmode)
	{
		IModelRepositoryService mr = (IModelRepositoryService) wfms.getService(IModelRepositoryService.class);
		MBpmnModel model = mr.getBpmnModel(name);
		final BpmnInstance instance = new BpmnInstance(model);
		instance.setWfms(wfms);
		BpmnExecutor executor = new BpmnExecutor(instance, true);
		String tmpInstanceName = null;
		do
			tmpInstanceName = instance.getModelElement().getId() + "_" + String.valueOf(++instanceCounter);
		while (processes.containsKey(tmpInstanceName));
		final String instanceName = tmpInstanceName;
		processes.put(instanceName, executor);
		instance.addChangeListener(new IChangeListener()
		{
			public void changeOccurred(ChangeEvent event)
			{
				if (instance.isFinished(null, null))
				{
					synchronized (BpmnProcessService.this)
					{
						processes.remove(instanceName);
					}
				}
			}
		});
		executor.setStepmode(stepmode);
		return instanceName;
	}
}
