package jadex.web.examples.puzzle.agent;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.web.examples.puzzle.Board;
import jadex.web.examples.puzzle.IPuzzleService;
import jadex.web.examples.puzzle.Move;
/**
 *  Implementation of the puzzle service.
 */
@Service
public class PuzzleService implements IPuzzleService
{
	//-------- attributes --------
	
	/** The agent to which the service belongs. */
	@ServiceComponent
	protected IBDIInternalAccess	agent;
	
	//-------- IPuzzleService interface --------
	
	/**
	 *  Solve the game and give a hint on the next move.
	 *  @param board	The current board state.
	 *  @return The tile to move next.
	 *  @throws Exception in future, when puzzle can not be solved.
	 */
	public IFuture<Move> hint(Board board)
	{
		final int depth	= board.getMoves().size();
		final Future<Move>	ret	= new Future<Move>();
		final Board	clone	= (Board)board.clone();
		final IGoal	goal	= agent.getGoalbase().createGoal("makemove");
		goal.getParameter("board").setValue(board);
		goal.addGoalListener(new IGoalListener()
		{
			public void goalFinished(AgentEvent ae)
			{
				if(clone.isSolution())
				{
					ret.setResult(clone.getMoves().get(depth));
				}
			}
			
			public void goalAdded(AgentEvent ae)
			{
				// ignore
			}
		});
		agent.getGoalbase().dispatchTopLevelGoal(goal);
		return ret;
	}
}
