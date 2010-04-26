package jadex.bpmn.tutorial;

import javax.swing.JOptionPane;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;

/**
 *  A task that displays a message using a
 *  JOptionPane.
 */
public class OKTask extends AbstractTask
{
	public void doExecute(ITaskContext context, BpmnInterpreter instance)
	{
		String	message	= (String)context.getParameterValue("message");
		String	title	= (String)context.getParameterValue("title");
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
}
