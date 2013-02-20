package jadex.bdiv3.actions;

import java.lang.reflect.Field;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.GoalAPI;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.runtime.impl.BDIAgentInterpreter;
import jadex.bdiv3.runtime.impl.RGoal;
import jadex.bdiv3.runtime.impl.RProcessableElement;
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
		return RGoal.GoalLifecycleState.NEW.equals(goal.getLifecycleState());
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
			
			// inject goal elements
			Class<?> cl = goal.getPojoElement().getClass();
			Field[] fields = cl.getDeclaredFields();
			for(Field f: fields)
			{
				if(f.isAnnotationPresent(GoalAPI.class))
				{
					f.setAccessible(true);
					f.set(goal.getPojoElement(), goal);
				}
			}
			
			ip.getCapability().addGoal(goal);
			goal.setLifecycleState(ia, RGoal.GoalLifecycleState.ADOPTED);
			ret.setResult(null);
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		return ret;
	}
}
