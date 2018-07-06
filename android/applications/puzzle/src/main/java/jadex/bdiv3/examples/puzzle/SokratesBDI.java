package jadex.bdiv3.examples.puzzle;

import jadex.android.puzzle.SokratesService.SokratesListener;
import jadex.android.puzzle.ui.GuiProxy;
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
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *  Puzzle agent tries to solve a solitair board game
 *  by recursiveky applying means-end-reasoning.
 */
@Agent
//@Arguments(
//{
//	@Argument(name="gui_listener", clazz=SokratesListener.class, description="Listener for the puzzle gui")
//})
@BDIConfigurations(
{
	@BDIConfiguration(name=MoveComparator.STRATEGY_SAME_LONG),
	@BDIConfiguration(name=MoveComparator.STRATEGY_DEFAULT),
	@BDIConfiguration(name=MoveComparator.STRATEGY_LONG),
	@BDIConfiguration(name=MoveComparator.STRATEGY_ALTER_LONG)
})
public class SokratesBDI
{
	//-------- attributes --------
	
	/** The puzzle board. */
	@Belief
	protected IBoard	board	= new JackBoard();
	
	@AgentArgument
	protected SokratesListener	gui_listener;
	
	@Belief
	protected GuiProxy gui_proxy;
	
	/** The number of tried moves. */
	protected int	triescnt;
	
	/** The depth of the current move. */
	protected int	depth;
	
	/** The delay between two moves (in milliseconds). */
	@AgentArgument
	protected long	delay;
	
	/** The strategy (none=choose the first applicable, long=prefer jump moves,
	 * same_long=prefer long moves of same color, alter_long=prefer long move of alternate color). */
	protected String strategy; // = MoveComparator.STRATEGY_SAME_LONG;
	
	//-------- methods --------
	
	public SokratesBDI()
	{
		super();
	}

	/**
	 *  Setup the gui and start playing.
	 */
	@AgentBody
	public IFuture<Void>	body(IInternalAccess agent)
	{
		gui_proxy = new GuiProxy(board, gui_listener);
		final Future<Void>	ret	= new Future<Void>();

		strategy = agent.getConfiguration();
		createGui(agent);
		
		gui_proxy.showMessage("Now puzzling:");
		final long	start	= System.currentTimeMillis();
		IFuture<MoveGoal> fut = agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new MoveGoal());
		fut.addResultListener(new IResultListener<MoveGoal>()
		{
			public void resultAvailable(MoveGoal movegoal)
			{
				long end = System.currentTimeMillis();
				gui_proxy.showMessage("Needed: "+(end-start)+" millis for " + triescnt + "moves");
				ret.setResult(null);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				gui_proxy.showMessage("No solution found :-(");
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Create the GUI (if any).
	 */
	protected void	createGui(final IInternalAccess agent)
	{
//		SwingUtilities.invokeLater(new Runnable()
//		{
//			public void run()
//			{
//				new BoardGui(agent.getExternalAccess(), board);
//			}
//		});
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
		@GoalTargetCondition(beliefs="board")
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
		public IFuture<Void>	move(final IPlan plan)
		{
			final Future<Void>	ret	= new Future<Void>();
			
			triescnt++;
			print("Trying "+move+" ("+triescnt+") ", depth);
			depth++;
			board.move(move);
					
			if(delay>0)
			{
				plan.waitFor(delay)
					.addResultListener(new DelegationResultListener<Void>(ret)
				{
					public void customResultAvailable(Void result)
					{
						IFuture<MoveGoal> fut = plan.dispatchSubgoal(new MoveGoal());
						fut.addResultListener(new ExceptionDelegationResultListener<MoveGoal, Void>(ret)
						{
							public void customResultAvailable(MoveGoal result)
							{
								ret.setResult(null);
							}
						});
					}
				});
			}
			else
			{
				IFuture<MoveGoal> fut = plan.dispatchSubgoal(new MoveGoal());
				fut.addResultListener(new ExceptionDelegationResultListener<MoveGoal, Void>(ret)
				{
					public void customResultAvailable(MoveGoal result)
					{
						ret.setResult(null);
					}
				});
			}
			
			return ret;
		}
		
		/**
		 *  The plan failure code.
		 */
		@PlanFailed
		public IFuture<Void> failed(IPlan plan)
		{
			assert board.getLastMove().equals(move): "Tries to takeback wrong move.";
			
			Future<Void>	ret	= new Future<Void>();
			
			depth--;
			print("Failed "+move, depth);
			board.takeback();
			if(delay>0)
			{
				plan.waitFor(delay).addResultListener(new DelegationResultListener<Void>(ret));
			}
			else
			{
				ret.setResult(null);
			}
			
			return ret;
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
		StringBuilder sb = new StringBuilder();
        for(int x=0; x<indent; x++)
            sb.append(" ");
        sb.append(text);
        
        gui_proxy.showMessage(sb.toString());
    }
}
