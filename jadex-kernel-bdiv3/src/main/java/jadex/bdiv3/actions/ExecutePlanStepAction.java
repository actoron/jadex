package jadex.bdiv3.actions;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.runtime.impl.BDIAgentInterpreter;
import jadex.bdiv3.runtime.impl.IPlanBody;
import jadex.bdiv3.runtime.impl.RGoal;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bdiv3.runtime.impl.RProcessableElement;
import jadex.bridge.IConditionalComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.ICommand;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

// todo: use IPlan (and plan executor abstract to be able to execute plans as subcomponents)
// todo: allow using multi-step plans

/**
 * 
 */
public class ExecutePlanStepAction implements IConditionalComponentStep<Void>
{
	/** The rplans for plan threads. */
	public static final ThreadLocal<RPlan>	RPLANS	= new ThreadLocal<RPlan>();
	
	/** The plan. */
	protected RPlan rplan;
	
	/** The resume command. */
	protected ICommand<Boolean> rescom;
	
	/**
	 *  Create a new action.
	 */
	public ExecutePlanStepAction(RPlan rplan, ICommand<Boolean> rescom)
	{
//		this.element = element;
		this.rplan = rplan;
		this.rescom = rescom;
	}
	
	/**
	 *  Test if the action is valid.
	 *  @return True, if action is valid.
	 */
	public boolean isValid()
	{
		// todo: abort execution
		boolean ret = RPlan.PlanLifecycleState.NEW.equals(rplan.getLifecycleState())
			|| RPlan.PlanLifecycleState.BODY.equals(rplan.getLifecycleState());
		
//		if(ret)
//		{
//			Object element = rplan.getReason();
//			if(element instanceof RGoal)
//			{
//				RGoal rgoal = (RGoal)element;
//				ret = RGoal.GOALLIFECYCLESTATE_ACTIVE.equals(rgoal.getLifecycleState())
//					&& RGoal.GOALPROCESSINGSTATE_INPROCESS.equals(rgoal.getProcessingState());
//				// todo: hack, how to avoid side effect
//				if(!ret)
//					rplan.abort();
//			}
//		}
			
//		if(!ret)
//			System.out.println("not valid: "+rplan);
		
		return ret;
	}
	
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public IFuture<Void> execute(final IInternalAccess ia)
	{
		System.out.println("execute candidate: "+rplan);
		
//		if(rplan.toString().indexOf("Move")!=-1)
//			System.out.println("plan exe: "+rplan);
		
		// problem plan context for steps needed that allows to know
		// when a plan has completed 
		
		Object element = rplan.getReason();
		if(element instanceof RGoal)
		{
			RGoal rgoal = (RGoal)element;
			if(!(RGoal.GoalLifecycleState.ACTIVE.equals(rgoal.getLifecycleState())
				&& RGoal.GoalProcessingState.INPROCESS.equals(rgoal.getProcessingState())) && !rplan.aborted)
			{
				// todo: hack, how to avoid side effect
				rplan.abort();
			}
		}
		
		if(RPlan.PlanProcessingState.WAITING.equals(rplan.getProcessingState()))
		{
			rescom.execute(null);
//			rplan.continueAfterWait(rescom);
		}
		else if(RPlan.PlanLifecycleState.NEW.equals(rplan.getLifecycleState()))
		{
			// Set plan as child of goal
			if(element instanceof RGoal)
			{
				RGoal rgoal = (RGoal)element;
				rgoal.setChildPlan(rplan);
			}
			
			final BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
			ip.getCapability().addPlan(rplan);
			rplan.setLifecycleState(RPlan.PlanLifecycleState.BODY);
			IPlanBody body = rplan.getBody();
			try
			{
				RPLANS.set(rplan);
				body.executePlan().addResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						RPLANS.set(null);
						ip.getCapability().removePlan(rplan);
						Object reason = rplan.getReason();
						if(reason instanceof RProcessableElement)
							((RProcessableElement)reason).planFinished(ia, rplan);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						RPLANS.set(null);
						resultAvailable(null);
					}
				});
			}
			finally
			{
				RPLANS.set(null);
			}
		}
		else
		{
			System.out.println("Plan proc state invalid: "+rplan.getProcessingState());
		}
		
		return IFuture.DONE;
	}
	
	/**
	 *  Get the rplan.
	 */
	public RPlan	getRPlan()
	{
		return rplan;
	}
}
