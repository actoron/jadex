package jadex.bpmn.runtime.task;

import jadex.bpmn.runtime.IProcessInstance;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.concurrent.IResultListener;

import javax.swing.SwingUtilities;

import com.daimler.client.connector.ClientConnector;
import com.daimler.client.connector.ClientRequest;

public class ShowClientInfoTask implements ITask
{
	/**
	 *  Execute the task.
	 *  @param context	The accessible values.
	 *  @param instance	The process instance executing the task.
	 *  @listener	To be notified, when the task has completed.
	 */
	public void execute(final ITaskContext context, IProcessInstance instance, final IResultListener listener)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if (context.getParameterValue("info_text") == null)
					context.setParameterValue("info_text", "<html><center><h1>No Text</h1></center></html");
				ClientConnector.getInstance().dispatchRequest(new ClientRequest(context, listener));
			}
		});
	}
}