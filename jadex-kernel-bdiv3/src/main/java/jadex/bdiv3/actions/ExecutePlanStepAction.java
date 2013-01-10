package jadex.bdiv3.actions;

import jadex.bdiv3.runtime.IPlanBody;
import jadex.bdiv3.runtime.RGoal;
import jadex.bdiv3.runtime.RPlan;
import jadex.bdiv3.runtime.RProcessableElement;
import jadex.bridge.IConditionalComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

// todo: use IPlan (and plan executor abstract to be able to execute plans as subcomponents)
// todo: allow using multi-step plans

/**
 * 
 */
public class ExecutePlanStepAction implements IConditionalComponentStep<Void>
{
	/** The plan. */
	protected RPlan rplan;
	
	/**
	 *  Create a new action.
	 */
	public ExecutePlanStepAction(RPlan rplan)
	{
//		this.element = element;
		this.rplan = rplan;
	}
	
	/**
	 *  Test if the action is valid.
	 *  @return True, if action is valid.
	 */
	public boolean isValid()
	{
		boolean ret = true;
		
		Object element = rplan.getReason();
		if(element instanceof RGoal)
		{
			RGoal rgoal = (RGoal)element;
			ret = RGoal.GOALLIFECYCLESTATE_ACTIVE.equals(rgoal.getLifecycleState())
				&& RGoal.GOALPROCESSINGSTATE_INPROCESS.equals(rgoal.getProcessingState());
			// todo: hack, how to avoid side effect
			if(!ret)
				rplan.abortPlan();
		}
			
//		if(!ret)
//			System.out.println("not valid: "+this+" "+element);
		
		return ret;
	}
	
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public IFuture<Void> execute(final IInternalAccess ia)
	{
//		System.out.println("execute candidate: "+rplan);
		
		// problem plan context for steps needed that allows to know
		// when a plan has completed 
		
		Object element = rplan.getReason();
		if(element instanceof RGoal)
		{
			RGoal rgoal = (RGoal)element;
			rgoal.setChildPlan(rplan);
		}
		rplan.setLifecycleState(RPlan.PLANLIFECYCLESTATE_BODY);
		IPlanBody body = rplan.getBody();
		return body.executePlanStep();
	}
}
