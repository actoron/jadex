package jadex.bdiv3.actions;

import jadex.bdiv3.features.impl.IInternalBDIAgentFeature;
import jadex.bdiv3.runtime.impl.IPlanBody;
import jadex.bdiv3.runtime.impl.RGoal;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bdiv3.runtime.impl.RProcessableElement;
import jadex.bridge.IConditionalComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

// todo: use IPlan (and plan executor abstract to be able to execute plans as subcomponents)
// todo: allow using multi-step plans

/**
 *  Action for executing a plan step. 
 */
public class ExecutePlanStepAction implements IConditionalComponentStep<Void>
{
	/** The rplans for plan threads. */
	public static final ThreadLocal<RPlan>	RPLANS	= new ThreadLocal<RPlan>();
	
	/** The plan. */
	protected RPlan rplan;
	
//	/** The resume command. */
//	protected ICommand<Boolean> rescom;
	
	/**
	 *  Create a new action.
	 */
	public ExecutePlanStepAction(RPlan rplan)
	{
//		this(rplan, null);
		this.rplan = rplan;
	}
	
//	/**
//	 *  Create a new action.
//	 */
//	public ExecutePlanStepAction(RPlan rplan, ICommand<Boolean> rescom)
//	{
////		System.err.println("epsa: "+rplan);
////		Thread.dumpStack();
////		this.element = element;
//		this.rplan = rplan;
//		this.rescom = rescom;
//	}
	
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
//		if(!isReasonValid())
//			System.out.println("executing invalid candidate: "+rplan);
		
//		if(rplan.toString().indexOf("Move")!=-1)
//			System.out.println("plan exe: "+rplan);
		
		// problem plan context for steps needed that allows to know
		// when a plan has completed 
		
		Object element = rplan.getReason();
		if(element instanceof RGoal)
		{
			RGoal rgoal = (RGoal)element;
			
//			System.out.println("executing candidate: "+rplan+" "+rgoal.getLifecycleState()+" "+rgoal.getProcessingState());
			
			if(!(RGoal.GoalLifecycleState.ACTIVE.equals(rgoal.getLifecycleState())
				&& RGoal.GoalProcessingState.INPROCESS.equals(rgoal.getProcessingState())))
			{
				// todo: hack, how to avoid side effect
//				rplan.abort();
				return IFuture.DONE;
			}
		}
		
		// Rescom now directly executed 
//		if(RPlan.PlanProcessingState.WAITING.equals(rplan.getProcessingState()))
//		{
//			rescom.execute(null);
////			rplan.continueAfterWait(rescom);
//		}else if
		// A new plan body must only be executed if it hasn't been aborted 
		if(!rplan.isFinishing() && RPlan.PlanLifecycleState.NEW.equals(rplan.getLifecycleState()))
		{
			// Set plan as child of goal
			if(element instanceof RGoal)
			{
				RGoal rgoal = (RGoal)element;
				rgoal.setChildPlan(rplan);
			}
			
//			final BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
			ia.getComponentFeature(IInternalBDIAgentFeature.class).getCapability().addPlan(rplan);
			
			IPlanBody body = rplan.getBody();
			try
			{
				RPLANS.set(rplan);
				body.executePlan().addResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						RPLANS.set(null);
						ia.getComponentFeature(IInternalBDIAgentFeature.class).getCapability().removePlan(rplan);
						Object reason = rplan.getReason();
						if(reason instanceof RProcessableElement)
						{
							((RProcessableElement)reason).planFinished(ia, rplan);
						}
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
		// Only needs to to something for waiting and new plans
		// Should processing state be set back to ready in case the plan is not within a step?
//		else
//		{
//			System.out.println("Plan proc state invalid: "+rplan.getProcessingState()+" "+rplan);
//		}
		
		return IFuture.DONE;
	}
	
	/**
	 *  Get the rplan.
	 */
	public RPlan	getRPlan()
	{
		return rplan;
	}
	
//	/**
//	 * 
//	 */
//	protected boolean isReasonValid()
//	{
//		boolean ret = true;
//		Object element = rplan.getReason();
//		if(element instanceof RGoal)
//		{
//			RGoal rgoal = (RGoal)element;
//			ret = IGoal.GoalLifecycleState.ACTIVE.equals(rgoal.getLifecycleState())
//				&& IGoal.GoalProcessingState.INPROCESS.equals(rgoal.getProcessingState());
//		}
//		return ret;
//	}
}
