package jadex.bpmn.tutorial;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;
import jadex.bpmn.runtime.task.ParameterMetaInfo;
import jadex.bpmn.runtime.task.TaskMetaInfo;

import javax.swing.JOptionPane;

/**
 *  A task that displays a message using a
 *  JOptionPane.
 */
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

	//-------- static methods --------
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "A task that displays a message using a JOptionPane.";
		ParameterMetaInfo pmi1	= new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
				String.class, "message", null, "The message to be shown.");
		ParameterMetaInfo pmi2	= new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
				String.class, "title", null, "The title of the dialog.");
		
		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{pmi1, pmi2}); 
	}
}
