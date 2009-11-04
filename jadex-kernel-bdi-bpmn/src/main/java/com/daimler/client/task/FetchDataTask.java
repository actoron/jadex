package com.daimler.client.task;

import jadex.bpmn.runtime.IProcessInstance;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.concurrent.IResultListener;

import javax.swing.SwingUtilities;

import com.daimler.client.connector.ClientConnector;
import com.daimler.client.connector.UserNotification;

public class FetchDataTask implements ITask
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
				ClientConnector.getInstance().queueNotification(new UserNotification(UserNotification.DATA_FETCH_NOTIFICATION_TYPE, context, listener));
			}
		});
	}
}