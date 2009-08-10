package jadex.wfms;

/**
 * The Workflow Management System interface.
 */
public interface IWfms
{
	/**
	 * Starts a BPMN process
	 * @param name name of the BPMN model
	 * @param stepmode if true, the process will start in step mode
	 */
	public void startBpmnProcess(String name, boolean stepmode);
	
	/**
	 *  Get a Wfms-service.
	 *  @param type The service interface/type.
	 *  @return The corresponding Wfms-service.
	 */
	public Object getService(Class type);
}
