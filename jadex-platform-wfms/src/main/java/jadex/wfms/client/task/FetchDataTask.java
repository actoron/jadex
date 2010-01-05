package jadex.wfms.client.task;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;
import jadex.wfms.client.Workitem;
import jadex.wfms.service.IWfmsClientService;

public class FetchDataTask extends AbstractClientTask
{
	/**
	 *  Execute the task.
	 *  @param context	The accessible values.
	 *  @param process	The process instance executing the task.
	 *  @listener	To be notified, when the task has completed.
	 */
	public void execute(ITaskContext context, BpmnInterpreter process, IResultListener listener)
	{
		IServiceContainer wfms = (IServiceContainer) process.getComponentAdapter().getServiceContainer(); 
		IWfmsClientService wiq = (IWfmsClientService) wfms.getService(IWfmsClientService.class);
		wiq.queueWorkitem(createWorkitem(Workitem.DATA_FETCH_WORKITEM_TYPE, context), createRedirListener(context, listener));
	}
}