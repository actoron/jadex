package jadex.wfms.client.task;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.IProcessInstance;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;
import jadex.wfms.client.Workitem;
import jadex.wfms.service.client.IWorkitemQueueService;

public class ShowClientInfoTask extends AbstractClientTask
{
	/**
	 *  Execute the task.
	 *  @param context	The accessible values.
	 *  @param instance	The process instance executing the task.
	 *  @listener	To be notified, when the task has completed.
	 */
	public void execute(final ITaskContext context, IProcessInstance instance, final IResultListener listener)
	{
		IServiceContainer wfms = ((BpmnInterpreter)instance).getComponentAdapter().getServiceContainer();
		IWorkitemQueueService wiq = (IWorkitemQueueService) wfms.getService(IWorkitemQueueService.class);
		wiq.queueWorkitem(createWorkitem(Workitem.TEXT_INFO_WORKITEM_TYPE, context, listener));
	}
}