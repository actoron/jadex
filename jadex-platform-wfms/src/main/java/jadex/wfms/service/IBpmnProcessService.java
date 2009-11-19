package jadex.wfms.service;

public interface IBpmnProcessService
{
	/**
	 * Starts a BPMN process
	 * @param name name of the BPMN model
	 * @param stepmode if true, the process will start in step mode
	 * @return instance name
	 */
	public String startProcess(String name, boolean stepmode);

}
