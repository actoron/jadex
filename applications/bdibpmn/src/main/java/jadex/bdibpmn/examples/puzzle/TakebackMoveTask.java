package jadex.bdibpmn.examples.puzzle;

import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;
import jadex.bridge.IInternalAccess;

/**
 *  Take back a move on the board.
 */
public class TakebackMoveTask extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, IInternalAccess instance)
	{
		IBoard	board	= (IBoard)context.getParameterValue("board");
		board.takeback();

//		BpmnExecutor exe = new BpmnExecutor((BpmnInstance) instance, true);
//		ExecutionControlPanel.createBpmnFrame(instance.getModelElement().getName()+": "+context.getModelElement().getName(), (BpmnInstance) instance, exe);	
	}
}
