package jadex.web.examples.puzzle.agent;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.web.examples.puzzle.Board;
import jadex.web.examples.puzzle.Move;


/**
 *  Make a move and dispatch a subgoal for the next.
 */
public class MovePlan extends Plan
{
	//-------- attributes --------

	/** The move to try. */
	protected Move move;

	/** The board. */
	protected Board board;

	//-------- constrcutors --------

	/**
	 *  Create a new move plan.
	 */
	public MovePlan()
	{
		this.move = (Move)getParameter("move").getValue();
		this.board = (Board)getParameter("board").getValue();
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		// Make the move.
		board.move(move);
		
		IGoal	goal	= (IGoal)getReason();
		System.out.println("deadline: "+(((Long)goal.getParameter("deadline").getValue()).longValue() - getScope().getTime()));
		
		// Plan will be aborted when board is solution,
		// otherwise continue with next move
		IGoal mm = createGoal("makemove");
		mm.getParameter("board").setValue(board);
		dispatchSubgoalAndWait(mm);
	}

	/**
	 *  The plan failure code.
	 */
	public void failed()
	{
		board.takeback();
	}
}
