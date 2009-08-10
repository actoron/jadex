package jadex.wfms.client.task;

import jadex.bpmn.runtime.BpmnInstance;
import jadex.bpmn.runtime.IProcessInstance;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.concurrent.IResultListener;
import jadex.wfms.IWfms;
import jadex.wfms.client.Workitem;
import jadex.wfms.service.IWorkitemQueueService;

import javax.swing.SwingUtilities;

import com.daimler.client.connector.ClientConnector;
import com.daimler.client.connector.UserNotification;

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
		if (context.getParameterValue("info_text") == null)
			context.setParameterValue("info_text", "<html><center><h1>" + context.getModelElement().getName() + "</h1></center></html>");
		IWfms wfms = ((BpmnInstance) instance).getWfms();
		IWorkitemQueueService wiq = (IWorkitemQueueService) wfms.getService(IWorkitemQueueService.class);
		wiq.queueWorkitem(createWorkitem(Workitem.TEXT_INFO_WORKITEM_TYPE, context, listener));
	}
}