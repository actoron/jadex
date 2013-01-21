package jadex.bdiv3.example.puzzle;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalAPLBuild;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.PlanCandidate;
import jadex.bdiv3.runtime.RPlan;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;

@Agent
public class SokratesBDI
{
	//-------- attributes --------
	
	/** The puzzle board. */
	@Belief
	protected IBoard	board	= new Board();
	
	/** The number of tried moves. */
	protected int	triescnt;
	
	/** The depth of the current move. */
	protected int	depth;
	
	/** The delay between two moves (in milliseconds). */
	protected long	delay	= 500;
	
	/** The strategy (none=choose the first applicable, long=prefer jump moves,
	 * same_long=prefer long moves of same color, alter_long=prefer long move of alternate color). */
	protected String	strategy	= MoveComparator.STRATEGY_SAME_LONG;
	
	//-------- methods --------
	
	/**
	 *  Setup the gui and start playing.
	 */
	@AgentBody
	public IFuture<Void>	body(BDIAgent agent)
	{
		final Future<Void>	ret	= new Future<Void>();

		createGui(agent);
		
		System.out.println("Now puzzling:");
		final long	start	= System.currentTimeMillis();
//		final long	startmem	= Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		agent.dispatchTopLevelGoal(new MoveGoal())
			.addResultListener(new IResultListener<MoveGoal>()
		{
			public void resultAvailable(MoveGoal movegoal)
			{
				long end = System.currentTimeMillis();
				System.out.println("Needed: "+(end-start)+" millis.");
				
//				if(getBeliefbase().containsBelief("endmem"))
//				{
//					Long	endmem	= (Long) getBeliefbase().getBelief("endmem").getFact();
//					if(endmem!=null)
//					{
//						System.out.println("Needed: "+(((endmem.longValue()-startmem)*10/1024)/1024)/10.0+" Mb.");
//					}
//				}
				
				ret.setResult(null);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("No solution found :-(");
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Create the GUI (if any).
	 */
	protected void	createGui(final BDIAgent agent)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				new BoardGui(agent.getExternalAccess(), board);
			}
		});
	}
	
	//-------- goals --------
	
	/**
	 *  The goal to make moves until the board reaches a solution.
	 */
	@Goal
	public class MoveGoal
	{
		/**
		 *  Move goal is successful when resulting board represents a solution.
		 */
		@GoalTargetCondition(events="board")
		public boolean	isAchieved()
		{
			return board.isSolution();
		}
		
		/**
		 *  Build plan candidates for all possible moves.
		 *  Sorts moves according to strategy.
		 */
		@GoalAPLBuild
		public List<PlanCandidate>	buildAPL()
		{
			List<PlanCandidate>	ret	= new ArrayList<PlanCandidate>();
			List<Move>	moves	= board.getPossibleMoves();
			Collections.sort(moves, new MoveComparator(board, strategy));
			
			for(Move move: moves)
			{
				ret.add(new PlanCandidate("move", SUtil.createHashMap(new String[]{"move"}, new Object[]{move})));
			}
		
			return ret;
		}
	}
	
	//-------- plans --------
	
	/**
	 *  Plan to make a move.
	 */
	@Plan(trigger=@Trigger(goals=MoveGoal.class))
	public IFuture<Void>	move(final RPlan rplan)
//	public IFuture<Void>	doMove(Move move)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		final Move	move	= (Move)((PlanCandidate)rplan.getCandidate()).getParameters().get("move");
		triescnt++;
		print("Trying "+move+" ("+triescnt+") ", depth);
		depth++;
		board.move(move);
				
		rplan.waitFor(delay)
			.addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				rplan.dispatchSubgoal(new MoveGoal())
					.addResultListener(new ExceptionDelegationResultListener<MoveGoal, Void>(ret)
				{
					public void customResultAvailable(MoveGoal result)
					{
						ret.setResult(null);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						print("Failed "+move, depth);
						board.takeback();
						depth--;
						rplan.waitFor(delay).addResultListener(new DelegationResultListener<Void>(ret));
					}
				});
			}
		});
		
		return ret;
	}


	/**
	 *  Print out an indented string.
	 *  @param text The text.
	 *  @param indent The number of cols to indent.
	 */
	protected void print(String text, int indent)
    {
        for(int x=0; x<indent; x++)
            System.out.print(" ");
        System.out.println(text);
    }
}
