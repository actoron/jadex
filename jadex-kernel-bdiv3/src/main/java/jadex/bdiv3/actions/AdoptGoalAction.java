package jadex.bdiv3.actions;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.runtime.BDIAgentInterpreter;
import jadex.bdiv3.runtime.RGoal;
import jadex.bridge.IConditionalComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
public class AdoptGoalAction implements IConditionalComponentStep<Void>
{
	/** The goal. */
	protected RGoal goal;
	
	/**
	 *  Create a new action.
	 */
	public AdoptGoalAction(RGoal goal)
	{
		this.goal = goal;
	}
	
	/**
	 *  Test if the action is valid.
	 *  @return True, if action is valid.
	 */
	public boolean isValid()
	{
		return RGoal.GOALLIFECYCLESTATE_NEW.equals(goal.getLifecycleState());
	}
	
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public IFuture<Void> execute(IInternalAccess ia)
	{
		Future<Void> ret = new Future<Void>();
		try
		{
			BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
			// todo: observe class and goal itself!
//			goal.observeGoal(ia);
//			ip.getCapability().addGoal(goal);
			goal.setLifecycleState(ia, RGoal.GOALLIFECYCLESTATE_ADOPTED);
			ret.setResult(null);
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		return ret;
	}
}
