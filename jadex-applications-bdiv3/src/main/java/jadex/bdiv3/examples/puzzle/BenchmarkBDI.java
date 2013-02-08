package jadex.bdiv3.examples.puzzle;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.impl.PlanCandidate;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;

@Agent
public class BenchmarkBDI extends SokratesBDI
{
	/**
	 *  Overwrite default strategy
	 */
	public BenchmarkBDI()
	{
		strategy	= MoveComparator.STRATEGY_ALTER_LONG;
	}

	/**
	 *  Overridden to skip gui creation.	
	 */
	protected void createGui(BDIAgent agent)
	{
	}
	
	/**
	 *  Plan to make a move.
	 *  Overridden to remove waits. 
	 */
	@Plan(trigger=@Trigger(goals=MoveGoal.class))
	public IFuture<Void> move(final RPlan rplan)
//	public IFuture<Void>	doMove(Move move)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		final Move	move	= (Move)((PlanCandidate)rplan.getCandidate()).getParameters().get("move");
		triescnt++;
		print("Trying "+move+" ("+triescnt+") ", depth);
		depth++;
		board.move(move);
				
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
				ret.setResult(null);
			}
		});
		
		return ret;
	}
}
