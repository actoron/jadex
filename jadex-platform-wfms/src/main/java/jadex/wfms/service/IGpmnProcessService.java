package jadex.wfms.service;

public interface IGpmnProcessService
{
	/**
	 * Starts a Gpmn process
	 * @param name name of the Gpmn model
	 * @param stepmode if true, the process will start in step mode
	 * @return instance name
	 */
	public String startProcess(String name);
}
