package jadex.bdiv3.actions;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.runtime.BDIAgentInterpreter;
import jadex.bdiv3.runtime.RGoal;
import jadex.bdiv3.runtime.RPlan;
import jadex.bridge.IConditionalComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

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
		return true;
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
			goal.unobserveGoal(ia);
			ip.getCapability().removeGoal(goal);
			goal.setLifecycleState(ia, RGoal.GOALLIFECYCLESTATE_DROPPED);
			ret.setResult(null);
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		return ret;
	}
}
