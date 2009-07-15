package jadex.bpmn.runtime;

import jadex.bpmn.model.MBpmnModel;

/**
 *  Interface for process instances.
 *  Contains publically accessible methods of running processes.
 */
public interface IProcessInstance
{
	/**
	 *  Get the BPMN model of the process instance.
	 *  @return The BPMN model
	 */
	public MBpmnModel	getModelElement();
}
