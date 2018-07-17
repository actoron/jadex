package jadex.bdiv3.examples.puzzle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.BDIConfiguration;
import jadex.bdiv3.annotation.BDIConfigurations;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalAPLBuild;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanFailed;
import jadex.bdiv3.annotation.PlanPassed;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IInternalAccess;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 *  Puzzle agent tries to solve a solitair board game
 *  by recursiveky applying means-end-reasoning.
 */
@Agent(type=BDIAgentFactory.TYPE)
@BDIConfigurations(
{
	@BDIConfiguration(name=MoveComparator.STRATEGY_SAME_LONG),	// Best strategy first.
	@BDIConfiguration(name=MoveComparator.STRATEGY_NONE),
	@BDIConfiguration(name=MoveComparator.STRATEGY_LONG),
	@BDIConfiguration(name=MoveComparator.STRATEGY_ALTER_LONG)
})
public class BlockingSokratesAgent
{
	//-------- attributes --------
	
	/** The puzzle board. */
	@Belief
	protected IBoard	board	= new JackBoard();
	
	/** The number of tried moves. */
	protected int	triescnt;
	
	/** The depth of the current move. */
	protected int	depth;
	
	/** The delay between two moves (in milliseconds). */
	protected long	delay	= 500;
	
	/** The strategy (none=choose the first applicable, long=prefer jump moves,
	 * same_long=prefer long moves of same color, alter_long=prefer long move of alternate color). */
	protected String strategy;
	
	//-------- methods --------
	
	/**
	 *  Setup the gui and start playing.
	 */
	@AgentBody//(keepalive=false)
	public void	body(IInternalAccess agent)
	{
		strategy = agent.getConfiguration();
		createGui(agent);
		
		try
		{
			System.out.println("Now puzzling:");
			final long	start	= System.currentTimeMillis();
			
			agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new MoveGoal()).get();
			
			long end = System.currentTimeMillis();
			System.out.println("Needed: "+(end-start)+" millis.");
		}
		catch(Exception e)
		{
			System.out.println("No solution found :-(");
		}
	}
	
	/**
	 *  Create the GUI (if any).
	 */
	protected void	createGui(final IInternalAccess agent)
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
		@GoalTargetCondition//(beliefs="board")
		public boolean	isAchieved()
		{
			return board.isSolution();
		}
		
		/**
		 *  Build plan candidates for all possible moves.
		 *  Sorts moves according to strategy.
		 */
		@GoalAPLBuild
		public List<MovePlan>	buildAPL()
		{
			List<MovePlan>	ret	= new ArrayList<MovePlan>();
			List<Move>	moves	= board.getPossibleMoves();
			Collections.sort(moves, new MoveComparator(board, strategy));
			
			for(Move move: moves)
			{
				ret.add(new MovePlan(move));
			}
		
			return ret;
		}
	}
	
	//-------- plans --------
	
	/**
	 *  Plan to make a move.
	 */
	@Plan(trigger=@Trigger(goals=MoveGoal.class))
	public class MovePlan
	{
		//-------- attributes --------
		
		/** The move. */
		protected Move	move;
		
		//-------- constructors --------
		
		/**
		 *  Create a move plan-
		 */
		public MovePlan(Move move)
		{
			this.move	= move;
		}
		
		//-------- methods --------
		
		/**
		 *  The plan body.
		 */
		@PlanBody
		public void	move(final IPlan plan)
		{
			triescnt++;
			print("Trying "+move+" ("+triescnt+") ", depth);
			depth++;
			board.move(move);
					
			if(delay>0)
			{
				plan.waitFor(delay).get();
			}
				
			plan.dispatchSubgoal(new MoveGoal()).get();
		}
		
		/**
		 *  The plan failure code.
		 */
		@PlanFailed
		public void failed(IPlan plan)
		{
			assert board.getLastMove().equals(move): "Tries to takeback wrong move.";
			
			depth--;
			print("Failed "+move, depth);
			board.takeback();
			if(delay>0)
			{
				plan.waitFor(delay).get();
			}
		}

		/**
		 *  The plan passed code.
		 */
		@PlanPassed
		public void passed()
		{
			depth--;
			print("Succeeded "+move, depth);
		}
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
