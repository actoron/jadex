package jadex.bpmn.tutorial;

import javax.swing.JOptionPane;

import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskParameter;
import jadex.bpmn.runtime.task.AbstractTask;
import jadex.bridge.IInternalAccess;

/**
 *  A task that displays a message using a
 *  JOptionPane.
 */
@Task(description="A task that displays a message using a JOptionPane.", parameters={
	@TaskParameter(name="message", clazz=String.class, direction=TaskParameter.DIRECTION_IN, description="The message to be shown."),
	@TaskParameter(name="title", clazz=String.class, direction=TaskParameter.DIRECTION_IN, description="The title of the dialog.")
})
public class OKTask extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, IInternalAccess instance)
	{
		String	message	= (String)context.getParameterValue("message");
		String	title	= (String)context.getParameterValue("title");
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
}
