package jadex.bpmn.examples.puzzle;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;

/**
 *  Take back a move on the board.
 */
public class TakebackMoveTask extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, BpmnInterpreter instance)
	{
		IBoard	board	= (IBoard)context.getParameterValue("board");
		board.takeback();

//		BpmnExecutor exe = new BpmnExecutor((BpmnInstance) instance, true);
//		ExecutionControlPanel.createBpmnFrame(instance.getModelElement().getName()+": "+context.getModelElement().getName(), (BpmnInstance) instance, exe);	
	}
}
