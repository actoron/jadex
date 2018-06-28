package jadex.bdibpmn.examples.puzzle;

import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;
import jadex.bridge.IInternalAccess;

/**
 *  Print out some text stored in variable test.
 */
public class PuzzlePrintTask extends AbstractTask
{
	/**
	 * 
	 */
	public void doExecute(ITaskContext context, IInternalAccess instance)
	{
		int	indent	= ((Number)context.getParameterValue("indent")).intValue();
        for(int x=0; x<indent; x++)
            System.out.print(" ");

		String text = (String)context.getParameterValue("text");
		System.out.println(text);
	}
}
