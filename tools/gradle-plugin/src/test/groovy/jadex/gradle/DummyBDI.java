package jadex.gradle;

import java.util.ArrayList;
import java.util.List;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalAPLBuild;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanFailed;
import jadex.bdiv3.annotation.PlanPassed;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;

@Agent
public class DummyBDI
{
	//-------- attributes --------

	/** The puzzle board. */
	@Belief
	protected String	board	= new String();

	@AgentArgument
	protected String	gui_listener;

	//-------- methods --------

	public DummyBDI()
	{
		super();
	}

	/**
	 *  Setup the gui and start playing.
	 */
	@AgentBody
	public IFuture<Void>	body(IInternalAccess agent)
	{
		final Future<Void>	ret	= new Future<Void>();
		ret.setResult(null);
		return ret;
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
			return !board.isEmpty();
		}
		
		/**
		 *  Build plan candidates for all possible moves.
		 *  Sorts moves according to strategy.
		 */
		@GoalAPLBuild
		public List<String>	buildAPL()
		{
			List<String>	ret	= new ArrayList<String>();

			ret.add("1");
			ret.add("2");

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

		/**
		 *  Create a move plan-
		 */
		public MovePlan() {}
		
		//-------- methods --------
		
		/**
		 *  The plan body.
		 */
		@PlanBody
		public IFuture<Void>	move(final IPlan plan)
		{
			final Future<Void>	ret	= new Future<Void>();

			ret.setResult(null);
			
			return ret;
		}
		
		/**
		 *  The plan failure code.
		 */
		@PlanFailed
		public IFuture<Void> failed(IPlan plan)
		{
			final Future<Void>	ret	= new Future<Void>();
			ret.setResult(null);
			return ret;
		}

		/**
		 *  The plan passed code.
		 */
		@PlanPassed
		public void passed()
		{
		}
	}

}
