package jadex.bdibpmn.examples.puzzle;

import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;
import jadex.bridge.IInternalAccess;

/**
 *  Make a move on the board.
 */
public class MakeMoveTask extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, IInternalAccess instance)
	{
		IBoard	board	= (IBoard)context.getParameterValue("board");
		Move	move	= (Move)context.getParameterValue("move");
		board.move(move);
	}
}
