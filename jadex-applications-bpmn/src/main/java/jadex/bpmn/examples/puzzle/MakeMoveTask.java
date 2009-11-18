package jadex.bpmn.examples.puzzle;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;

/**
 *  Make a move on the board.
 */
public class MakeMoveTask extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, BpmnInterpreter instance)
	{
		IBoard	board	= (IBoard)context.getParameterValue("board");
		Move	move	= (Move)context.getParameterValue("move");
		board.move(move);
	}
}
