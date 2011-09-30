package jadex.bpmn.tutorial;

import jadex.bpmn.annotation.Task;
import jadex.bpmn.annotation.TaskParameter;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;

import javax.swing.JOptionPane;

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
	public void doExecute(ITaskContext context, BpmnInterpreter instance)
	{
		String	message	= (String)context.getParameterValue("message");
		String	title	= (String)context.getParameterValue("title");
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
}
