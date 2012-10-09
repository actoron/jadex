package jadex.bdiv3.actions;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.runtime.BDIAgentInterpreter;
import jadex.bdiv3.runtime.RGoal;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
public class AdoptGoalAction implements IAction<Void>
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
	public IFuture<Boolean> isValid()
	{
		return new Future<Boolean>(true);
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
			ip.getRuleSystem().observeObject(goal.getPojoElement());
			ia.getExternalAccess().scheduleStep(new FindApplicableCandidatesAction(goal));
			ret.setResult(null);
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		return ret;
	}
}
