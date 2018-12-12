package jadex.bdiv3.actions;

import jadex.bdiv3.features.impl.IInternalBDIAgentFeature;
import jadex.bdiv3.runtime.impl.RGoal;
import jadex.bridge.IConditionalComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 * 
 */
public class DropGoalAction implements IConditionalComponentStep<Void>
{
	/** The goal. */
	protected RGoal goal;
	
	/**
	 *  Create a new action.
	 */
	public DropGoalAction(RGoal goal)
	{
		this.goal = goal;
	}
	
	/**
	 *  Test if the action is valid.
	 *  @return True, if action is valid.
	 */
	public boolean isValid()
	{
		return RGoal.GoalLifecycleState.DROPPING.equals(goal.getLifecycleState());
	}
	
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public IFuture<Void> execute(final IInternalAccess ia)
	{
		final Future<Void> ret = new Future<Void>();
//		BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
//		goal.unobserveGoal(ia);
		
		goal.callFinishedMethod().addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				cont();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				cont();
			}
			
			protected void cont()
			{
				ia.getFeature(IInternalBDIAgentFeature.class).getCapability().removeGoal(goal);
				goal.setLifecycleState(ia, RGoal.GoalLifecycleState.DROPPED);
				ret.setResult(null);
			}
		});
		
		return ret;
	}
}
